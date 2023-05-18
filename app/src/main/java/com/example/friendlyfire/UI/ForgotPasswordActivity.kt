package com.example.friendlyfire.UI

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var liveData : ConnectionLiveData
    private lateinit var forgotPasswordLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        checkNetworkConnection()

        forgotPasswordLayout = findViewById(R.id.forgotPasswordLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val email = findViewById<EditText>(R.id.email)
        val submitButton = findViewById<Button>(R.id.submitButton)

        submitButton.setOnClickListener{
            val emailText: String = email.text.toString().trim{it <= ' '}
            if (emailText.isEmpty()){
                Toast.makeText(
                    this@ForgotPasswordActivity,
                    "Please enter email address",
                    Toast.LENGTH_SHORT
                ).show()
            }
            //firebase functionality. Firebase takes care of everything with the email being sent and the
            //actual reset of the password with the link send in the email
            else{
               FirebaseAuth.getInstance().sendPasswordResetEmail(emailText)
                   .addOnCompleteListener { task ->
                       if (task.isSuccessful) {
                           Toast.makeText(
                               this@ForgotPasswordActivity,
                               "Email sent successfully to reset your password!",
                               Toast.LENGTH_SHORT
                           ).show()
                           finish() //no longer need this activity instance. Assumption that user went to email account to reset
                       }
                       else{
                           //Email doesn't exist in the system or some other error
                           Toast.makeText(
                               this@ForgotPasswordActivity,
                               task.exception!!.message.toString(),
                               Toast.LENGTH_SHORT
                           ).show()
                       }
                   }
            }
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                forgotPasswordLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                forgotPasswordLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}