package com.example.mediagallery.flickr

import android.os.Bundle
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediagallery.R
//import com.example.mediagallery.databinding.FlickrActivityPhotosBinding
import com.example.mediagallery.flickr.adapter.PhotosAdapter
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.viewmodel.PhotosViewModel
import kotlinx.android.synthetic.main.flickr_activity_photos.*


class PhotosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flickr_activity_photos)
        val photosViewModel: PhotosViewModel by viewModels()
        //val photosActivityPhotosBinding : FlickrActivityPhotosBinding
        //val photoAdapter = PhotosAdapter()
        var search_et = findViewById(R.id.search_editText) as EditText
        var string = search_et.text.toString()
        photosRecyclerView.adapter = photosViewModel.photosAdapter
        photosRecyclerView.layoutManager = GridLayoutManager(this, 3)

        photosViewModel.loadPhotos(string).observe(this,
            Observer<List<Photo>> { list ->
                with(photosViewModel.photosAdapter) {
                    photos.clear()
                    photos.addAll(list)
                    notifyDataSetChanged()
                }
         })
        /*photosViewModel.photosListLiveData.observe(this, Observer <List<Photo>>{list ->
            photoAdapter.photos.addAll(list)

        })*/
    }
}