package com.b305.buddy.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.b305.buddy.databinding.ActivityAdminBinding
import com.b305.buddy.util.LocationProvider
import com.b305.buddy.util.SharedManager
import com.b305.buddy.util.Socket
import kotlinx.coroutines.Runnable

// admin
// adminadmin
class AdminActivity : AppCompatActivity() {
    
    lateinit var binding: ActivityAdminBinding
    private val sharedManager: SharedManager by lazy { SharedManager(this) }
    private val socket = Socket(this)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        
        binding.btnLogout.setOnClickListener {
            sharedManager.removeCurrentToken()
            sharedManager.removeCurrentToken()
            Toast.makeText(this, "로그아웃 성공", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
        
        binding.btnConnect.setOnClickListener {
            Log.d("AdminActivity", "connection")
            socket.connection()
        }
        
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                Log.d("AdminActivity", "sendLocation")
                sendLocation()
                handler.postDelayed(this, 3000)
            }
        }
        binding.btnSend.setOnClickListener {
            handler.post(runnable)
        }
        
        binding.btnStop.setOnClickListener {
            handler.removeCallbacks(runnable)
        }
    }
    
    private fun sendLocation() {
        val locationProvider = LocationProvider(this)
        val latitude = locationProvider.getLocationLatitude().toString()
        val longitude = locationProvider.getLocationLongitude().toString()
        
        socket.sendLocation(latitude, longitude)
    }
}