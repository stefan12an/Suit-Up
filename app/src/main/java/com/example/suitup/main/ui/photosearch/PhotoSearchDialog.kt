package com.example.suitup.main.ui.photosearch

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import suitup.R

class PhotoSearchDialog(val call: (String) -> Unit, private val predictions: List<String>?) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.photo_search_dialog)
        val listView = dialog.findViewById<ListView>(R.id.prediction_list)
        val adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                predictions ?: emptyList()
            )
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, i, _ ->
            dismiss()
            call(adapter.getItem(i).toString())
        }
        dialog.show()
        return dialog
    }

    companion object {
        const val TAG = "PurchaseConfirmationDialog"
    }
}