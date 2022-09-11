package com.example.project2_gameapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegistrationFragment.RegistrationFragmentListener, MainPageFragment.MainPageFragmentListener {

    private static final String TAG = "main activity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            goToLogin();
        } else {
            goToMainPage();
        }
    }

    @Override
    public void goToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootview, new LoginFragment(), "login-fragment")
                .commit();
    }


    @Override
    public void goToMainPage() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootview, new MainPageFragment(), "main-page-fragment")
                .commit();
    }

    @Override
    public void backToLogin() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goToRegistration() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootview, new RegistrationFragment(), "registration-fragment")
                .addToBackStack(null)
                .commit();
    }
}