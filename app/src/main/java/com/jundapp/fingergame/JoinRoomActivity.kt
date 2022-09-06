package com.jundapp.fingergame

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join_room.*

class JoinRoomActivity : AppCompatActivity() {

    lateinit var mAdView : AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room)

        MobileAds.initialize(this){}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        btn_join.setOnClickListener({joinRoom()})
        loadUri()

    }

    private fun joinRoom() {
        val database = Firebase.database
        val myRef = database.getReference(et_roomcode.text.toString())

        myRef.child("other").setValue("Easta")

        myRef.addListenerForSingleValueEvent (object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(dataSnapshot.child("active").getValue<String>()!=null){
                    // TODO open play screen
                    val i = Intent(this@JoinRoomActivity, MainActivity::class.java)
                    i.putExtra("game mode", et_roomcode.text.toString())
                    i.putExtra("position", "other")
                    startActivity(i)
                }else{
                    Toast.makeText(this@JoinRoomActivity,"Room tidak ada!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@JoinRoomActivity, "Terjadi kesalahan!", Toast.LENGTH_LONG).show()
            }
        })

    }

    private fun loadUri() {
        val uri = intent.data
        if(uri != null){
            val params = uri.pathSegments
            val id = params.get(params.size - 1)
            et_roomcode.setText(id)
        }
    }
}