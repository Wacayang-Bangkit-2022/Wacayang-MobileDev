package com.c22_pc383.wacayang

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.c22_pc383.wacayang.helper.IGeneralSetup

class SettingFragment : PreferenceFragmentCompat(), IGeneralSetup {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey);
        setup()
    }

    override fun setup() {
        val langPref = findPreference<Preference> (resources.getString(R.string.language)) as Preference
        val aboutPref = findPreference<Preference> (resources.getString(R.string.about)) as Preference

        langPref.setOnPreferenceClickListener {
            changeLanguage()
            true
        }

        aboutPref.setOnPreferenceClickListener {
            toggleAbout()
            true
        }
    }

    private fun changeLanguage() = startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))

    private fun toggleAbout() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(resources.getString(R.string.about))
            setMessage(getString(R.string.about_app))
        }.show()
    }
}