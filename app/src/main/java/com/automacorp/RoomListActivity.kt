package com.automacorp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme

class RoomListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel: RoomViewModel by viewModels()

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }

        val openRoom: (roomId: Long) -> Unit = {
            val intent = Intent(this, RoomActivity::class.java)
            intent.putExtra("ROOM_ID", it)
            startActivity(intent)
        }

        val context = this

        setContent {
            val roomsState by viewModel.roomsState.collectAsState() // Collecting the state

            // Fetch rooms data on initial launch
            LaunchedEffect(Unit) {
                viewModel.findAll()
            }

            // Check for errors or display rooms
            if (roomsState.error != null) {
                // Display an error message
                Toast.makeText(context, "Error: ${roomsState.error}", Toast.LENGTH_LONG).show()
            }

            // Display rooms list
            RoomList(
                context = context,
                rooms = roomsState.rooms,
                navigateBack = navigateBack,
                openRoom = openRoom
            )
        }
    }
}


@Composable
fun RoomList(
    context: Context,
    rooms: List<RoomDto>,
    navigateBack: () -> Unit,
    openRoom: (id: Long) -> Unit
) {
    AutomacorpTheme {
        Scaffold(
            topBar = { AutomacorpTopAppBar("Rooms", navigateBack, context) },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {  context.startActivity(Intent(context, NewRoomActivity::class.java)) },
                    icon = {
                        Icon(
                            Icons.Filled.AddCircle,
                            contentDescription = stringResource(R.string.act_room_create),
                        )
                    },
                    text = { Text(text = stringResource(R.string.act_room_create)) }
                )}
        ) { innerPadding ->
            if (rooms.isEmpty()) {
                Text(
                    text = "No room found",
                    modifier = Modifier.padding(innerPadding)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(innerPadding),
                ) {
                    items(rooms, key = { it.id }) {
                        RoomItem(
                            room = it,
                            modifier = Modifier.clickable { openRoom(it.id) },
                        )
                    }
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun RoomListPreview() {
    AutomacorpTheme {
        // Sample preview with mock data for rooms
        LazyColumn(
            contentPadding = PaddingValues(4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp),
        ) {
            val rooms = RoomService.findAll()
            items(rooms, key = { it.id }) {
                RoomItem(
                    room = it,
                    modifier = Modifier
                )
            }
        }
    }
}
