package com.example.periody

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.periody.navigation.AppNavHost
import com.example.periody.ui.theme.PeriodyTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PeriodyTheme(
                dynamicColor = false
            ) {
                AppNavHost()
            }
        }

    }
    }

