package org.example.project

import android.content.Context

object ContextProvider {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun get(): Context = appContext
}