package com.ritorno.geoblocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MyCollections : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_collections)

        val fragmentmanager = supportFragmentManager
        val fragmentTransaction = fragmentmanager.beginTransaction()
        fragmentTransaction.replace(R.id.Home_Activity_Fragment_Container, MyNftsFragment())
        fragmentTransaction.commit()
    }
}