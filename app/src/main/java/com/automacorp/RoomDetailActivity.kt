package com.automacorp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.automacorp.model.RoomDto
import com.automacorp.service.RoomService
import com.automacorp.ui.theme.AutomacorpTheme
import kotlin.math.round

class RoomDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val param = intent.getLongExtra("ROOM_ID", 0)
        val viewModel: RoomViewModel by viewModels()
        viewModel.room = RoomService.findById(param)

        val onRoomSave: () -> Unit = {
            if(viewModel.room != null) {
                val roomDto: RoomDto = viewModel.room as RoomDto
                RoomService.updateRoom(roomDto.id, roomDto)
                Toast.makeText(baseContext, "Room ${roomDto.name} was updated", Toast.LENGTH_LONG).show()
                startActivity(Intent(baseContext, MainActivity::class.java))
            }
        }

        val navigateBack: () -> Unit = {
            startActivity(Intent(baseContext, MainActivity::class.java))
        }
        setContent {
            AutomacorpTheme {
                Scaffold(
                    topBar = { AutomacorpTopAppBar("Room", navigateBack,this) },
                    floatingActionButton = { RoomUpdateButton(onRoomSave) },
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (viewModel.room != null) {
                        RoomDetail(viewModel,Modifier.padding(innerPadding))
                    } else {
                        NoRoom(Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}



