package com.vikas.kmm

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val appDao = AppDao(DatabaseDriverFactory())
    App(appDao)
}
