package ca.unb.mobiledev.superduperfitnessapp.model

import java.util.*

data class Record(
    var name: String = "",
    var data: Long = 0,
    var image:ByteArray?=null
)