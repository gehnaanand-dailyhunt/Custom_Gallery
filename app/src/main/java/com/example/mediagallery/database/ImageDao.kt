package com.example.mediagallery.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mediagallery.model.GalleryPicture

@Dao
interface ImageDao{
    @Insert
    fun insert(image: GalleryPicture)

    @Query("SELECT * from image_table WHERE id = :id")
    fun get(id: Long): GalleryPicture?

    @Query("SELECT * from image_table")
    fun getImages(): LiveData<List<GalleryPicture>>

    @Query("SELECT * from image_table where isLiked = 1")
    fun getLiked(): LiveData<List<GalleryPicture>>

    @Update
    fun likeToggle(image: GalleryPicture)

    @Query("update image_table set isLiked = :like where id = :id")
    fun likeUpdate(id: Long, like: Boolean)

}