package com.example.lab3

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            loadFragment(InputFragment(), R.id.inputFragmentContainer)
        }
    }

    private fun loadFragment(fragment: Fragment, containerId: Int) {
        supportFragmentManager.commit {
            replace(containerId, fragment)
        }
    }

    fun showResult(result: String) {
        val resultFragment = ResultFragment.newInstance(result)
        supportFragmentManager.commit {
            replace(R.id.resultFragmentContainer, resultFragment)
            addToBackStack(null)
        }
    }

    fun clearInput() {
        val inputFragment = supportFragmentManager.findFragmentById(R.id.inputFragmentContainer) as? InputFragment
        inputFragment?.clearInput()
    }

    fun savePassword(password: String): Boolean {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
            val entry = "$timestamp: $password\n"

            val file = File(filesDir, "passwords.txt")
            FileOutputStream(file, true).use {
                it.write(entry.toByteArray())
            }
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    fun openStoredData() {
        val intent = Intent(this, StoredDataActivity::class.java)
        startActivity(intent)
    }
}