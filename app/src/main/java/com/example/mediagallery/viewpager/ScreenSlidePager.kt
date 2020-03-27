package com.example.mediagallery.viewpager

import ZoomOutPageTransformer
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.viewmodel.GalleryViewModel


class ScreenSlidePagerActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        viewPager = findViewById(R.id.pager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        viewModel.loadImages()
        viewModel.images.observe(this, Observer<List<GalleryPicture>> { images ->
            viewPager.adapter = ScreenSlidePagerAdapter(this, images)

        })
    }

    fun setInitialPos() {
        val pos: Int = intent.getIntExtra("position", 0)

        if(pos!= 0) {
            viewPager.setCurrentItem(pos,false)

        }

    }

}