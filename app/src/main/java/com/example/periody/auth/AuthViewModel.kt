package com.example.periody.auth

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.periody.model.User
import com.example.periody.profile.ProfileStorageHelper
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    data class AuthState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val isAuthenticated: Boolean = false,
        val currentUser: User? = null,
        val registerSuccess: Boolean = false
    )

    sealed class AuthEvent {
        data class Login(val email: String, val password: String) : AuthEvent()
        data class Register(
            val email: String,
            val username: String,
            val firstName: String?,
            val lastName: String?,
            val phone: String?,
            val address: String?,
            val password: String
        ) : AuthEvent()

        object Logout : AuthEvent()
        object CheckSession : AuthEvent()
    }

    private val supabase = SupabaseProvider.client

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Login -> login(event.email, event.password)
            is AuthEvent.Register -> register(
                email = event.email,
                password = event.password,
                username = event.username,
                firstName = event.firstName,
                lastName = event.lastName,
                phone = event.phone,
                address = event.address
            )
            AuthEvent.Logout -> logout()
            AuthEvent.CheckSession -> checkSession()
        }
    }

    private fun showError(message: String) {
        _state.value = _state.value.copy(
            isLoading = false,
            error = message
        )
    }

    private fun mapRegisterError(e: Exception): String {
        val msg = e.message ?: ""
        return when {
            "duplicate key value violates unique constraint" in msg ->
                "Email sudah terdaftar. Silakan login."
            "Failed to connect" in msg || "timeout" in msg ->
                "Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
            else -> "Registrasi gagal. Silakan coba lagi."
        }
    }

    // ============================================================
    // SANITIZE INPUT
    // ============================================================
    private fun sanitizeInput(
        username: String,
        firstName: String?,
        lastName: String?,
        phone: String?,
        address: String?
    ): Map<String, String> {

        val safeUsername = username.ifBlank { "User" }
        val safeFirst = firstName?.ifBlank { "Nama" } ?: "Nama"
        val safeLast = lastName?.ifBlank { "Lengkap" } ?: "Lengkap"
        val safePhone = phone?.ifBlank { "-" } ?: "-"
        val safeAddress = address?.ifBlank { "-" } ?: "-"
        val fullName = "$safeFirst $safeLast"

        return mapOf(
            "username" to safeUsername,
            "firstName" to safeFirst,
            "lastName" to safeLast,
            "name" to fullName,
            "phone" to safePhone,
            "address" to safeAddress,
            "profile_image_url" to ""
        )
    }

    // ============================================================
    // REGISTER
    // ============================================================
    private fun register(
        email: String,
        password: String,
        username: String,
        firstName: String?,
        lastName: String?,
        phone: String?,
        address: String?
    ) {
        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                error = null,
                registerSuccess = false
            )

            // VALIDASI INPUT
            when {
                email.isBlank() -> return@launch showError("Email wajib diisi.")
                !email.contains("@") -> return@launch showError("Format email tidak valid.")
                password.length < 6 -> return@launch showError("Password minimal 6 karakter.")
                username.isBlank() -> return@launch showError("Username wajib diisi.")
                firstName.isNullOrBlank() -> return@launch showError("Nama depan wajib diisi.")
                lastName.isNullOrBlank() -> return@launch showError("Nama belakang wajib diisi.")
            }

            try {
                // 1) Register ke Auth
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                // 2) Ambil userId dari session
                val session = supabase.auth.currentSessionOrNull()
                    ?: throw Exception("Session tidak ditemukan setelah registrasi")

                val userId = session.user?.id
                    ?: throw Exception("User ID tidak ditemukan setelah registrasi")

                // 3) Bersihkan input
                val cleaned = sanitizeInput(username, firstName, lastName, phone, address)

                // 4) Insert ke tabel users (SESUAI TABEL KAMU)
                supabase.from("users").insert(
                    mapOf(
                        "id" to userId,
                        "email" to email
                    ) + cleaned
                )

                val user = loadUserSync(userId)

                _state.value = AuthState(
                    isAuthenticated = false,
                    currentUser = user,
                    registerSuccess = true
                )

            } catch (e: Exception) {
                val msg = e.message ?: ""

                if ("duplicate key value violates unique constraint" in msg) {
                    _state.value = AuthState(registerSuccess = true)
                    return@launch
                }

                showError(mapRegisterError(e))
            }
        }
    }

    // ============================================================
    // LOGIN
    // ============================================================
    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                val userId = supabase.auth.currentSessionOrNull()?.user?.id
                    ?: throw Exception("Session tidak ditemukan")

                val user = loadUserSync(userId)

                _state.value = AuthState(
                    isAuthenticated = true,
                    currentUser = user
                )

            } catch (e: Exception) {
                showError(e.message ?: "Login gagal")
            }
        }
    }

    // ============================================================
    // CHECK SESSION
    // ============================================================
    fun checkSession() {
        viewModelScope.launch {
            val session = supabase.auth.currentSessionOrNull()
            if (session == null) {
                _state.value = AuthState(isAuthenticated = false)
                return@launch
            }

            val userId = session.user?.id ?: return@launch
            val user = loadUserSync(userId)

            _state.value = AuthState(
                isAuthenticated = true,
                currentUser = user
            )
        }
    }

    // ============================================================
    // LOGOUT
    // ============================================================
    fun logout() {
        viewModelScope.launch {
            _state.value = AuthState()
            try { supabase.auth.clearSession() } catch (_: Exception) {}
            try { supabase.auth.signOut() } catch (_: Exception) {}
            _state.value = AuthState()
        }
    }

    // ============================================================
    // LOAD USER (SYNC)
    // ============================================================
    private suspend fun loadUserSync(userId: String): User {
        return supabase.from("users")
            .select { filter { eq("id", userId) } }
            .decodeSingle<User>()
    }

    // ============================================================
    // LOAD USER (ASYNC)
    // ============================================================
    fun loadUser(userId: String) {
        viewModelScope.launch {
            try {
                val user = loadUserSync(userId)
                _state.value = _state.value.copy(currentUser = user)
            } catch (e: Exception) {
                showError(e.message ?: "Gagal memuat data user")
            }
        }
    }

    // ============================================================
    // UPDATE FOTO PROFIL
    // ============================================================
    fun updateProfileImage(
        context: Context,
        userId: String,
        uri: Uri,
        onDone: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val storage = ProfileStorageHelper(context)
                val imageUrl = storage.uploadProfileImage(uri, userId)

                supabase.from("users").update({
                    set("profile_image_url", imageUrl)
                }) {
                    filter { eq("id", userId) }
                }

                loadUser(userId)
                onDone()

            } catch (e: Exception) {
                showError(e.message ?: "Gagal memperbarui foto profil")
            }
        }
    }

    // ============================================================
    // UPDATE USER DATA
    // ============================================================
    fun updateUserAll(
        username: String,
        name: String?,
        phone: String?,
        address: String?
    ) {
        viewModelScope.launch {
            try {
                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch

                val safeName = name?.ifBlank { "Nama Lengkap" } ?: "Nama Lengkap"
                val safePhone = phone?.ifBlank { "-" } ?: "-"
                val safeAddress = address?.ifBlank { "-" } ?: "-"
                val safeUsername = username.ifBlank { "User" }

                supabase.from("users").update({
                    set("username", safeUsername)
                    set("name", safeName)
                    set("phone", safePhone)
                    set("address", safeAddress)
                }) {
                    filter { eq("id", userId) }
                }

                loadUser(userId)

            } catch (e: Exception) {
                showError(e.message ?: "Gagal memperbarui data")
            }
        }
    }

    // ============================================================
    // UPDATE EMAIL
    // ============================================================
    fun updateEmail(newEmail: String) {
        viewModelScope.launch {
            try {
                supabase.auth.updateUser { email = newEmail }

                val userId = supabase.auth.currentSessionOrNull()?.user?.id ?: return@launch

                supabase.from("users").update({
                    set("email", newEmail)
                }) {
                    filter { eq("id", userId) }
                }

                loadUser(userId)

            } catch (e: Exception) {
                showError(e.message ?: "Gagal memperbarui email")
            }
        }
    }

    // ============================================================
    // UPDATE PASSWORD
    // ============================================================
    fun updatePassword(newPassword: String) {
        viewModelScope.launch {
            try {
                supabase.auth.updateUser { password = newPassword }
            } catch (e: Exception) {
                showError(e.message ?: "Gagal memperbarui password")
            }
        }
    }
}
