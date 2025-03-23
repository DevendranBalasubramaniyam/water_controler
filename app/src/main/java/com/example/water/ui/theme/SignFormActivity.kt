package com.example.water

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SignFormActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var nameInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_form)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val databaseUrl = "https://demo1-ec581-default-rtdb.asia-southeast1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseUrl).reference

        // Initialize UI components
        nameInput = findViewById(R.id.name_input)
        phoneInput = findViewById(R.id.phone_input)
        submitButton = findViewById(R.id.submit_button)

        // Submit button logic
        submitButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please enter both Name and Phone Number", Toast.LENGTH_SHORT).show()
            } else {
                addUserToDatabase(name, phone)
            }
        }
    }

    private fun addUserToDatabase(name: String, phone: String) {
        database.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Determine the next user key (user1, user2, etc.)
                val nextUserKey = "user${snapshot.childrenCount + 1}"

                val userUpdates = mapOf(
                    "name" to name,
                    "phone" to phone
                )

                // Add the user under the determined key
                database.child("users").child(nextUserKey).setValue(userUpdates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SignFormActivity, "Details submitted successfully!", Toast.LENGTH_SHORT).show()

                        // Navigate to Wi-Fi setup page
                        val intent = Intent(this@SignFormActivity, WifiSetupActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SignFormActivity, "Failed to submit details. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignFormActivity, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
