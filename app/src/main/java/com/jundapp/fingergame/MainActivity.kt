package com.jundapp.fingergame

import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TANGAN_KANAN = 1
    val TANGAN_KANAN_MUSUH = 2
    val TANGAN_KIRI = 3
    val TANGAN_KIRI_MUSUH = 4
    val TANGAN_KOSONG = 0

    val GILIRAN_KAMU = 0
    val GILIRAN_MUSUH = 1

    private var activeHand = TANGAN_KOSONG
    private var val_tangan = intArrayOf(1, 1, 1, 1, 1)

    private var giliran = GILIRAN_MUSUH

    private lateinit var own : String
    private lateinit var gameMode : String

    private lateinit var database : FirebaseDatabase
    private lateinit var myRef : DatabaseReference
    private lateinit var listener : ValueEventListener
    lateinit var mInterstitialAd : InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-2749371619132674/5040984905"
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        if(intent.getStringExtra("position") != null)
        own = intent.getStringExtra("position")
        gameMode = intent.getStringExtra("game mode")

        setListener()
        if (gameMode.equals("local")){
            turn_kamu.visibility = INVISIBLE
        }else{
            database = Firebase.database
            myRef = database.getReference(gameMode)

            turn_musuh.visibility = INVISIBLE

            if(own.equals("other")){
                giliran = GILIRAN_MUSUH
                turn_kamu.text = "giliran musuh"
            }else{
                giliran = GILIRAN_KAMU
            }

            listener = myRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(own.equals(p0.child("active").value)){
                        for (i in (1..4)){
                            val_tangan[i] = Integer.parseInt(p0.child(childName(i)).value.toString())
                            Log.e("val" + i + "hehe ",val_tangan[i].toString())
                        }
                        updateImage()
                        updateAvailability()
                    }
                }

            })
        }
    }

    override fun onStop() {
        super.onStop()
        myRef.removeEventListener(listener)
    }
    override fun onDestroy() {
        super.onDestroy()
        myRef.removeEventListener(listener)
    }

    private fun childName(tangan : Int) : String{
        if(own.equals("self"))
            when(tangan){
                TANGAN_KIRI -> return "self-kiri"
                TANGAN_KANAN -> return "self-kanan"
                TANGAN_KANAN_MUSUH -> return "other-kanan"
                TANGAN_KIRI_MUSUH -> return "other-kiri"
            }
        else
            when(tangan){
                TANGAN_KIRI -> return "other-kiri"
                TANGAN_KANAN -> return "other-kanan"
                TANGAN_KANAN_MUSUH -> return "self-kanan"
                TANGAN_KIRI_MUSUH -> return "self-kiri"
            }
        return ""
    }

    private fun setListener() {
        tangan_kanan.setOnClickListener {
            if (activeHand.equals(TANGAN_KANAN)) {
                activeHand = TANGAN_KOSONG
                tangan_kanan.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
            } else if (activeHand.equals(TANGAN_KOSONG) && giliran.equals(GILIRAN_KAMU)) {
                activeHand = TANGAN_KANAN
                tangan_kanan.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
            } else if (activeHand.equals(TANGAN_KIRI)) {
                move(TANGAN_KANAN)
            } else if (activeHand.equals(TANGAN_KANAN_MUSUH) || activeHand.equals(TANGAN_KIRI_MUSUH)) {
                doing(activeHand, TANGAN_KANAN)
            }
        }
        tangan_kiri.setOnClickListener {
            if (activeHand.equals(TANGAN_KIRI)) {
                activeHand = TANGAN_KOSONG
                tangan_kiri.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
            } else if (activeHand.equals(TANGAN_KOSONG) && giliran.equals(GILIRAN_KAMU)) {
                activeHand = TANGAN_KIRI
                tangan_kiri.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
            } else if (activeHand.equals(TANGAN_KANAN)) {
                move(TANGAN_KIRI)
            } else if (activeHand.equals(TANGAN_KANAN_MUSUH) || activeHand.equals(TANGAN_KIRI_MUSUH)) {
                doing(activeHand, TANGAN_KIRI)
            }
        }
        tangan_kanan_musuh.setOnClickListener {
            if (activeHand.equals(TANGAN_KANAN_MUSUH)) {
                activeHand = TANGAN_KOSONG
                tangan_kanan_musuh.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.transparant
                    )
                )
            } else if (activeHand.equals(TANGAN_KOSONG) && giliran.equals(GILIRAN_MUSUH)) {
                activeHand = TANGAN_KANAN_MUSUH
                tangan_kanan_musuh.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
            } else if (activeHand.equals(TANGAN_KIRI_MUSUH)) {
                move(TANGAN_KANAN_MUSUH)
            } else if (activeHand.equals(TANGAN_KANAN) || activeHand.equals(TANGAN_KIRI)) {
                doing(activeHand, TANGAN_KANAN_MUSUH)
                activeHand = TANGAN_KOSONG
            }
        }
        tangan_kiri_musuh.setOnClickListener {
            if (activeHand.equals(TANGAN_KIRI_MUSUH)) {
                activeHand = TANGAN_KOSONG
                tangan_kiri_musuh.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.transparant
                    )
                )
            } else if (activeHand.equals(TANGAN_KOSONG) && giliran.equals(GILIRAN_MUSUH)) {
                activeHand = TANGAN_KIRI_MUSUH
                tangan_kiri_musuh.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorAccent
                    )
                )
            } else if (activeHand.equals(TANGAN_KANAN_MUSUH)) {
                move(TANGAN_KIRI_MUSUH)
            } else if (activeHand.equals(TANGAN_KANAN) || activeHand.equals(TANGAN_KIRI)) {
                doing(activeHand, TANGAN_KIRI_MUSUH)
            }
        }
    }

    private fun updateAvailability() {
        if(gameMode.equals("local")){
            updateLocalAvailability()
        }else{
            updateOnlineAvailability()
        }

    }

    private fun updateOnlineAvailability() {
        if ((val_tangan[TANGAN_KANAN] == 0 && val_tangan[TANGAN_KIRI] == 0) ||
            (val_tangan[TANGAN_KANAN_MUSUH] == 0 && val_tangan[TANGAN_KIRI_MUSUH] == 0)
        ) {
            disableAll()
            if (giliran.equals(GILIRAN_MUSUH)) {
                turn_kamu.text = "MUSUH MENANG..."
            } else if (giliran.equals(GILIRAN_KAMU)) {
                turn_kamu.text = "KAMU MENANG!!!"
            }

            val rootObject = HashMap<String,Int>()
            for (i in (1..4)){
                rootObject.put(childName(i),val_tangan[i])
            }
            myRef.updateChildren(rootObject as Map<String, Any>)
            if (own.equals("self"))myRef.child("active").setValue("other")
            else myRef.child("active").setValue("self")

            if (mInterstitialAd.isLoaded) mInterstitialAd.show()
        } else
            if (giliran.equals(GILIRAN_MUSUH)) {
                enableAll()
                giliran = GILIRAN_KAMU
                turn_kamu.text = "giliran kamu"
            } else if (giliran.equals(GILIRAN_KAMU)) {
                disableAll()
                giliran = GILIRAN_MUSUH
                turn_kamu.text = "giliran musuh"

                val rootObject = HashMap<String,Int>()
                for (i in (1..4)){
                    rootObject.put(childName(i),val_tangan[i])
                }
                myRef.child("active").setValue("null")
                myRef.updateChildren(rootObject as Map<String, Any>)
                if (own.equals("self"))myRef.child("active").setValue("other")
                else myRef.child("active").setValue("self")
            }
    }


    private fun updateLocalAvailability() {
        if ((val_tangan[TANGAN_KANAN] == 0 && val_tangan[TANGAN_KIRI] == 0) ||
            (val_tangan[TANGAN_KANAN_MUSUH] == 0 && val_tangan[TANGAN_KIRI_MUSUH] == 0)
        ) {
            disableAll()
            turn_kamu.text = "KAMU MENANG!!!"
            turn_musuh.text = "KAMU MENANG!!!"
            if (mInterstitialAd.isLoaded) mInterstitialAd.show()
        } else
            if (giliran.equals(GILIRAN_MUSUH)) {
                giliran = GILIRAN_KAMU
                turn_kamu.visibility = VISIBLE
                turn_musuh.visibility = INVISIBLE
            } else if (giliran.equals(GILIRAN_KAMU)) {
                giliran = GILIRAN_MUSUH
                turn_kamu.visibility = INVISIBLE
                turn_musuh.visibility = VISIBLE
            }
    }

    private fun disableAll(){
        tangan_kanan_musuh.isEnabled = false
        tangan_kiri_musuh.isEnabled = false
        tangan_kanan.isEnabled = false
        tangan_kiri.isEnabled = false
    }
    private fun enableAll(){
        tangan_kanan_musuh.isEnabled = true
        tangan_kiri_musuh.isEnabled = true
        tangan_kanan.isEnabled = true
        tangan_kiri.isEnabled = true
    }

    private fun doing(hand1: Int, hand2: Int) {
        var turnDone = true
        when (hand1) {
            TANGAN_KANAN ->
                if (hand2 == TANGAN_KIRI_MUSUH && val_tangan[TANGAN_KIRI_MUSUH] != 0) {
                    val_tangan[TANGAN_KIRI_MUSUH] += val_tangan[TANGAN_KANAN]
                } else if (hand2 == TANGAN_KANAN_MUSUH && val_tangan[TANGAN_KANAN_MUSUH] != 0) {
                    val_tangan[TANGAN_KANAN_MUSUH] += val_tangan[TANGAN_KANAN]
                }else{
                    turnDone = false
                }
            TANGAN_KIRI ->
                if (hand2 == TANGAN_KIRI_MUSUH && val_tangan[TANGAN_KIRI_MUSUH] != 0) {
                    val_tangan[TANGAN_KIRI_MUSUH] += val_tangan[TANGAN_KIRI]
                } else if (hand2 == TANGAN_KANAN_MUSUH && val_tangan[TANGAN_KANAN_MUSUH] != 0) {
                    val_tangan[TANGAN_KANAN_MUSUH] += val_tangan[TANGAN_KIRI]
                }else{
                    turnDone = false
                }
            TANGAN_KANAN_MUSUH ->
                if (hand2 == TANGAN_KIRI && val_tangan[TANGAN_KIRI] != 0) {
                    val_tangan[TANGAN_KIRI] += val_tangan[TANGAN_KANAN_MUSUH]
                } else if (hand2 == TANGAN_KANAN && val_tangan[TANGAN_KANAN] != 0) {
                    val_tangan[TANGAN_KANAN] += val_tangan[TANGAN_KANAN_MUSUH]
                }else{
                    turnDone = false
                }
            TANGAN_KIRI_MUSUH ->
                if (hand2 == TANGAN_KIRI && val_tangan[TANGAN_KIRI] != 0) {
                    val_tangan[TANGAN_KIRI] += val_tangan[TANGAN_KIRI_MUSUH]
                } else if (hand2 == TANGAN_KANAN && val_tangan[TANGAN_KANAN] != 0) {
                    val_tangan[TANGAN_KANAN] += val_tangan[TANGAN_KIRI_MUSUH]
                }else{
                    turnDone = false
                }
        }
        tangan_kanan.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
        tangan_kiri.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
        tangan_kanan_musuh.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
        tangan_kiri_musuh.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
        val_tangan[TANGAN_KANAN_MUSUH] %= 5
        val_tangan[TANGAN_KIRI_MUSUH] %= 5
        val_tangan[TANGAN_KANAN] %= 5
        val_tangan[TANGAN_KIRI] %= 5
        updateImage()
        if (turnDone) updateAvailability()
    }

    private fun move(hand1: Int) {
        if (val_tangan[opposite(hand1)] > 1) initAlert(hand1)
        else doMove(hand1, 1)
    }

    private fun opposite(hand : Int) : Int {
        when(hand){
            TANGAN_KIRI -> return TANGAN_KANAN
            TANGAN_KANAN -> return TANGAN_KIRI
            TANGAN_KANAN_MUSUH -> return TANGAN_KIRI_MUSUH
            TANGAN_KIRI_MUSUH -> return TANGAN_KANAN_MUSUH
        }
        return TANGAN_KOSONG
    }

    public fun doMove(hand1: Int, move_val: Int) {
        if (move_val > 0) {
            var turnDone = false
            when (hand1) {
                TANGAN_KIRI -> if (val_tangan[TANGAN_KANAN] >= move_val) {
                    val_tangan[TANGAN_KANAN] -= move_val
                    turnDone = (val_tangan[TANGAN_KANAN] != val_tangan[TANGAN_KIRI])
                    val_tangan[TANGAN_KIRI] += move_val
                }
                TANGAN_KANAN -> if (val_tangan[TANGAN_KIRI] >= move_val) {
                    val_tangan[TANGAN_KIRI] -= move_val
                    turnDone = (val_tangan[TANGAN_KANAN] != val_tangan[TANGAN_KIRI])
                    val_tangan[TANGAN_KANAN] += move_val
                }
                TANGAN_KIRI_MUSUH -> if (val_tangan[TANGAN_KANAN_MUSUH] >= move_val) {
                    val_tangan[TANGAN_KANAN_MUSUH] -= move_val
                    turnDone = (val_tangan[TANGAN_KANAN_MUSUH] != val_tangan[TANGAN_KIRI_MUSUH])
                    val_tangan[TANGAN_KIRI_MUSUH] += move_val
                }
                TANGAN_KANAN_MUSUH -> if (val_tangan[TANGAN_KIRI_MUSUH] >= move_val) {
                    val_tangan[TANGAN_KIRI_MUSUH] -= move_val
                    turnDone = (val_tangan[TANGAN_KANAN_MUSUH] != val_tangan[TANGAN_KIRI_MUSUH])
                    val_tangan[TANGAN_KANAN_MUSUH] += move_val
                }
            }
            tangan_kanan.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
            tangan_kiri.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
            tangan_kanan_musuh.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
            tangan_kiri_musuh.setBackgroundColor(ContextCompat.getColor(this, R.color.transparant))
            val_tangan[TANGAN_KANAN_MUSUH] %= 5
            val_tangan[TANGAN_KIRI_MUSUH] %= 5
            val_tangan[TANGAN_KANAN] %= 5
            val_tangan[TANGAN_KIRI] %= 5
            updateImage()
            if (turnDone) updateAvailability()
        }

    }

    private fun updateImage() {
        activeHand = TANGAN_KOSONG
        tangan_kanan.setImageResource(getHandDrawable(val_tangan[TANGAN_KANAN]))
        tangan_kiri.setImageResource(getHandDrawable(val_tangan[TANGAN_KIRI]))
        tangan_kanan_musuh.setImageResource(getHandDrawable(val_tangan[TANGAN_KANAN_MUSUH]))
        tangan_kiri_musuh.setImageResource(getHandDrawable(val_tangan[TANGAN_KIRI_MUSUH]))
    }

    private fun getHandDrawable(i: Int): Int {
        when (i) {
            0 -> return R.drawable.nol
            1 -> return R.drawable.satu
            2 -> return R.drawable.dua
            3 -> return R.drawable.tiga
            4 -> return R.drawable.empat
            else -> return 0
        }
    }

    private fun initAlert(hand1: Int) {
        val cdd = DialogPindahinJari(this,hand1)
        cdd.show()
//        val builder = AlertDialog.Builder(this)
//
//        with(builder) {
//            setTitle("")
//            setMessage("")
//            setPositiveButton("1", { dialog, which -> doMove(hand1, 1) })
//            setNegativeButton("2", { dialog, which -> doMove(hand1, 2) })
//            setNeutralButton("cancel", { dialog, which -> doMove(hand1, 0) })
//            setCancelable(false)
//            show()
//        }

    }
}
