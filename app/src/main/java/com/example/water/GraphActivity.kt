package com.example.water

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class GraphActivity : AppCompatActivity() {

    private lateinit var dailyGraph: LineGraphView
    private lateinit var weeklyGraph: LineGraphView
    private lateinit var database: DatabaseReference
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_graphs)

    dailyGraph = findViewById(R.id.dailyGraph)
    weeklyGraph = findViewById(R.id.weeklyGraph)

    // Initialize Firebase
    val databaseUrl = "https://demo1-ec581-default-rtdb.asia-southeast1.firebasedatabase.app/"
    database = FirebaseDatabase.getInstance(databaseUrl).reference

    fetchGraphData()
}

private fun fetchGraphData() {
    val currentDateKey = getCurrentDateKey()

    // Fetch daily data
    database.child("history").child(currentDateKey)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dailyGraphData = mutableListOf<Pair<String, Float>>()

                // Extract daily data
                for (cycleSnapshot in snapshot.children) {
                    val startTime = cycleSnapshot.child("start_time").getValue(String::class.java) ?: "N/A"
                    val waterUsed = cycleSnapshot.child("water_used").getValue(Float::class.java) ?: 0f
                    dailyGraphData.add(Pair(startTime, waterUsed))
                }

                // Pass daily data to daily graph
                dailyGraph.setDataPoints(dailyGraphData)

                // Fetch weekly data after daily data
                fetchWeeklyGraphData()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database Error: ${error.message}")
            }
        })
}

private fun fetchWeeklyGraphData() {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -6) // Go back 6 days for a 7-day range
    val startDateKey = getDateKey(calendar.time)
    val endDateKey = getCurrentDateKey()

    database.child("history")
        .orderByKey()
        .startAt(startDateKey)
        .endAt(endDateKey)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val weeklyGraphData = mutableMapOf<String, Float>()

                // Aggregate weekly data
                for (dateSnapshot in snapshot.children) {
                    for (cycleSnapshot in dateSnapshot.children) {
                        val waterUsed = cycleSnapshot.child("water_used").getValue(Float::class.java) ?: 0f
                        val dateKey = dateSnapshot.key ?: "N/A"
                        weeklyGraphData[dateKey] = weeklyGraphData.getOrDefault(dateKey, 0f) + waterUsed
                    }
                }

                // Convert aggregated weekly data to sorted list
                val sortedWeeklyData = weeklyGraphData.toSortedMap().map { Pair(it.key, it.value) }

                // Pass weekly data to weekly graph
                weeklyGraph.setDataPoints(sortedWeeklyData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database Error: ${error.message}")
            }
        })
}

private fun getCurrentDateKey(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}

private fun getDateKey(date: Date): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(date)
}
}