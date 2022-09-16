package com.example.project2_gameapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginFragmentListener, RegistrationFragment.RegistrationFragmentListener, ChatroomsFragment.ChatroomsFragmentListener,
        CreateChatroomFragment.CreateChatroomFragmentListener, ViewChatroomFragment.ViewChatroomFragmentListener, NavigationView.OnNavigationItemSelectedListener, NewGameFragment.NewGameFragmentListener,
        GameLobbyFragment.GameLobbyFragmentListener, GameRoomFragment.GameRoomFragmentListener {

    private static final String TAG = "main activity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
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
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rootView, new GameLobbyFragment(), "game-lobby-fragment")
                    .commit();
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
            case R.id.nav_games:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.rootView, new GameLobbyFragment(), "game-lobby-fragment")
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
    public void selectUserForGame(User user) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, NewGameFragment.newInstance(user), "new-game-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void joinGame(Game game) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, GameRoomFragment.newInstance(game), "new-game-fragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goBackToLobby(String gameID, String winner) {
        Log.d("qq", "goBackToLobby: " + winner);
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Game Over")
                .setMessage(winner + " Wins!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSupportFragmentManager().popBackStack();

                        db.collection("games").document(gameID)
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.d(TAG, "closed win window");
                                        Log.d(TAG, "delete stuff here instead??");
                                    }
                                });
                    }
                });
        b.create().show();
    }
}