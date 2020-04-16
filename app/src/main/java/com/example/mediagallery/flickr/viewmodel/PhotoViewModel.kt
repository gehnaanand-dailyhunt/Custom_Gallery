package com.example.mediagallery.flickr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediagallery.flickr.adapter.PhotosAdapter
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.networking.WebClient
import kotlinx.coroutines.launch

class PhotosViewModel : ViewModel() {
    private val mutablePhotosListLiveData = MutableLiveData<List<Photo>>()
    val photosListLiveData: LiveData<List<Photo>> = mutablePhotosListLiveData

    var photosAdapter = PhotosAdapter()

    fun loadPhotos(s : String): LiveData<List<Photo>> {
        viewModelScope.launch {
            val searchResponse = WebClient.client.fetchImages(s)
            val photosList = searchResponse.photos.photo.map { photo ->
                Photo(
                    id = photo.id,
                    url = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg",
                    title = photo.title
                )
            }
            mutablePhotosListLiveData.postValue(photosList)
        }
        return photosListLiveData
    }
}