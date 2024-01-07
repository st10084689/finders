package com.example.finders

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private lateinit var HomeButton: RelativeLayout
    private lateinit var AccountButton: RelativeLayout
    private lateinit var myBirdButton: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)


        HomeButton = findViewById(R.id.homeRelative)
        AccountButton = findViewById(R.id.accountRelative)
        myBirdButton = findViewById(R.id.myBirdRelative)

        HomeButton.setOnClickListener {
            loadFragment(HomeFragment())
        }

//        AccountButton.setOnClickListener {
//            loadFragment(AccountFragment())
//        }

        myBirdButton.setOnClickListener {
            loadFragment(MyBirdsFragment())
        }

        loadFragment(HomeFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}