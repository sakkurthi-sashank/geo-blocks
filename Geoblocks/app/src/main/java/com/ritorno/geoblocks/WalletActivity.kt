package com.ritorno.geoblocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class WalletActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        val fragmentmanager = supportFragmentManager
        val fragmentTransaction = fragmentmanager.beginTransaction()
        fragmentTransaction.replace(R.id.Home_Activity_Fragment_Container, WalletFragment())
        fragmentTransaction.commit()
    }
}