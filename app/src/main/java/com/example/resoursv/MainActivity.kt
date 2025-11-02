package com.example.resoursv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.resoursv.navigation.NavGraph
import com.example.resoursv.ui.theme.ResourSVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ResourSVTheme {
                NavGraph()
            }
        }
    }
}