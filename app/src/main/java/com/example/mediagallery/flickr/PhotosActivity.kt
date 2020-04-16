package com.example.mediagallery.flickr

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediagallery.R
import com.example.mediagallery.flickr.adapter.PhotoAdapter
//import com.example.mediagallery.databinding.FlickrActivityPhotosBinding
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.viewmodel.PhotosViewModel
import kotlinx.android.synthetic.main.flickr_activity_photos.*


class PhotosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flickr_activity_photos)
        val photosViewModel: PhotosViewModel by viewModels()
        //val photosActivityPhotosBinding : FlickrActivityPhotosBinding
        val photoAdapter = PhotoAdapter()
        val search_et = findViewById(R.id.search_editText) as EditText
        val string = search_et.text.toString()
        photosRecyclerView.adapter = photosViewModel.photosAdapter
        photosRecyclerView.layoutManager = GridLayoutManager(this, 3)

        Log.i("0000","Working")
        val button = findViewById(R.id.button_search) as Button
        button.setOnClickListener {
            photosViewModel.loadPhotos(string).observe(this,
                Observer<List<Photo>> { list ->
                    with(photosViewModel.photosAdapter) {
                        photoAdapter.submitList(list)
                        //photos.clear()
                        //photos.addAll(list)
                        notifyDataSetChanged()
                    }
                })
        }

        Log.i("0000", "Worked")
        /*photosViewModel.photosListLiveData.observe(this, Observer <List<Photo>>{list ->
            photoAdapter.photos.addAll(list)

        })*/
    }
}