package com.example.mediagallery

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediagallery.adapter.GalleryAdapter
import com.example.mediagallery.adapter.SpaceItemDecoration
import com.example.mediagallery.databinding.ActivityMainBinding
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.viewmodel.GalleryViewModel
import com.example.mediagallery.viewpager.ScreenSlidePagerActivity
import kotlinx.android.synthetic.main.multi_gallery_listitem.*

class MainActivity : AppCompatActivity() {

    //private lateinit var adapter: GalleryAdapter
    private val galleryViewModel: GalleryViewModel by viewModels()

    //private lateinit var pictures: ArrayList<GalleryPicture>
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        //galleryViewModel = ViewModelProviders.of(this)[GalleryViewModel::class.java]
        val layoutManager = GridLayoutManager(this, 3)

        val galleryAdapter = GalleryAdapter()
        binding.rv.layoutManager = layoutManager

        binding.rv.adapter = galleryAdapter
        binding.rv.addItemDecoration(SpaceItemDecoration(8))
        //binding.rv.adapter = adapter

        galleryViewModel.images.observe(this, Observer <List<GalleryPicture>> { images ->
            galleryAdapter.submitList(images)
        } )

        galleryAdapter.setOnClickListener { galleryPicture, pos ->
            showToast(galleryPicture.contentUri.toString() + image.id)
            //Toast.makeText(this,"Hello",Toast.LENGTH_SHORT)
            val intent = Intent(this, ScreenSlidePagerActivity::class.java)
            intent.putExtra("position", pos);
            startActivity(intent)
        }
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
