package com.example.mediagallery.viewmodel

import android.app.Application
import android.content.ContentUris
import android.content.ContentResolver
import android.database.ContentObserver
import android.provider.MediaStore
import android.net.Uri
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mediagallery.database.ImageDatabase
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.repository.ImageRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class GalleryViewModel(application: Application) : AndroidViewModel(application) {



    private val _images = MutableLiveData<List<GalleryPicture>>()
    val images: LiveData<List<GalleryPicture>> get() = _images

    private  val imageRepo: ImageRepo

    init {
        loadImages()
        val imageDao = ImageDatabase.getDatabase(application).imageDao()
        imageRepo = ImageRepo(imageDao)
    }

    private var contentObserver: ContentObserver? = null

    fun loadImages() {
        viewModelScope.launch {
            val imageList = queryImages()
            _images.postValue(imageList)

            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadImages()
                }
            }
        }
    }

    private suspend fun queryImages(): List<GalleryPicture> {
        val images = mutableListOf<GalleryPicture>()

        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN
            )


            val selection = null
            val selectionArgs = null
            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            getApplication<Application>().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->


                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()) {

                    val id = cursor.getLong(idColumn)
                    val dateTaken = cursor.getLong(dateTakenColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val image = GalleryPicture(id, displayName,null, dateTaken, contentUri)
                    images += image


                }
            }
        }
        return images
    }


    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
    }


    fun insert(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.insert(galleryPicture)
    }

    fun delete(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.delete(galleryPicture)
    }
    fun update(galleryPicture: GalleryPicture) = viewModelScope.launch(Dispatchers.IO) {
        imageRepo.likeToggle(galleryPicture)
    }

}
private fun ContentResolver.registerObserver(
    uri: Uri,
    observer: (selfChange: Boolean) -> Unit
): ContentObserver {
    val contentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            observer(selfChange)
        }
    }
    registerContentObserver(uri, true, contentObserver)
    return contentObserver
}