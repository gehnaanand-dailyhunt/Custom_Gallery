package com.example.mediagallery.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mediagallery.database.ImageDatabase
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.repository.ImageRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageViewModel(application: Application) : AndroidViewModel(application){
    private  val imageRepo: ImageRepo
    val allPhotos : LiveData<List<GalleryPicture>>
    val likedPhotos : LiveData<List<GalleryPicture>>

    init {
        val imageDao = ImageDatabase.getDatabase(application).imageDao()
        imageRepo = ImageRepo(imageDao)
        allPhotos = imageRepo.photos
        likedPhotos = imageRepo.likedPhotos
    }

    fun insert(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.insert(galleryPicture)
    }

    fun update(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.likeToggle(galleryPicture)
    }

    fun delete(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.delete(galleryPicture)
    }
}