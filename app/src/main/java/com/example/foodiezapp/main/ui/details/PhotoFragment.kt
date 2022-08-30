package com.example.foodiezapp.main.ui.details

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.example.foodiezapp.R
import com.squareup.picasso.Picasso


class PhotoFragment(private val photos_url: String?) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.fragment_photo)
        val imageView = dialog.findViewById<ImageView>(R.id.photo)
        Picasso.get().load(photos_url).into(imageView)
        dialog.show()
        return dialog
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}