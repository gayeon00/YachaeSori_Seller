package com.yachaesori.yachaesori_seller.util

import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.yachaesori.yachaesori_seller.R

fun ImageView.setImageFromUrl(imageUrl: String) {

    Log.d("ProductListAdapter", imageUrl)
    val storageReference = Firebase.storage.reference.child(imageUrl)

    storageReference.downloadUrl.addOnSuccessListener {
        Glide.with(context)
            .load(it)
            .placeholder(R.drawable.loading_placeholder)
            .fitCenter()
            .into(this)
    }
}