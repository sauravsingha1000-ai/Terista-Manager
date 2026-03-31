// ui/settings/SettingsFragment.kt
package com.terista.manager.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.terista.manager.R
import com.terista.manager.databinding.FragmentSettingsBinding

class SettingsFragment : PreferenceFragmentCompat() {
    
    private lateinit var prefs: SharedPreferences
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        
        setupPreferences()
    }
    
    private fun setupPreferences() {
        // Theme preference
        findPreference<androidx.preference.ListPreference>("theme")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                when (newValue as String) {
                    "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                true
            }
        }
        
        // Show hidden files
        findPreference<androidx.preference.SwitchPreferenceCompat>("show_hidden")?.apply {
            isChecked = prefs.getBoolean("show_hidden", false)
            setOnPreferenceChangeListener { _, newValue ->
                prefs.edit { putBoolean("show_hidden", newValue as Boolean) }
                true
            }
        }
        
        // Confirm delete
        findPreference<androidx.preference.SwitchPreferenceCompat>("confirm_delete")?.apply {
            isChecked = prefs.getBoolean("confirm_delete", true)
            setOnPreferenceChangeListener { _, newValue ->
                prefs.edit { putBoolean("confirm_delete", newValue as Boolean) }
                true
            }
        }
        
        // Cache cleaner (placeholder)
        findPreference<androidx.preference.Preference>("cache_cleaner")?.setOnPreferenceClickListener {
            // Implement cache cleaning
            true
        }
    }
}