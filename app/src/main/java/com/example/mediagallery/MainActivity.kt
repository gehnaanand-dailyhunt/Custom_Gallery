package com.example.mediagallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediagallery.adapter.CustomGalleryAdapter
import com.example.mediagallery.adapter.GalleryAdapter
import com.example.mediagallery.utils.SpaceItemDecoration
import com.example.mediagallery.camera.Camera
import com.example.mediagallery.databinding.ActivityMainBinding
import com.example.mediagallery.flickr.PhotosActivity
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.videoPlayer.VideoPlayerActivity
import com.example.mediagallery.viewmodel.GalleryVideoViewModel
import com.example.mediagallery.viewmodel.GalleryViewModel
import com.example.mediagallery.viewpager.ScreenSlidePagerActivity
import kotlinx.android.synthetic.main.multi_gallery_listitem.*

class MainActivity : AppCompatActivity() {

    //private lateinit var adapter: GalleryAdapter
    private val galleryViewModel: GalleryViewModel by viewModels()
    private val galleryVideoViewModel: GalleryVideoViewModel by viewModels()

    //private lateinit var pictures: ArrayList<GalleryPicture>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        binding.navView.selectedItemId = R.id.nav_gallery
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
        //galleryViewModel = ViewModelProviders.of(this)[GalleryViewModel::class.java]d
        val layoutManager = GridLayoutManager(this, 3)

        val galleryAdapter = GalleryAdapter()
        binding.rv.layoutManager = layoutManager

        binding.rv.adapter = galleryAdapter
        binding.rv.addItemDecoration(
            SpaceItemDecoration(
                8
            )
        )
        //binding.rv.adapter = adapter
        binding.fabLayout.fabCamera.setOnClickListener {
            startActivity(Intent(this, Camera::class.java))
        }

        galleryVideoViewModel.videos.observe(this, Observer {videos ->
            galleryAdapter.submitList(videos)
        })

        galleryViewModel.images.observe(this, Observer <List<GalleryPicture>> { images ->
            galleryAdapter.submitList(images)
        } )

        galleryAdapter.setOnClickListener { galleryPicture, pos ->
            showToast(galleryPicture.contentUri + image.id)
            //Toast.makeText(this,"Hello",Toast.LENGTH_SHORT)
            val intent = Intent(this, ScreenSlidePagerActivity::class.java)
            intent.putExtra("position", pos)
            intent.putExtra("Activity", 1)
            startActivity(intent)
        }

        galleryAdapter.setOnClickListenerVideo { uri ->
            val intent = Intent(this, VideoPlayerActivity::class.java)
            intent.putExtra("uri",uri)
            startActivity(intent)
        }

        /*galleryAdapter.setOnClickListenerLike { galleryPicture ->
            galleryViewModel.insert(galleryPicture)
            galleryViewModel.update(galleryPicture)
        }*/
        requestReadStoragePermission()
    }

    private fun requestReadStoragePermission() {
        val readStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                readStorage
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(readStorage), 3)
        } else loadPictures()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.change_layout -> {
                if(item.title == "LIST"){
                    //galleryViewModel.deleteAll()
                    binding.rv.layoutManager = LinearLayoutManager(this)
                    item.title = "GRID"
                }else{
                    binding.rv.layoutManager = GridLayoutManager(this,3)
                    item.title = "LIST"
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadPictures() {
        galleryViewModel.loadImages()
        galleryVideoViewModel.loadVideos()
    }

    private fun showToast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            loadPictures()
        else {
            showToast("Permission Required to Fetch Gallery.")
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.rv.adapter = null
    }
}
