package com.seattracking.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.seattracking.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var ref: DatabaseReference
    private lateinit var binding: ActivityMainBinding

    var totalSeat = 29;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Write a message to the database
        val database = FirebaseDatabase.getInstance()
        ref  = database.getReference("label_counts")

        binding.sendButton.setOnClickListener {
            val message = binding.inputEditText.text.toString()
            sendMessage(message)
        }

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val latestData = snapshot.children.lastOrNull()?.getValue(Availability::class.java)
                binding.dataTextView.text = (totalSeat - latestData?.Seated!!).toString()

                binding.seatedData.text = latestData?.Seated.toString()
                binding.unseatedData.text = latestData?.Unseated.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                error.toException().printStackTrace()
            }
        })



    }

    private fun sendMessage(message: String){
        val messageObject = Message(message)
        Log.d("firebase", "message sent")

        ref.push().setValue(messageObject).addOnSuccessListener {

            binding.inputEditText.text.clear()

        }.addOnFailureListener {
            it.printStackTrace()
        }


    }

    data class Message(val message: String){
        constructor() : this("")
    }

    data class Availability(val Seated: Int = 0, val Unseated: Int = 0){

    }
}