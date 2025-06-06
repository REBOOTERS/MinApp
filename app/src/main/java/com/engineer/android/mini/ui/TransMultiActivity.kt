package com.engineer.android.mini.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityMd3Binding
import com.engineer.android.mini.ui.fragments.MainViewModel

class TransMultiActivity : AppCompatActivity() {
    private val TAG = "MD3Activity"
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMd3Binding
    private val viewModel: MainViewModel by viewModels()

    private val pickPictureCallback =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { result ->
            if (result == null) Log.e(TAG, "Invalid input image Uri.")
            Log.d(TAG, "called with: result = $result")
            if (result != null) {
                viewModel.setInputBitmap(this, result)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMd3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_md3) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        binding.fab.setOnClickListener { view ->
            pickPictureCallback.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_md3)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}