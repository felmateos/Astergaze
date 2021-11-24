package com.example.authenticatorapp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.authenticatorapp.ui.messages.MessagesFragment;
import com.example.authenticatorapp.ui.chats.ChatsFragment;
import com.example.authenticatorapp.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class BottomActivity extends AppCompatActivity {

    Button chatBtn, profBtn, contBtn;
    ImageView contIcon, profIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom);

        int statusBarColor = android.graphics.Color.parseColor("#20111111");
        int navBarColor = android.graphics.Color.parseColor("#20111111");
        BottomActivity.setWindowStatusNav(getWindow(), statusBarColor, navBarColor);

        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        chatBtn = findViewById(R.id.chat_btn);
        profBtn = findViewById(R.id.prof_btn);
        contBtn = findViewById(R.id.cont_btn);
        contIcon = findViewById(R.id.cont_icon);
        profIcon = findViewById(R.id.prof_icon);
        contIcon.setImageResource(R.drawable.ic_person_24);
        chatBtn.setTextColor(getResources().getColor(R.color.colorPrimary));


        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ChatsFragment()).commit();
        }

         chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = new ChatsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                selectedFragment).commit();
                profBtn.setTextColor(Color.parseColor("#777777"));
                chatBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                contIcon.setImageResource(R.drawable.ic_person_24);
                profIcon.setImageResource(R.drawable.ic_house_outline);
            }
        });

        profBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = new NotificationsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                chatBtn.setTextColor(Color.parseColor("#777777"));
                profBtn.setTextColor(getResources().getColor(R.color.colorPrimary));
                contIcon.setImageResource(R.drawable.ic_person_outline_white);
                profIcon.setImageResource(R.drawable.ic_house);
            }
        });
        contBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment selectedFragment = null;
                selectedFragment = new MessagesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        selectedFragment).commit();
                chatBtn.setTextColor(Color.parseColor("#777777"));
                profBtn.setTextColor(Color.parseColor("#777777"));
                contIcon.setImageResource(R.drawable.ic_person_outline_white);
                profIcon.setImageResource(R.drawable.ic_house_outline);
            }
        });

    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            contIcon.setScaleX(1.3f);
                            contIcon.setScaleY(1.3f);
                            selectedFragment = new ChatsFragment();
                            break;
                        case R.id.nav_favorites:
                            contIcon.setScaleX(1f);
                            contIcon.setScaleY(1f);
                            selectedFragment = new MessagesFragment();
                            break;
                        case R.id.nav_search:
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };
    public static void setWindowStatusNav(android.view.Window window, int statusbarColor, int navbarColor) {

        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH) {
            window.getAttributes().flags |= flags;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            window.getDecorView().setSystemUiVisibility(uiVisibility);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getAttributes().flags &= ~flags;

            window.setStatusBarColor(statusbarColor);
            window.setNavigationBarColor(navbarColor);
        }
    }
}