package com.example.mediagallery.viewmodel

import java.util.concurrent.TimeUnit
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
import com.example.mediagallery.model.GalleryPicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GalleryVideoViewModel(application: Application) : AndroidViewModel(application) {

    private val _videos = MutableLiveData<List<GalleryPicture>>()
    val videos: LiveData<List<GalleryPicture>> get() = _videos

    private var contentObserver: ContentObserver? = null

    fun loadVideos() {
        viewModelScope.launch {
            val videoList = queryVideos()
            _videos.postValue(videoList)

            if (contentObserver == null) {
                contentObserver = getApplication<Application>().contentResolver.registerObserver(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                ) {
                    loadVideos()
                }
            }
        }
    }

    private suspend fun queryVideos(): List<GalleryPicture> {
        val videos = mutableListOf<GalleryPicture>()

        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.SIZE
            )


            val selection = "${MediaStore.Video.Media.DURATION} >= ?"
            val selectionArgs = arrayOf(
                TimeUnit.MILLISECONDS.convert(5, TimeUnit.MINUTES).toString()
            )
            val sortOrder = "${MediaStore.Video.Media.DATE_TAKEN} DESC"

            getApplication<Application>().contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->


                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                //val durationColumn =
                    //cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
                val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
                //val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (cursor.moveToNext()) {

                    val id = cursor.getLong(idColumn)
                    val dateTaken = cursor.getLong(dateTakenColumn)
                    val displayName = cursor.getString(nameColumn)
                    //val size = cursor.getInt(sizeColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        id
                    ).toString()

                    val video = GalleryPicture(id,displayName,null,dateTaken,contentUri)
                    videos += video
                }
            }
        }
        return videos
    }


    override fun onCleared() {
        contentObserver?.let {
            getApplication<Application>().contentResolver.unregisterContentObserver(it)
        }
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