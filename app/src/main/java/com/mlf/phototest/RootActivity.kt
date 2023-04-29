package com.mlf.phototest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Surface
import com.mlf.phototest.nav.RootNavigation
import com.mlf.phototest.theme.AppTheme

class RootActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface {
                    RootNavigation()
                }
            }
        }
    }
}

