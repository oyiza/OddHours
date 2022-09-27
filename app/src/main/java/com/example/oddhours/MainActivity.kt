package com.example.oddhours

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.oddhours.database.DatabaseHelper
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkTheme) //when dark mode is enabled, we use the dark theme
        } else {
            setTheme(R.style.LightTheme)  //default app theme
        }

        /**
         * Calling initDatabase function to create the DatabaseHelper singleton
         */
        DatabaseHelper.initDatabase(this)

        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottomNv)

        val navController = findNavController(R.id.navHostFragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigationHomeFragment, R.id.navigationAddJobFragment, R.id.navigationShiftsFragment, R.id.navigationChartsFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}