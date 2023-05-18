package com.example.friendlyfire.ViewModel

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

class GroupLobbyLiveData(
    private val docRef : DocumentReference
) : LiveData<Any>(), EventListener<DocumentSnapshot> {

    private var snapshotListener : ListenerRegistration? = null

    override fun onActive() {
        super.onActive()
        snapshotListener = docRef.addSnapshotListener(this)
    }

    override fun onInactive() {
        super.onInactive()
        snapshotListener?.remove()
    }
//    lateinit var firestore : FirebaseFirestore
//    private var _players: MutableLiveData<ArrayList<Player>> = MutableLiveData<ArrayList<Player>>()

//    private fun listenToPlayers() {
//        firestore.collection("Player").addSnapshotListener{
//                snapshot, e ->
//            //if exception, skip
//            if(e != null) {
//                Log.w("MainMenuActivity", "Listen Failed", e)
//                return@addSnapshotListener
//            }
//            //if here, no exceptions encountered
//            if(snapshot != null) {
//                val allPlayers = ArrayList<Player>()
//                val documents = snapshot.documents
//                documents.forEach {
//                    val player = it.toObject(Player::class.java)
//                    if(player != null) {
//                        allPlayers.add(player!!)
//                    }
//                }
//                _players.value = allPlayers
//            }
//        }
//    }
//
//    internal var players:MutableLiveData<ArrayList<Player>>
//        get() { return _players }
//        set(value) {_players = value}

    override fun onEvent(result: DocumentSnapshot?, error: FirebaseFirestoreException?) {
//        val groupData = result?.let { document ->
//            document.toObject(Group::class.java)
//        }
    }
}