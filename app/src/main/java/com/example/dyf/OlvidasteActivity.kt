package com.example.dyf

import OlvidasteScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface

import androidx.compose.ui.Modifier
import com.example.dyf.data.UserPreferences

import com.example.dyf.ui.theme.DyfTheme

class OlvidasteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userPreferences = UserPreferences(context = this)
        setContent {
            DyfTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    OlvidasteScreen(userPreferences = userPreferences)
                }
            }
        }
    }
}

