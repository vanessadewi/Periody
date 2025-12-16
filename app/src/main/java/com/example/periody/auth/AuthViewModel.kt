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

            try {
                // 1) Register ke Auth
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }

                val userId = supabase.auth.currentSessionOrNull()?.user?.id
                    ?: throw Exception("User ID tidak ditemukan setelah registrasi")

                val fullName = listOfNotNull(firstName, lastName)
                    .joinToString(" ")
                    .ifBlank { "" }

                // 2) Insert ke tabel users
                supabase.from("users").insert(
                    mapOf(
                        "id" to userId,
                        "email" to email,
                        "username" to username,
                        "first_name" to firstName,
                        "last_name" to lastName,
                        "name" to fullName,
                        "phone" to phone,
                        "address" to address,
                        "profile_image_url" to null
                    )
                )

                val user = loadUserSync(userId)

                _state.value = AuthState(
                    isAuthenticated = false,
                    currentUser = user,
                    registerSuccess = true
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registrasi gagal"
                )
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
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login gagal"
                )
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

            try {
                supabase.auth.clearSession()
            } catch (_: Exception) {}

            try {
                supabase.auth.signOut()
            } catch (_: Exception) {}

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
                _state.value = _state.value.copy(error = e.message)
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
                _state.value = _state.value.copy(error = e.message)
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

                supabase.from("users").update({
                    set("username", username)
                    set("name", name ?: "")
                    set("phone", phone ?: "")
                    set("address", address ?: "")
                }) {
                    filter { eq("id", userId) }
                }

                loadUser(userId)

            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
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
                _state.value = _state.value.copy(error = e.message)
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
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }
}
