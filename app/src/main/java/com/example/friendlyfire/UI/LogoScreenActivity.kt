package com.example.friendlyfire.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R

class LogoScreenActivity : AppCompatActivity() {

    private lateinit var liveData : ConnectionLiveData
    private lateinit var logoLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo_screen)

        checkNetworkConnection()

        logoLayout = findViewById(R.id.logoLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

            val beginButton = findViewById<Button>(R.id.beginButton)
            beginButton.setOnClickListener {
                val intent = Intent(this, HomeScreenActivity::class.java)
                startActivity(intent)
            }
        }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                logoLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                logoLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
    }
