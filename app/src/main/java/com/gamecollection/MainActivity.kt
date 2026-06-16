package com.gamecollection

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gamecollection.di.LocalAppContainer
import com.gamecollection.navigation.GameCollectionNavHost
import com.gamecollection.ui.theme.GameCollectionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as GameCollectionApplication).container

        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                GameCollectionTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        val navController = rememberNavController()
                        GameCollectionNavHost(navController = navController)
                    }
                }
            }
        }
    }
}
