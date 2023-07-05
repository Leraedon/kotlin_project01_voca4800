package com.example.vocaapplication

import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat

class SettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)
        val colorPreference: ListPreference? = findPreference("color")
        val timerPreference: ListPreference? = findPreference("timer")

        colorPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        timerPreference?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        colorPreference?.setOnPreferenceChangeListener { preference, newValue ->
            Log.d("Leraedon", "preference key:${preference.key}, newValue: ${newValue}")
            true
        }
    }
}