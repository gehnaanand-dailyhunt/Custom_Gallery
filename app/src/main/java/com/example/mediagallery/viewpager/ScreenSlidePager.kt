package com.example.mediagallery.viewpager

import ZoomOutPageTransformer
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.viewmodel.PhotosViewModel
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.viewmodel.GalleryViewModel
import com.example.mediagallery.viewmodel.ImageViewModel
import kotlinx.android.synthetic.main.post_list_item.*


class ScreenSlidePagerActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private val viewModel: GalleryViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        var screenSlidePagerAdapter: ScreenSlidePagerAdapter
        viewPager = findViewById(R.id.pager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        val activity_name = intent.getIntExtra("Activity", 1)
        when(activity_name){
            //Main Activity
            1 -> {
                viewModel.loadImages()
                viewModel.images.observe(this, Observer<List<GalleryPicture>> { images ->
                    viewPager.adapter = ScreenSlidePagerAdapter(this, images)
                })
            }
            //Custom Gallery
            2 -> {
                imageViewModel.allPhotos.observe(this, Observer<List<GalleryPicture>>{ images ->
                    screenSlidePagerAdapter = ScreenSlidePagerAdapter(this,images)
                    viewPager.adapter = screenSlidePagerAdapter
                    screenSlidePagerAdapter.setOnClickListenerLike { image ->
                        imageViewModel.update(image)
                    }

                })
            }
            //Liked Activity
            3 -> {
                imageViewModel.likedPhotos.observe(this, Observer<List<GalleryPicture>>{ images ->
                    screenSlidePagerAdapter = ScreenSlidePagerAdapter(this,images)
                    viewPager.adapter = screenSlidePagerAdapter
                    screenSlidePagerAdapter.setOnClickListenerLike { image ->
                        imageViewModel.update(image)
                    }

                })
            }
        }
    }

    fun setInitialPos() {
        val pos: Int = intent.getIntExtra("position", 0)
        if(pos!= 0) {
            viewPager.setCurrentItem(pos,false)
        }
    }
}