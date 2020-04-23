package com.example.mediagallery.repository

import androidx.lifecycle.LiveData
import com.example.mediagallery.database.ImageDao
import com.example.mediagallery.model.GalleryPicture

class ImageRepo(private val imageDao: ImageDao){
    val photos: LiveData<List<GalleryPicture>> = imageDao.getImages()

    fun insert(galleryPicture: GalleryPicture){
        imageDao.insert(galleryPicture)
    }

    fun likeToggle(galleryPicture: GalleryPicture){
        imageDao.likeToggle(galleryPicture)
    }

    fun delete(id: Long){
        imageDao.delete(id)
    }

    val likedPhotos : LiveData<List<GalleryPicture>> = imageDao.getLiked()
}