package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomCommandDto
import com.automacorp.ui.theme.AutomacorpTheme

class NewRoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: RoomViewModel by viewModels()

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, RoomListActivity::class.java))
        }

        setContent {
            AutomacorpTheme {
                var name by remember { mutableStateOf("") }
                var currentTemperature by remember { mutableStateOf("") }
                var targetTemperature by remember { mutableStateOf("") }

                Scaffold(
                    topBar = {
                        AutomacorpTopAppBar("New Room", navigateBack, this)
                    },
                    content = { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Room Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = currentTemperature,
                                onValueChange = { currentTemperature = it },
                                label = { Text("Current Temperature") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = targetTemperature,
                                onValueChange = { targetTemperature = it },
                                label = { Text("Target Temperature") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = {
                                    if (name.isBlank() || currentTemperature.isBlank() || targetTemperature.isBlank()) {
                                        Toast.makeText(
                                            this@NewRoomActivity,
                                            "All fields are required",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        val roomCommand = RoomCommandDto(
                                            name = name,
                                            currentTemperature = currentTemperature.toDoubleOrNull(),
                                            targetTemperature = targetTemperature.toDoubleOrNull()
                                        )
                                        viewModel.createRoom(roomCommand)
                                        Toast.makeText(
                                            this@NewRoomActivity,
                                            "Room created successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish() // Close the activity
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Create Room")
                            }
                        }
                    }
                )
            }
        }
    }
}
