package com.example.mediagallery.model

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "image_table")
class GalleryPicture(
    @PrimaryKey (autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "tag")
    var tag: String?,
    @ColumnInfo(name = "dateTaken")
    val dateTaken: Long,
    @ColumnInfo(name = "contentUri")
    val contentUri: String,
    @ColumnInfo(name = "isLiked")
    var isLiked: Boolean = false) {

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<GalleryPicture>() {
            override fun areItemsTheSame(oldItem: GalleryPicture, newItem: GalleryPicture) =
                oldItem.id == newItem.id

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: GalleryPicture, newItem: GalleryPicture) =
                oldItem == newItem
        }
    }
}