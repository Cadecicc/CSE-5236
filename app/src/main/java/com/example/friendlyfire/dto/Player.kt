package com.example.friendlyfire.dto

data class Player(var username:String = "") {

    override fun toString(): String {
        return username
    }
}