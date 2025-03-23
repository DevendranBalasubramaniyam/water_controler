package com.example.water

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WifiSetupActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_setup)

        database = FirebaseDatabase.getInstance().reference

        val ssidInput = findViewById<EditText>(R.id.ssid_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val updateButton = findViewById<Button>(R.id.update_button)

        updateButton.setOnClickListener {
            val ssid = ssidInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (ssid.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both SSID and Password", Toast.LENGTH_SHORT).show()
            } else {
                val updates = mapOf(
                    "ssid" to ssid,
                    "password" to password
                )

                database.updateChildren(updates).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "SSID and Password updated successfully!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Failed to update. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
