package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import smjni.jnigen.ExposeToNative;

@ExposeToNative
public class MainActivity extends AppCompatActivity {

    static {
       System.loadLibrary("myapplication");
    }

    private native int nativeFunction(byte b);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nativeFunction((byte)0);
    }
}