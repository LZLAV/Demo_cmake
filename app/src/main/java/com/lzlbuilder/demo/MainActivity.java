package com.lzlbuilder.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.lzlbuilder.demo.natives.NativeTest;

public class MainActivity extends AppCompatActivity {

    // 加载动态库
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(NativeTest.stringFromJNIStatic());
    }

}