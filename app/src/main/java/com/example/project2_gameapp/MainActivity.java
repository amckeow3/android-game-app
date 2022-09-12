package com.example.project2_gameapp;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegistrationFragment.RegistrationFragmentListener, ChatroomsFragment.ChatroomsFragmentListener,
        CreateChatroomFragment.CreateChatroomFragmentListener, ViewChatroomFragment.ViewChatroomFragmentListener, NavigationView.OnNavigationItemSelectedListener, NewGameFragment.NewGameFragmentListener {

    private static final String TAG = "main activity";
    private FirebaseAuth mAuth;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.navDrawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            goToLogin();
        } else {
            goToChatrooms();
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_chatrooms:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rootView, new ChatroomsFragment(), "chatrooms-fragment")
                        .commit();
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                goToLogin();
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void goToLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment(), "login-fragment")
                .commit();
    }

    @Override
    public void createNewChatroom() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateChatroomFragment(), "create-chatroom-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void openSelectedChatroom(Chatroom chatroom) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ViewChatroomFragment.newInstance(chatroom), "view-chatroom-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void backToLogin() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void goToChatrooms() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new ChatroomsFragment(), "chatrooms-fragment")
                .commit();
    }

    @Override
    public void goToRegistration() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new RegistrationFragment(), "registration-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void leaveChatroom() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void newGame() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new NewGameFragment(), "new-game-fragment")
                .addToBackStack(null)
                .commit();
    }
}