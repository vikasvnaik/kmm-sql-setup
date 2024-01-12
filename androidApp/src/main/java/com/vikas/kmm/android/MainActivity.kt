package com.vikas.kmm.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.vikas.kmm.App
import com.vikas.kmm.AppDao
import com.vikas.kmm.DatabaseDriverFactory
import com.vikas.kmm.MainView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appDao = AppDao(DatabaseDriverFactory(applicationContext))
        setContent {
            MainView(appDao)
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        //App()
    }
}
