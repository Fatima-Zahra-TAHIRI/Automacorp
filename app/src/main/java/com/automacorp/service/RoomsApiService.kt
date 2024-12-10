package com.automacorp.service

import com.automacorp.model.RoomCommandDto
import com.automacorp.model.RoomDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomsApiService {
    // Read all rooms
    @GET("rooms")
    fun findAll(): Call<List<RoomDto>>

    // Read one room by its id
    @GET("rooms/{id}")
    fun findById(@Path("id") id: Long): Call<RoomDto>

    // Update a room
    @PUT("rooms/{id}")
    fun updateRoom(@Path("id") id: Long, @Body room: RoomCommandDto): Call<RoomDto>

    // Create a new room
    @POST("rooms")
    fun createRoom(@Body room: RoomCommandDto): Call<RoomDto>

    // Delete a room by its id
    @DELETE("rooms/{id}")
    fun deleteRoom(@Path("id") id: Long): Call<Void>
}