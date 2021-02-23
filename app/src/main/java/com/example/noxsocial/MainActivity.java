package com.example.noxsocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fragments.AddFragment;
import fragments.HomeFragment;
import fragments.ProfileFragment;
import app.*;
import fragments.SettingsFragment;

//https://github.com/gauravk95/bubble-navigation
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView top_navigation_constraint ;

    ProfileFragment profileFragment = new ProfileFragment() ;
    SettingsFragment settingsFragment = new SettingsFragment() ;
    HomeFragment homeFragment = new HomeFragment() ;
    AddFragment addFragment = new AddFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){

        top_navigation_constraint = findViewById(R.id.top_navigation_constraint);
        top_navigation_constraint.setOnNavigationItemSelectedListener(this);
        top_navigation_constraint.setSelectedItemId(R.id.home);
    }



    private void openFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction() ;
        fragmentTransaction.replace(R.id.container , fragment) ;
        fragmentTransaction.commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()){
            case R.id.profile:{

                openFragment(profileFragment);
                break;
            }
            case R.id.settings:{

                openFragment(settingsFragment);
                break;
            }
            case R.id.home:{
                openFragment(homeFragment);
                break;
            }
            case R.id.add:{
                openFragment(addFragment);
                break;
            }
        }
        return true ;
    }
}