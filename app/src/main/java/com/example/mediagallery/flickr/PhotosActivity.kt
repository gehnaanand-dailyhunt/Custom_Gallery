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
import com.example.mediagallery.CustomGalleryActivity
import com.example.mediagallery.LikedActivity
import com.example.mediagallery.MainActivity
import com.example.mediagallery.R
import com.example.mediagallery.adapter.CustomGalleryAdapter
import com.example.mediagallery.camera.Camera
import com.example.mediagallery.databinding.FlickrActivityPhotosBinding
import com.example.mediagallery.flickr.adapter.PhotoAdapter
//import com.example.mediagallery.databinding.FlickrActivityPhotosBinding
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.viewmodel.PhotosViewModel
import com.example.mediagallery.utils.SpaceItemDecoration
import com.example.mediagallery.viewpager.ScreenSlidePagerActivity
import kotlinx.android.synthetic.main.flickr_activity_photos.*


class PhotosActivity : AppCompatActivity() {
    private lateinit var query_string : String
    private val photosViewModel: PhotosViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flickr_activity_photos)

        val binding : FlickrActivityPhotosBinding = DataBindingUtil.setContentView(this, R.layout.flickr_activity_photos)

        binding.navView.selectedItemId = R.id.nav_flickr_gallery
        binding.navView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.nav_gallery -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                R.id.nav_flickr_gallery -> {
                    startActivity(Intent(this, PhotosActivity::class.java))
                }
                R.id.nav_custom_gallery -> {
                    startActivity(Intent(this, CustomGalleryActivity::class.java))
                }
                R.id.nav_like -> {
                    startActivity(Intent(this, LikedActivity::class.java))
                }

            }
            return@setOnNavigationItemSelectedListener true
        }

        val photoAdapter = PhotoAdapter()
        binding.photosRecyclerView.adapter = photoAdapter
        binding.photosRecyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.photosRecyclerView.addItemDecoration(
            SpaceItemDecoration(4)
        )

        query_string = "HD Wallpaper"
        photosViewModel.loadPhotos(query_string).observe(this, Observer{ list ->
            photoAdapter.submitList(list)
        })

        photoAdapter.setOnClickListenerImage{_, pos ->
            val intent = Intent(this, ScreenSlidePagerActivity::class.java)
            intent.putExtra("Activity", 4)
            intent.putExtra("position", pos)
            intent.putExtra("Query", query_string)
            startActivity(intent)
        }

        /*photoAdapter.setOnClickListenerLike { galleryPicture ->
            photosViewModel.insert(galleryPicture)
            photosViewModel.update(galleryPicture)
        }*/

        binding.buttonSearch.setOnClickListener {
            val search_et = findViewById<EditText>(R.id.editText)
            val string = search_et.text.toString()
            query_string = string
            Toast.makeText(this,string,Toast.LENGTH_SHORT).show()

            photosViewModel.loadPhotos(query_string).observe(this, Observer { list ->
                photoAdapter.submitList(list)
            })
        }

        binding.fabLayout.fabCamera.setOnClickListener {
            startActivity(Intent(this, Camera::class.java))
        }
    }
}