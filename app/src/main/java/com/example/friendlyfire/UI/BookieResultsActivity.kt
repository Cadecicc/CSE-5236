package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R

class BookieResultsActivity : AppCompatActivity() {

    private lateinit var liveData : ConnectionLiveData
    private lateinit var bookieResultsLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookie_results)

        checkNetworkConnection()

        bookieResultsLayout = findViewById(R.id.bookieResultsLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val betLineValue = intent.getDoubleExtra("bet_line", 3.0)
        val tallyCount = intent.getDoubleExtra("tally", 4.0)
        val backToMenu = findViewById<Button>(R.id.backToMenuButton)
        Log.i("BookieResultsActivity","Bet Line: $betLineValue\nTally: $tallyCount")

        //Convert to Int
//        val tallyCountInt = tallyCount.toString().toInt()
//        val betLineValueInt = betLineValue.toString().toInt()

        //get textView
        val textView = findViewById(R.id.description) as TextView

        //Lower side wins
        if (tallyCount < betLineValue) {
            textView.text = getString(R.string.lowerSideWin)
        }
        //higher side wins
        else{
            textView.text = getString(R.string.higherSideWin)
        }

        backToMenu.setOnClickListener{
            val mainMenu = Intent(this@BookieResultsActivity, MainMenuActivity::class.java)
            startActivity(mainMenu)
        }


    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                bookieResultsLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                bookieResultsLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}