package com.ritorno.geoblocks

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class TradeNFT : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trade_nft)

        val mCountDownTimer: CountDownTimer
        var i = 0

        val tradebtn = findViewById<Button>(R.id.trade_btn)

        val mProgressBar: ProgressBar = findViewById<View>(R.id.progressbar) as ProgressBar
        mProgressBar.progress = i
        mCountDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.v("Log_tag", "Tick of Progress$i$millisUntilFinished")
                i++
                mProgressBar.progress = i as Int * 100 / (5000 / 1000)
            }

            override fun onFinish() {
                //Do what you want
                i++
                mProgressBar.progress = 100

                Toast.makeText(applicationContext, "Trade Completed !! NFT minted Succesfully !!", Toast.LENGTH_LONG).show()
                val intent = Intent( applicationContext, MyCollections::class.java)
                startActivity(intent)
                finish()
            }
        }

        tradebtn.setOnClickListener {
            mCountDownTimer.start()
        }

    }
}