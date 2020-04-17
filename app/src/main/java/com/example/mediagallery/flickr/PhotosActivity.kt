package com.example.mediagallery.flickr

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mediagallery.R
import com.example.mediagallery.databinding.FlickrActivityPhotosBinding
import com.example.mediagallery.flickr.adapter.PhotoAdapter
//import com.example.mediagallery.databinding.FlickrActivityPhotosBinding
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.viewmodel.PhotosViewModel
import com.example.mediagallery.utils.SpaceItemDecoration
import com.example.mediagallery.viewpager.ScreenSlidePagerActivity
import kotlinx.android.synthetic.main.flickr_activity_photos.*


class PhotosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flickr_activity_photos)
        val photosViewModel: PhotosViewModel by viewModels()
        val binding : FlickrActivityPhotosBinding = DataBindingUtil.setContentView(this, R.layout.flickr_activity_photos)

        binding.buttonSearch.setOnClickListener {
            val search_et = findViewById<EditText>(R.id.editText)
            val string = search_et.text.toString()
            val photoAdapter = PhotoAdapter()

            binding.photosRecyclerView.adapter = photoAdapter
            binding.photosRecyclerView.layoutManager = GridLayoutManager(this, 3)
            Toast.makeText(this,string,Toast.LENGTH_SHORT).show()
            binding.photosRecyclerView.addItemDecoration(
                SpaceItemDecoration(4)
            )
            photosViewModel.loadPhotos(string).observe(this, Observer { list ->
                photoAdapter.submitList(list)
            })

        }

    }
}