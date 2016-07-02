package com.ssn.cobble.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ssn.cobble.kit.Res;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Res.resources();//
    }
}
