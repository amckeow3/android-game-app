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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootview, new MainPageFragment(), "main-page-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToRegistration() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootview, new RegistrationFragment(), "registration-fragment")
                .addToBackStack(null)
                .commit();
    }
}