package com.jundapp.fingergame

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {


    lateinit var mAdView : AdView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initListener()

        MobileAds.initialize(this){}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

    }

    private fun initListener() {
        mainLokal.setOnClickListener({
            val i = Intent(this, MainActivity::class.java)
            i.putExtra("game mode", "local")
            startActivity(i)
        })


        buatRuangan.setOnClickListener({
            val i = Intent(this, CreateRoomActivity::class.java)
            startActivity(i)
        })


        masukRuangan.setOnClickListener({
            val i = Intent(this, JoinRoomActivity::class.java)
            startActivity(i)
        })
    }
}