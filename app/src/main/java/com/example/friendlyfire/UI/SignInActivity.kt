package com.example.friendlyfire.UI

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignInActivity : AppCompatActivity() {

    private lateinit var liveData : ConnectionLiveData
    private lateinit var signInLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        checkNetworkConnection()

        signInLayout = findViewById(R.id.signInLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<Button>(R.id.forgotPasswordButton)

        loginButton.setOnClickListener {
            when{
                //when the register button is pressed, check to see if the email or password is empty
                TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignInActivity,
                        "Please enter email address",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@SignInActivity,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                //the email and password texts had something in them
                else ->{

                    //trim any excess spaces if the user entered them by accident
                    val email: String = email.text.toString().trim{it <= ' '}
                    val password: String = password.text.toString().trim{it <= ' '}

                    //Create an instance and create a register a user with email and password
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->

                            //If sign in is successful
                            if(task.isSuccessful){

                                //Firebase successfully signed in user. !! means can not be null
                                val firebaseUser: FirebaseUser = task.result!!.user!!

                                Toast.makeText(
                                    this@SignInActivity,
                                    "Login Successful!!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                //Send the user to the main menu
                                val intent =
                                    Intent(this@SignInActivity, MainMenuActivity::class.java)

                                //remove instances of activities running in the background (login or register)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                //send over the email and userID to the MainMenu activity for future use
                                intent.putExtra("user_id",firebaseUser.uid)
                                intent.putExtra("email_id",email)
                                startActivity(intent)
                                finish() //closes current activity. Otherwise runs in the background when we don't need it,
                                //if user presses back, app will close
                            }
                            else{
                                //The sign in was not successful
                                Toast.makeText(
                                    this@SignInActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                }
            }
        }
        forgotPassword.setOnClickListener{
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                signInLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                signInLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }
}