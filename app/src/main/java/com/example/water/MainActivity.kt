package com.example.water

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.anastr.speedviewlib.SpeedView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var speedometer: SpeedView
    private lateinit var totalWaterUsedText: TextView
    private lateinit var valvePositionText: TextView
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private lateinit var tapSwitch: Switch
    private lateinit var database: DatabaseReference
    private lateinit var liveFlowText: TextView
    private lateinit var valveSeekBar: SeekBar
    private lateinit var viewGraphButton: Button
    private lateinit var updateSsidButton: Button
    private lateinit var ssidInput: EditText
    private lateinit var passwordInput: EditText

    private var isTapOn = false
    private var startWaterUsed = 0
    private var totalWaterUsed = 0
    private var startTime = ""
    private lateinit var currentDateKey: String

    private var currentFlow = 0
    private var waterUsedInCycle = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        val databaseUrl = "https://demo1-ec581-default-rtdb.asia-southeast1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(databaseUrl).reference

        // Initialize UI components
        speedometer = findViewById(R.id.speedometer)
        totalWaterUsedText = findViewById(R.id.total_flow_text)
        valvePositionText = findViewById(R.id.valve_position_label)
        tapSwitch = findViewById(R.id.tap_switch)
        liveFlowText = findViewById(R.id.live_flow_text)
        valveSeekBar = findViewById(R.id.flow_control)
        viewGraphButton = findViewById(R.id.view_graph_button)

        setupSpeedometer()
        currentDateKey = getCurrentDateKey()

        // Button to open graph activity
        viewGraphButton.setOnClickListener {
            val intent = Intent(this, GraphActivity::class.java)
            startActivity(intent)
        }

        // Update SSID and Password

        fetchFirebaseData()

        // Tap Switch logic
        tapSwitch.setOnCheckedChangeListener { _, isChecked ->
            isTapOn = isChecked
            tapSwitch.text = if (isTapOn) "Tap Status: ON" else "Tap Status: OFF"

            if (isTapOn) {
                startTime = getCurrentTime()
                startWaterUsed = totalWaterUsed
                valveSeekBar.isEnabled = true
            } else {
                finalizeCycleData()
                valveSeekBar.isEnabled = false
                resetValvePosition()
            }

            database.child("status").setValue(isTapOn).addOnFailureListener {
                Log.e("Firebase", "Failed to update status: ${it.message}")
            }
        }

        // Valve Position Logic (SeekBar)
        valveSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (isTapOn) {
                    val degree = (progress * 180 / seekBar!!.max)
                    val percentage = (degree * 100 / 180)

                    valvePositionText.text = "Valve Position: $percentage%"
                    database.child("position").setValue(degree).addOnFailureListener {
                        Log.e("Firebase", "Failed to update position: ${it.message}")
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setupSpeedometer() {
        speedometer.unit = "L/min"
        speedometer.maxSpeed = 200F
    }

    private fun fetchFirebaseData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    currentFlow = snapshot.child("flow").getValue(Int::class.java) ?: 0
                    speedometer.speedTo(currentFlow.toFloat())
                    liveFlowText.text = "Live Flow: $currentFlow L/min"

                    val dailyHistory = snapshot.child("history").child(currentDateKey)
                    totalWaterUsed = dailyHistory.child("total_water").getValue(Int::class.java) ?: 0
                    totalWaterUsedText.text = "Total Water Used: $totalWaterUsed liters"

                } catch (e: Exception) {
                    Log.e("Firebase", "Error fetching data: ${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database Error: ${error.message}")
            }
        })
    }

    private fun finalizeCycleData() {
        val endTime = getCurrentTime()
        waterUsedInCycle = currentFlow

        val historyBranch = database.child("history").child(currentDateKey)
        val updatedTotalWater = totalWaterUsed + waterUsedInCycle
        historyBranch.child("total_water").setValue(updatedTotalWater)

        val cycleBranch = historyBranch.child("cycle_${System.currentTimeMillis()}")
        cycleBranch.child("start_time").setValue(startTime)
        cycleBranch.child("end_time").setValue(endTime)
        cycleBranch.child("water_used").setValue(waterUsedInCycle)

        Log.d("CycleData", "Cycle recorded successfully")
    }

    private fun resetValvePosition() {
        valveSeekBar.progress = 0
        valvePositionText.text = "Valve Position: 0%"
        database.child("position").setValue(0)
    }

    private fun getCurrentDateKey(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
