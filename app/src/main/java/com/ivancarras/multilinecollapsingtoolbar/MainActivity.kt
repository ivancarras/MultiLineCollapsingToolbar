package com.ivancarras.multilinecollapsingtoolbar

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.ivancarras.multilinecollapsingtoolbar.databinding.ActivityMainBinding

class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpToolbarConfiguration()
        addContentView()
    }

    private fun setUpToolbarConfiguration() {
        binding.toolbar.setTitle("Multiline collapsing toolbar demo")

        binding.toolbar.navigationIconOnClick = {
            onBackPressed()
        }
    }

    private fun addContentView() {
        supportFragmentManager.beginTransaction()
            .replace(
                binding.toolbar.fragmentContainerView.id,
                SimpleFragment.createInstance(R.layout.fragment_multiline_collapsing_toolbar_content)
            )
            .commit()
    }
}