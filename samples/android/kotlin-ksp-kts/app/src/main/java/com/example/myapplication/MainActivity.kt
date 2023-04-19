package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import smjni.jnigen.ExposeToNative

@ExposeToNative
class MainActivity : AppCompatActivity() {

    companion object {
        init {
            System.loadLibrary("myapplication")
        }
    }

    private external fun nativeFunction(b: Byte): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nativeFunction(0)
    }
}