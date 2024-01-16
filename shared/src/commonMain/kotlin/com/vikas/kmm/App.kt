package com.vikas.kmm

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi
import androidx.compose.ui.graphics.Color
import com.vikas.kmm.dto.Data
import io.github.aakira.napier.Napier
import io.ktor.client.call.body
import kotlinx.coroutines.launch

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(appDao: AppDao) {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            val coroutineScope = rememberCoroutineScope()
            appDao.insertData()
            coroutineScope.launch {
                val data = appDao.getRemoteDta().body<Data>()
                Napier.v("data : $data")
            }
            Text(text = Greeting().greet())
            Text(text = appDao.getData().toString())
        }
    }
}

