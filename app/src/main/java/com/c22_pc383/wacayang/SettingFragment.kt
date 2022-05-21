package com.c22_pc383.wacayang

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : PreferenceFragmentCompat(), IGeneralSetup {
    private lateinit var auth: FirebaseAuth
    private lateinit var accountPref: Preference
    private lateinit var signOutPref: Preference
    private lateinit var signInPref: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey);
        setup()
    }

    override fun setup() {
        auth = Firebase.auth

        val langPref = findPreference<Preference> (resources.getString(R.string.language)) as Preference
        val aboutPref = findPreference<Preference> (resources.getString(R.string.about)) as Preference

        accountPref = findPreference<Preference>(resources.getString(R.string.account)) as Preference
        signInPref = findPreference<Preference>(resources.getString(R.string.sign_in)) as Preference
        signOutPref = findPreference<Preference>(resources.getString(R.string.sign_out)) as Preference

        langPref.setOnPreferenceClickListener {
            changeLanguage()
            true
        }

        aboutPref.setOnPreferenceClickListener {
            toggleAbout()
            true
        }

        accountPref.setOnPreferenceClickListener {
            accountInfo()
            true
        }

        signInPref.setOnPreferenceClickListener {
            signIn()
            true
        }

        signOutPref.setOnPreferenceClickListener {
            signOut()
            true
        }

        toggleSignStatus()
    }

    private fun changeLanguage() = startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))

    private fun toggleAbout() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(resources.getString(R.string.about))
            setMessage(resources.getString(R.string.about_app))
        }.show()
    }

    private fun toggleSignStatus() {
        val hasUser = auth.currentUser != null

        accountPref.isVisible = hasUser
        signOutPref.isVisible = hasUser
        signInPref.isVisible = !hasUser

        if (hasUser) accountPref.title = auth.currentUser?.email.toString()
    }

    private fun accountInfo() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.sign_in_as, auth.currentUser?.email.toString()),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun signIn() {
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun signOut() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(resources.getString(R.string.sign_out))
            setMessage(resources.getString(R.string.confirm_sign_out))
            setNegativeButton("No", null)
            setPositiveButton("Yes") { _, _ ->
                auth.signOut()
                AppPreference(requireContext()).clearToken()
                signIn()
            }
        }.show()
    }
}