package com.example.lab2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {
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
}