package com.quangduy.chatapp.ui.activity.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quangduy.chatapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var token: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // Khởi tạo navController từ NavHostFragment
////                val navController = (supportFragmentManager
////                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
////                    .navController
//
//                if (supportFragmentManager.backStackEntryCount > 0) {
//                    isEnabled = false
//                    onBackPressedDispatcher.onBackPressed()
//                } else {
//                    if (navController.currentDestination?.id == R.id.homeFragment) {
//                        moveTaskToBack(true)
//                    } else {
//                        isEnabled = false
//                        onBackPressedDispatcher.onBackPressed()
//                    }
//                }
//            }
//        })


    }

}