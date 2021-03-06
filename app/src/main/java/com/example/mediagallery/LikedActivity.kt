package com.example.mediagallery

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediagallery.adapter.CustomGalleryAdapter
import com.example.mediagallery.camera.Camera
import com.example.mediagallery.databinding.ActivityMainBinding
import com.example.mediagallery.flickr.PhotosActivity
import com.example.mediagallery.utils.SpaceItemDecoration
import com.example.mediagallery.viewmodel.ImageViewModel
import com.example.mediagallery.viewpager.ScreenSlidePagerActivity

class LikedActivity: AppCompatActivity() {
    private lateinit var viewModel: ImageViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.navView.selectedItemId = R.id.nav_like
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

        val customGalleryAdapter = CustomGalleryAdapter()
        binding.rv.layoutManager = GridLayoutManager(this,2)
        binding.rv.adapter = customGalleryAdapter
        binding.rv.addItemDecoration(
            SpaceItemDecoration(4)
        )

        viewModel.likedPhotos.observe(this, Observer { photos ->
            customGalleryAdapter.submitList(photos)
        })

        customGalleryAdapter.setOnClickListenerImage{ _, pos ->
            val intent = Intent(this , ScreenSlidePagerActivity::class.java)
            intent.putExtra("Activity",3)
            intent.putExtra("position", pos)
            startActivity(intent)
        }

        customGalleryAdapter.setOnClickListenerLike { galleryPicture ->
            viewModel.update(galleryPicture)
        }

        binding.fabLayout.fabCamera.setOnClickListener {
            startActivity(Intent(this, Camera::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.change_layout -> {
                if(item.title == "LIST"){
                    binding.rv.layoutManager = LinearLayoutManager(this)
                    item.title = "GRID"
                }else{
                    binding.rv.layoutManager = GridLayoutManager(this,2)
                    item.title = "LIST"
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}