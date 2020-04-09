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
import com.example.mediagallery.utils.TopSpacingItemDecoration
import com.example.mediagallery.viewmodel.ImageViewModel
import com.example.mediagallery.viewpager.ScreenSlidePagerActivity

class CustomGalleryActivity : AppCompatActivity() {
    private lateinit var viewModel: ImageViewModel
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        val customGalleryAdapter = CustomGalleryAdapter()
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.rv.layoutManager = GridLayoutManager(this, 3)
        binding.rv.adapter = customGalleryAdapter
        binding.rv.addItemDecoration(
            TopSpacingItemDecoration(10)
        )
        viewModel.allPhotos.observe(this, Observer { photos ->
        customGalleryAdapter.submitList(photos)
        })

        customGalleryAdapter.setOnClickListenerImage { _, pos ->
            val intent = Intent(this, ScreenSlidePagerActivity::class.java)
            intent.putExtra("customGallery",true)
            intent.putExtra("position", pos)
            startActivity(intent)
        }

        customGalleryAdapter.setOnClickListenerLike {
            galleryPicture -> viewModel.update(galleryPicture)
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
                    binding.rv.layoutManager = GridLayoutManager(this,3)
                    item.title = "LIST"
                }
            }
            R.id.camera -> {
                val intent = Intent(this, Camera::class.java)
                startActivity(intent)
            }
            R.id.gallery -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            R.id.liked -> {
                startActivity(Intent(this, LikedActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}