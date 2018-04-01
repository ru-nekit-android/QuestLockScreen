package ru.nekit.android.window

interface WindowListener {

    fun onWindowOpen(window: Window)

    fun onWindowOpened(window: Window)

    fun onWindowClose(window: Window)

    fun onWindowClosed(window: Window)
}
