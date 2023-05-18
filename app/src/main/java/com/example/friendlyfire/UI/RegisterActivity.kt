package com.example.friendlyfire.UI

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerFragment : RegisterFragment
    lateinit var db: CollectionReference

    private lateinit var liveData : ConnectionLiveData
    private lateinit var registerLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        checkNetworkConnection()

        registerLayout = findViewById(R.id.registerLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

        db = FirebaseFirestore.getInstance().collection("Player")

        registerFragment = RegisterFragment()
        val registerButton = findViewById<Button>(R.id.registerButton)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
//        val venmo = findViewById<EditText>(R.id.venmo)
//        val username = findViewById<EditText>(R.id.username)

        registerButton.setOnClickListener {
//            var fragTransaction = supportFragmentManager.beginTransaction()
//            fragTransaction.replace(R.id.fragLayout, RegisterFragment)
//            fragTransaction.commit()

            when{
                //when the register button is pressed, check to see if the email or password is empty
                TextUtils.isEmpty(email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                //the email and password texts had something in them
                else ->{

                    //trim any excess spaces if the user entered them by accident
                    val email: String = email.text.toString().trim{it <= ' '}
                    val password: String = password.text.toString().trim{it <= ' '}
//                    val venmo: String = venmo.text.toString().trim{it <= ' '}
//                    val username: String = username.text.toString().trim{it <= ' '}

                    val isValidPassword: Boolean = isValidPassword(password)
                    val isValidEmail: Boolean = isEmailValid(email)

                    if(isValidPassword && isValidEmail){
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener { task ->

                                //If registration is successful
                                if(task.isSuccessful){

                                    //Firebase successfully registered user. !! means can not be null
                                    val firebaseUser: FirebaseUser = task.result!!.user!!

                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Register Successful!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else{
                                    //The register was not successful
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }

                        Log.i("RegisterActivity", "Attempting to add data...")
                        //change so this is triggered once the auth has been updated, maybe use onAuthStateChanged
//                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                        val items = HashMap<String, Any>()
                        items.put("role", "none")
//                    items.put("username", username)
//                    items.put("venmo", venmo)
                        items.put("wagerAmount", 0)
                        items.put("wagerSide", "under")
                        db.document(email).set(items).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.i("RegisterActivity", "Successful Register...")
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Added Account to Firestore",
                                    Toast.LENGTH_SHORT
                                ).show()

                                //Send the user to the main menu
                                val intent =
                                    Intent(this@RegisterActivity, MainMenuActivity::class.java)

                                //remove instances of activities running in the background (login or register)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                                //send over the email and userID to the MainMenu activity for future use
//                        intent.putExtra("user_id",firebaseUser.uid)
                                intent.putExtra("email_id", email)
                                startActivity(intent)
                                finish() //closes current activity. Otherwise runs in the background when we don't need it,
                                //if user presses back, app will close
                                Log.i("RegisterActivity", "Exiting Register...")
                            } else {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                    else{
                        Toast.makeText(
                            this@RegisterActivity,
                            "Password does not meet requirements",
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
                registerLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                registerLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }



    companion object {
        fun isValidPassword(password: String?): Boolean {
            val pattern: Pattern
            val passwordPattern= "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$!%^&+=])(?=\\S+$).{4,}$"
            pattern = Pattern.compile(passwordPattern)
            val matcher: Matcher = pattern.matcher(password)
            return matcher.matches()
        }

        fun isEmailValid(email: String?): Boolean {
            val pattern: Pattern
            val emailRegex = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\." + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+"
            pattern = Pattern.compile(emailRegex)
            val matcher: Matcher = pattern.matcher(email)
            return matcher.matches()


        }
    }


}