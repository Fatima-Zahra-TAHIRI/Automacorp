package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round

class RoomActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val param = intent.getLongExtra("ROOM_ID", 0)
        val viewModel: RoomViewModel by viewModels()

        // Save the room
        val onRoomSave: () -> Unit = {
            viewModel.room?.let { roomDto ->
                viewModel.updateRoom(roomDto.id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG).show()
                startActivity(Intent(baseContext, RoomListActivity::class.java))
            }
        }

        // Delete the room
        val onRoomDelete: () -> Unit = {
            viewModel.room?.let { roomDto ->
                viewModel.deleteRoom(roomDto.id)
                Toast.makeText(baseContext, "Room ${roomDto.name} was deleted", Toast.LENGTH_LONG).show()
                startActivity(Intent(baseContext, RoomListActivity::class.java))
            }
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, RoomListActivity::class.java))
        }

        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack, this) },
                    floatingActionButton = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            RoomUpdateButton(onRoomSave)
                            Spacer(modifier = Modifier.height(16.dp))
                            RoomDeleteButton(onRoomDelete)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LaunchedEffect(param) {
                        viewModel.findRoom(param)
                    }
                    if (viewModel.room != null) {
                        RoomDetail(viewModel, Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}

@Composable
fun RoomUpdateButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Done,
                contentDescription = stringResource(R.string.act_room_save),
            )
        },
        text = { Text(text = stringResource(R.string.act_room_save)) }
    )
}

@Composable
fun RoomDeleteButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        icon = {
            Icon(
                Icons.Filled.Delete,
                contentDescription = stringResource(R.string.act_room_delete),
            )
        },
        text = { Text(text = stringResource(R.string.act_room_delete)) },
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )
}

@Composable
fun NoRoom(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = stringResource(R.string.act_room_none),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
    }
}

@Composable
fun RoomDetail(model: RoomViewModel, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        // Editable room name
        Text(
            text = stringResource(R.string.act_room_name),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = model.room?.name ?: "",
            onValueChange = { newName ->
                model.room = model.room?.copy(name = newName)
            },
            label = { Text(text = stringResource(R.string.act_room_name)) },
            modifier = Modifier.fillMaxWidth()
        )

        // Display current temperature
        Text(
            text = stringResource(R.string.act_room_current_temperature),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "${model.room?.currentTemperature}°C",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Slider for target temperature
        Text(
            text = stringResource(R.string.act_room_target_temperature),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Slider(
            value = model.room?.targetTemperature?.toFloat() ?: 0f,
            onValueChange = { newValue ->
                model.room = model.room?.copy(targetTemperature = newValue.toDouble())
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            steps = 0,
            valueRange = 10f..28f,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "${round((model.room?.targetTemperature ?: 0.0) * 10) / 10}°C",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
