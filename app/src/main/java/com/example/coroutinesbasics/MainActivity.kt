package com.example.coroutinesbasics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.coroutinesbasics.navigation.NavigationRoot
import com.example.coroutinesbasics.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            BasicsAppLayoutContent(windowSize = windowSizeClass)
        }
    }

    @Composable
    fun BasicsAppLayoutContent(
        windowSize: WindowSizeClass? = null
    ) {
        AppTheme {
            Scaffold { innerPadding ->
                val modifier = Modifier.padding(innerPadding)
                NavigationRoot(modifier = modifier)
            }
        }
    }
}