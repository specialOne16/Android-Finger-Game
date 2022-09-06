package com.jundapp.fingergame

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_room.*

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd
    private lateinit var listener : ValueEventListener
    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room)

        MobileAds.initialize(this){}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        et_roomcode.setText(generateRandomPassword())

        mulai.setOnClickListener({
            createRoom()
            if (mInterstitialAd.isLoaded) mInterstitialAd.show()
        })
        share.setOnClickListener({ shareRoom() })

    }

    fun generateRandomPassword(): String {
        val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var passWord = ""
        for (i in 0..5) {
            passWord += chars[Math.floor(Math.random() * chars.length).toInt()]
        }
        return passWord
    }

    private fun createRoom() {
        Toast.makeText(this, "Menunggu lawan...", Toast.LENGTH_LONG).show()

        val database = Firebase.database
        val myRef = database.getReference(et_roomcode.text.toString())

        myRef.child("self").setValue("your name")
        myRef.child("self-kanan").setValue(1)
        myRef.child("self-kiri").setValue(1)
        myRef.child("other-kanan").setValue(1)
        myRef.child("other-kiri").setValue(1)
        myRef.child("active").setValue("null")

        listener = myRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@CreateRoomActivity, "Terjadi kesalahan!", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                val other = p0.child("other").value
                if (other != null) {
                    // TODO open play screen
                    val i = Intent(this@CreateRoomActivity, MainActivity::class.java)
                    i.putExtra("game mode", et_roomcode.text.toString())
                    i.putExtra("position", "self")
                    startActivity(i)
                    myRef.removeEventListener(listener)
                }
            }
        })
    }

    fun shareRoom() {
        if (et_roomcode.text.toString().contains(" ")) {
            Toast.makeText(this, "Tidak bisa bagikan kode yang mengandung spasi", Toast.LENGTH_LONG)
                .show()
        } else {
            var message =
                "Ayo main finger game denganku!!! Download gamenya dan klik link dibawah untuk main\n\n\nfingergame.id/v1/"

            message += et_roomcode.text.toString()

            message += "\n\nDownload fingergame disini : https://drive.google.com/drive/folders/1xcwL90Oz61yXpyCvwLKpvKQnCTOEGYiN?usp=sharing"
            val shareBody = message
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Ayo main finger game denganku!!!")
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Bagikan dengan.."))
        }
    }
}