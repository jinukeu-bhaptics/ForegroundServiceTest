package com.example.foregroundservicetest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foregroundservicetest.ui.theme.ForegroundServiceTestTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val logList = mutableStateListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForegroundServiceTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(logList)
                }
            }
        }
    }

    private fun startLoggingThread() {
        Thread {
            while (true) {
                val currentTime = System.currentTimeMillis()
                logList.add("Log at: $currentTime")
                Log.d(TAG, "Log at: $currentTime")
                runOnUiThread {
                    // Update the UI
                }
                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    @Composable
    fun MainScreen(logs: List<String>) {
        var serviceRunning by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.fillMaxSize().padding(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = {
                startForegroundService(Intent(this@MainActivity, ForegroundService::class.java))
                serviceRunning = true
                startLoggingThread()
            }) {
                Text(text = "Start Service")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                stopService(Intent(this@MainActivity, ForegroundService::class.java))
                serviceRunning = false
            }) {
                Text(text = "Stop Service")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                finish()
            }) {
                Text(text = "Quit App")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxSize()) {
                logs.forEach { log ->
                    Text(text = log)
                }
            }
        }
    }
}

