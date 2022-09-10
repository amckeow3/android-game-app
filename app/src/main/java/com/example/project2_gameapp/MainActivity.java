package com.example.project2_gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void login() {
        //TODO: go to main app page after login
    }

    @Override
    public void goToRegistration() {
        //TODO: go to main app page after registration
    }
}