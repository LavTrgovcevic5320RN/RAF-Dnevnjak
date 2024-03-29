package com.example.myapplication.view.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.model.Day;
import com.example.myapplication.view.viewpager.PagerAdapter;
import com.example.myapplication.viewmodels.SplashViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private SplashViewModel splashViewModel;
    private Day selectedDay;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return false;
        });
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        initLogIn();
        initViewPager();
        initNavigation();
    }

    private void initLogIn() {
        setTitle(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + LocalDate.now().getYear() + ".");
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean("is_logged_in", false)) {
            Intent intent = new Intent(this, LogInActivity.class);
            startActivity(intent);
        }
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
    }

    @SuppressLint("NonConstantResourceId")
    private void initNavigation() {
        ((BottomNavigationView)findViewById(R.id.bottomNavigation)).setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                // setCurrentItem metoda viewPager samo obavesti koji je Item trenutno aktivan i onda metoda getItem u adapteru setuje odredjeni fragment za tu poziciju
                case R.id.menu_calendar:
                    if(selectedDay != null){
                        setTitle(selectedDay.getLocalDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + selectedDay.getLocalDate().getYear() + ".");
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("title", selectedDay.getLocalDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + selectedDay.getLocalDate().getYear() + ".");
                        editor.apply();
                    }
                    viewPager.setCurrentItem(PagerAdapter.FRAGMENT_1, false);break;
                case R.id.menu_profile:
                    viewPager.setCurrentItem(PagerAdapter.FRAGMENT_3, false); break;
            }
            return true;
        });
    }


    public void openDay(Day day) {
        this.selectedDay = day;
        ((BottomNavigationView)findViewById(R.id.bottomNavigation)).setSelectedItemId(R.id.menu_day_schedule);

        setTitle(selectedDay.getLocalDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + selectedDay.getLocalDate().getDayOfMonth() + ". " + selectedDay.getLocalDate().getYear());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("title", selectedDay.getLocalDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + selectedDay.getLocalDate().getDayOfMonth() + ". " + selectedDay.getLocalDate().getYear());
        editor.apply();

        viewPager.setCurrentItem(PagerAdapter.FRAGMENT_2, false);

    }
}