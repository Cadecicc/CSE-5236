package com.example.friendlyfire.Model

class Group(
    private var Players : MutableList<Any?>?,
    private var bet : String,
    private var bettingLine : Number,
    private var canProgress : Boolean,
    private var overPot : Number,
    private var underPot : Number
) {

}