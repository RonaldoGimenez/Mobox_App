package com.example.mobox_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mobox_app.ui.navigation.AppNavigationGraph
import com.example.mobox_app.ui.theme.MoboxTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoboxTheme {

                AppNavigationGraph()
            }
        }
    }
}