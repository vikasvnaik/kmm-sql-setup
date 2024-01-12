package com.vikas.kmm

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(appDao: AppDao) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            appDao.insertData()
            Text(text = Greeting().greet())
            Text(text = appDao.getData().toString())
        }
    }
}

