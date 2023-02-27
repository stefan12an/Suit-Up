package com.example.suitup.main.ui.profile

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import suitup.R

class EditCredentialsDialog(context: Context) : Dialog(context) {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_edit_credentials)

        emailInput = findViewById(R.id.profile_email)
        passwordInput = findViewById(R.id.profile_password)
        saveButton = findViewById(R.id.profile_save_button)
        cancelButton = findViewById(R.id.profile_cancel_button)
        firebaseAuth = FirebaseAuth.getInstance()

        emailInput.setText(firebaseAuth.currentUser?.email)

        saveButton.setOnClickListener {
            if(emailInput.text.toString() != firebaseAuth.currentUser?.email){
                firebaseAuth.currentUser?.updateEmail(emailInput.text.toString())
            }
            if (passwordInput.text.toString() != ""){
                firebaseAuth.currentUser?.updatePassword(passwordInput.text.toString())
            }
            dismiss()
        }
        cancelButton.setOnClickListener { dismiss() }
    }
}