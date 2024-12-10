package com.automacorp

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel : ViewModel() {
    var room by mutableStateOf<RoomDto?>(null)
    val roomsState = MutableStateFlow(RoomList())

    fun findAll() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findAll().execute() }
                .onSuccess { response ->
                    val rooms = response.body() ?: emptyList()
                    roomsState.value = RoomList(rooms) // On success, update rooms list
                    // Log success message
                    Log.d("RoomViewModel", "Successfully fetched rooms: ${rooms.size} rooms found.")
                }
                .onFailure { exception ->
                    exception.printStackTrace()
                    roomsState.value = RoomList(emptyList(), exception.message ?: "Unknown Error") // On failure, set error message
                    // Log error message
                    Log.e("RoomViewModel", "Failed to fetch rooms: ${exception.message}", exception)
                }
        }
    }


    fun findRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.findById(id).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature ?.let { Math.round(it * 10) /10.0 },
            currentTemperature = roomDto.currentTemperature,
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun deleteRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.deleteRoom(id).execute() }
                .onSuccess {
                    Log.d("RoomViewModel", "Successfully deleted room with ID: $id")
                }
                .onFailure {
                    it.printStackTrace()
                    Log.e("RoomViewModel", "Failed to delete room with ID: $id - ${it.message}", it)
                }
        }
    }

    fun createRoom(roomCommand: RoomCommandDto) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.createRoom(roomCommand).execute() }
                .onSuccess { response ->
                    room = response.body()
                    Log.d("RoomViewModel", "Successfully created room: ${room?.name}")
                }
                .onFailure { exception ->
                    exception.printStackTrace()
                    Log.e("RoomViewModel", "Failed to create room: ${exception.message}", exception)
                }
        }
    }


}
