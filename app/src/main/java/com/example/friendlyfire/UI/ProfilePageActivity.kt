package com.example.friendlyfire.UI

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.friendlyfire.Model.ConnectionLiveData
import com.example.friendlyfire.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfilePageActivity : AppCompatActivity() {
    private lateinit var choosePhoto:Button
    private lateinit var takePhoto:Button
    private lateinit var profilePic:ImageView
    private lateinit var db: DocumentReference

    private lateinit var liveData : ConnectionLiveData
    private lateinit var profilePageLayout: RelativeLayout
    private lateinit var noWifiLayout: RelativeLayout

    companion object{
        const val imageRequestCode = 1
        const val cameraCode = 101
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        checkNetworkConnection()

        profilePageLayout = findViewById(R.id.profilePageLayout)
        noWifiLayout = findViewById(R.id.noWifiLayout)

//        registerFragment = RegisterFragment()
        val user = FirebaseAuth.getInstance().currentUser
        db = FirebaseFirestore.getInstance().document("Player/${user?.uid}")

        choosePhoto = findViewById(R.id.choosePhotoButton)
        takePhoto = findViewById(R.id.takePhotoButton)
        profilePic = findViewById(R.id.profilePicture)

        val emailEt = findViewById<EditText>(R.id.emailEt)
//        val venmoEt = findViewById<EditText>(R.id.venmoEt)
//        val usernameEt = findViewById<EditText>(R.id.usernameEt)
        val passwordEt = findViewById<EditText>(R.id.passwordEt)
        val updateProfileButton = findViewById<Button>(R.id.updateProfileButton)
        val mainMenuButton = findViewById<Button>(R.id.mainMenuButton)

        val getImage = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                profilePic.setImageURI(it)
            }
        )


        takePhoto.isEnabled = false
        choosePhoto.isEnabled = true
        getPermissions()

        choosePhoto.setOnClickListener {
            getImage.launch("image/*")
        }


        updateProfileButton.setOnClickListener {
//            var fragTransaction = supportFragmentManager.beginTransaction()
//            fragTransaction.replace(R.id.fragLayout, RegisterFragment)
//            fragTransaction.commit()
            val newEmail = emailEt.text.toString().trim{it <= ' '}
            val newPassword = passwordEt.text.toString().trim{it <= ' '}
//            val newVenmo = venmoEt.text.toString().trim{it <= ' '}

            when(!TextUtils.isEmpty(newEmail)) {
                true -> {
                    user!!.updateEmail(newEmail)
                        .addOnCompleteListener { task ->
                            //If update is successful
                            if(task.isSuccessful){
                                Toast.makeText(
                                    this@ProfilePageActivity,
                                    "Email updated to $newEmail",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            else{
                                //The update was not successful
                                Toast.makeText(
                                    this@ProfilePageActivity,
                                    task.exception!!.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                }
                false -> Log.i("ProfilePageActivity", "No update for Email Address was specified")
            }
            when(!TextUtils.isEmpty(newPassword)) {
                true -> user!!.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        //If update is successful
                        if(task.isSuccessful){
                            Toast.makeText(
                                this@ProfilePageActivity,
                                "Password updated to $newPassword",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else{
                            //The update was not successful
                            Toast.makeText(
                                this@ProfilePageActivity,
                                task.exception!!.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }
                false -> Log.i("ProfilePageActivity", "No update for Password was specified")
            }
//            val updatedVenmo = when(!TextUtils.isEmpty(newVenmo)) {
//                true -> newVenmo
//                false -> venmoTv.getText().toString()
//            }
        }

        mainMenuButton.setOnClickListener{
            startActivity(Intent(this@ProfilePageActivity, MainMenuActivity::class.java))
            this.finish()
        }
    }

    private fun getPermissions() {
        //if user has not granted permission for camera, then request permission
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 111)
        }
        else{
            takePhoto .isEnabled = true
            takePhoto.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, cameraCode)


            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            takePhoto.isEnabled = true
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == cameraCode){
            val bmp = data?.extras?.get("data") as Bitmap
            profilePic.setImageBitmap(bmp)
        }
    }
    private fun checkNetworkConnection(){
        liveData = ConnectionLiveData(application)
        liveData.observe(this) { isConnected ->

            if (isConnected) {
                profilePageLayout.visibility = View.VISIBLE
                noWifiLayout.visibility = View.GONE

            } else {
                profilePageLayout.visibility = View.GONE
                noWifiLayout.visibility = View.VISIBLE
            }
        }
    }

}