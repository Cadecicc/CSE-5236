package com.example.friendlyfire.Model

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

object RegistrationUtil {

    var dbPlayer: CollectionReference = FirebaseFirestore.getInstance().collection("Player")

    fun  validateUser():Any?{
        var foundValidPlayer: Any? = ""

        dbPlayer.get().addOnSuccessListener { result ->
            for (doc in result){
                var players: MutableList<Any?>? = doc.data["Players"] as MutableList<Any?>?
                players?.forEach { player ->
                    if (player == "djohnson2919@gmail.com"){
                        foundValidPlayer = player
                    }
                }
                }
            }
        return foundValidPlayer
        }

}
