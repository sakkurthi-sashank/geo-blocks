package com.ritorno.geoblocks

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar
import io.metamask.androidsdk.DappMetadata
import io.metamask.androidsdk.Ethereum
import io.metamask.androidsdk.EthereumMethod
import io.metamask.androidsdk.EthereumRequest
import io.metamask.androidsdk.Logger
import io.metamask.androidsdk.RequestError
import io.metamask.androidsdk.Result
import io.metamask.androidsdk.SDKOptions

open class SplashActivity : Activity() {
    private val LOCATION_PERMISSION = 1
    var permission_local = false
    var sp: SharedPreferences? = null
    var btn: Button? = null
    var cv:CardView?=null
    var pgBar: GoogleProgressBar? = null
    var pg_text:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)




        sp = this.getSharedPreferences("wallet", MODE_PRIVATE)

        btn = findViewById(R.id.connect_wallet)
        cv = this.findViewById<CardView>(R.id.card_view_splash)
        pgBar = this.findViewById(R.id.progress_loader_splash)
        pg_text = this.findViewById(R.id.splash_text_2)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            ), LOCATION_PERMISSION
        )
        else
            OpenNextScreen()




        val btn = this.findViewById<Button>(R.id.connect_wallet)

        btn.setOnClickListener {

            val dappMetadata = DappMetadata("Droid Dapp", "https://droiddapp.com")
            val infuraAPIKey = "1234567890" // We use Infura API for read-only RPCs for a seamless user experience
            val ethereum = Ethereum(this, dappMetadata, SDKOptions(infuraAPIKey))

            ethereum.connect() { it ->
                when (it) {
                    is io.metamask.androidsdk.Result.Error -> {
                        Logger.log("Ethereum connection error: ${it.error.message}")
                    }
                    is io.metamask.androidsdk.Result.Success.Item -> {
                        Logger.log("Ethereum connection result: ${it.value}")
                        val editor = sp?.edit()
                        editor?.putString("address", it.value)
                        editor?.apply()

                        val i = Intent(baseContext, MapActivity::class.java)
                        startActivity(i)
                        finish()
                    }

                    else -> {}
                }
            }






        }
    }



     fun OpenNextScreen() {

            val walletadress :String? = sp!!.getString("address", "").toString()

            if (walletadress!!.isNullOrEmpty() || walletadress.isNullOrBlank()) {
                cv!!.visibility = View.VISIBLE
            } else {
                pgBar!!.visibility = View.VISIBLE
                pg_text!!.visibility = View.VISIBLE
                val handler = Handler()
                handler.postDelayed({
                    val i = Intent(this, MapActivity::class.java)
                    startActivity(i)
                    this.finish()
                }, 1500)
            }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    permission_local = true
                    OpenNextScreen()
                } else {
                    Toast.makeText(this, "Permission Required", Toast.LENGTH_SHORT).show()
                    permission_local = true
                    OpenNextScreen()
                }
            }
        }
    }
}