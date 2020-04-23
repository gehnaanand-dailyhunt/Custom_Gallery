package com.example.mediagallery.flickr.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.mediagallery.database.ImageDatabase
import com.example.mediagallery.flickr.adapter.PhotoAdapter
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.networking.WebClient
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.repository.ImageRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PhotosViewModel(application: Application) : AndroidViewModel(application) {
    private val mutablePhotosListLiveData = MutableLiveData<List<Photo>>()
    val photosListLiveData: LiveData<List<Photo>> = mutablePhotosListLiveData

    private val _images = MutableLiveData<List<GalleryPicture>>()
    val images: LiveData<List<GalleryPicture>> get() = _images

    private  val imageRepo: ImageRepo

    init {
        val imageDao = ImageDatabase.getDatabase(application).imageDao()
        imageRepo = ImageRepo(imageDao)
    }
    //var photosAdapter = PhotoAdapter()
    var method =  "flickr.photos.search"
    var api_key = "5e97710be9ddf23a1c64c5feadf3d036"
    var format = "json"
    var nojsoncallback = 1


    fun loadPhotos(s:String): LiveData<List<GalleryPicture>> {
        viewModelScope.launch {
            val images = mutableListOf<GalleryPicture>()
            val searchResponse = WebClient.client.fetchImages(method,api_key,format,nojsoncallback,1,s)
            val photosList = searchResponse.photos.photo.map { photo ->
                Photo(
                    id = photo.id,
                    url = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg",
                    title = photo.title,
                    dateTaken = System.currentTimeMillis()
                )
                val url = "https://farm${photo.farm}.staticflickr.com/${photo.server}/${photo.id}_${photo.secret}.jpg"
                val image = GalleryPicture(photo.id.toLong(), photo.title, null, System.currentTimeMillis(), url)
                images += image
            }
            _images.postValue(images)
            //mutablePhotosListLiveData.postValue(photosList)
        }
        return images
        //return photosListLiveData
    }

    fun insert(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.insert(galleryPicture)
    }

    fun delete(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.delete(id)
    }
    fun update(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.likeToggle(galleryPicture)
    }
}