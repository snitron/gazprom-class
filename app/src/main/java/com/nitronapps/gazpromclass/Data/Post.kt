package com.nitronapps.gazpromclass.Data

import android.net.Uri

import java.util.ArrayList

class Post(val title: String, val content: String, val author: String, val count: String, val paths: ArrayList<Uri>) {

    val pathsLength: Int
        get() = paths.size
}
