package com.example.mediagallery.flickr.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediagallery.flickr.adapter.PhotoAdapter
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.networking.WebClient
import kotlinx.coroutines.launch

class PhotosViewModel : ViewModel() {
    private val mutablePhotosListLiveData = MutableLiveData<List<Photo>>()
    val photosListLiveData: LiveData<List<Photo>> = mutablePhotosListLiveData

    //var photosAdapter = PhotoAdapter()
    var method =  "flickr.photos.search"
    var api_key = "5e97710be9ddf23a1c64c5feadf3d036"
    var format = "json"
    var nojsoncallback = 1

    fun loadPhotos(s:String): LiveData<List<Photo>> {
        viewModelScope.launch {
            val searchResponse = WebClient.client.fetchImages(method,api_key,format,nojsoncallback,2,s)
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