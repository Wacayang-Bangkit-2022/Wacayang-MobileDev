package com.c22_pc383.wacayang

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.c22_pc383.wacayang.databinding.ActivityMainBinding
import com.c22_pc383.wacayang.helper.IGeneralSetup

class MainActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setup()
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setup() {
        val navCtrl = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(
            navCtrl,
            AppBarConfiguration.Builder(
                setOf(
                    R.id.home_fragment,
                    R.id.favorite_fragment,
                    R.id.setting_fragment
                )
            ).build()
        )
        binding.bottomNav.setupWithNavController(navCtrl)
    }
}