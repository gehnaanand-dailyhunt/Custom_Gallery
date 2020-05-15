package com.example.mediagallery.viewpager

import ZoomOutPageTransformer
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.flickr.viewmodel.PhotosViewModel
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.photoEditor.PreviewActivity
import com.example.mediagallery.videoPlayer.VideoPlayerActivity
import com.example.mediagallery.viewmodel.GalleryViewModel
import com.example.mediagallery.viewmodel.ImageViewModel
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.post_list_item.*
import java.io.File
import java.util.*
import kotlin.Comparator


class ScreenSlidePagerActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    private val viewModel: GalleryViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()
    private val photosViewModel: PhotosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)

        var screenSlidePagerAdapter: ScreenSlidePagerAdapter
        viewPager = findViewById(R.id.pager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        activity_name = intent.getIntExtra("Activity", 1)
        when(activity_name){

            //Main Activity
            1 -> {
                //viewModel.loadImages()
                viewModel.images.observe(this, Observer<List<GalleryPicture>> { images ->
                    screenSlidePagerAdapter = ScreenSlidePagerAdapter(this, images)
                    viewPager.adapter = screenSlidePagerAdapter
                    screenSlidePagerAdapter.setOnClickListenerLike { image ->
                        Log.i("2222222","2222222")
                        viewModel.insert(image)
                        viewModel.update(image)
                        Log.i("3333333","33333333")
                    }

                    screenSlidePagerAdapter.setOnClickListenerVideo { uri ->
                        val intent = Intent(this, VideoPlayerActivity::class.java)
                        intent.putExtra("uri",uri)
                        startActivity(intent)
                    }

                    screenSlidePagerAdapter.setOnClickListenerEdit { uri ->
                        val file = File(externalMediaDirs.first(),
                            "${System.currentTimeMillis()}.jpg")
                        val intent = Intent(this, PreviewActivity::class.java)
                        intent.putExtra("uri", uri)
                        intent.putExtra("file", file)
                        startActivity(intent)
                    }
                    screenSlidePagerAdapter.setOnClickListenerShare { uri ->
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri))
                        startActivity(Intent.createChooser(intent, "Share to"))
                    }
                })

            }
            //Custom Gallery
            2 -> {
                imageViewModel.allPhotos.observe(this, Observer<List<GalleryPicture>>{ images ->
                    if(!images.isEmpty()) {
                        val images = images.sortedByDescending { it.dateTaken }
                        screenSlidePagerAdapter = ScreenSlidePagerAdapter(this, images)
                        viewPager.adapter = screenSlidePagerAdapter
                        screenSlidePagerAdapter.setOnClickListenerLike { image ->
                            imageViewModel.update(image)
                        }
                        screenSlidePagerAdapter.setOnClickListenerVideo { uri ->
                            val intent = Intent(this, VideoPlayerActivity::class.java)
                            intent.putExtra("uri", uri)
                            startActivity(intent)
                        }
                        screenSlidePagerAdapter.setOnClickListenerEdit { uri ->
                            val file = File(externalMediaDirs.first(),
                                "${System.currentTimeMillis()}.jpg")
                            val intent = Intent(this, PreviewActivity::class.java)
                            intent.putExtra("uri", uri)
                            intent.putExtra("file", file)
                            startActivity(intent)
                        }

                        screenSlidePagerAdapter.setOnClickListenerShare { uri ->
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "image/*"
                            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri))
                            startActivity(Intent.createChooser(intent, "Share to"))
                        }
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

                    screenSlidePagerAdapter.setOnClickListenerVideo { uri ->
                        val intent = Intent(this, VideoPlayerActivity::class.java)
                        intent.putExtra("uri",uri)
                        startActivity(intent)
                    }

                    screenSlidePagerAdapter.setOnClickListenerEdit { uri ->
                        val file = File(externalMediaDirs.first(),
                            "${System.currentTimeMillis()}.jpg")
                        val intent = Intent(this, PreviewActivity::class.java)
                        intent.putExtra("uri", uri)
                        intent.putExtra("file", file)
                        startActivity(intent)
                    }

                    screenSlidePagerAdapter.setOnClickListenerShare { uri ->
                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "image/*"
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri))
                        startActivity(Intent.createChooser(intent, "Share to"))
                    }

                })
            }
            //Flickr Activity
            4 -> {
                val string = intent.getStringExtra("Query")
                photosViewModel.loadPhotos(string)
                photosViewModel.images.observe(this, Observer { images ->
                    screenSlidePagerAdapter = ScreenSlidePagerAdapter(this, images)
                    viewPager.adapter = screenSlidePagerAdapter

                    screenSlidePagerAdapter.setOnClickListenerLike { image ->
                        photosViewModel.insert(image)
                        photosViewModel.update(image)
                    }

                    screenSlidePagerAdapter.setOnClickListenerEdit { uri ->
                        val file = File(externalMediaDirs.first(),
                            "${System.currentTimeMillis()}.jpg")
                        val intent = Intent(this, PreviewActivity::class.java)
                        intent.putExtra("uri", uri)
                        intent.putExtra("file", file)
                        startActivity(intent)
                    }

                })
            }
        }
    }

    /*override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.delete_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val delete = menu?.findItem(R.id.delete)
        if(activity_name == 2)
            if (delete != null) {
                delete.setVisible(true)
            }

        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete -> {
                val pos = intent.getIntExtra("position", 0)
                val builder = AlertDialog.Builder(this).setTitle("Delete")
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("OK") { dialog, which ->
                        imageViewModel.allPhotos.observe(this, Observer { image ->
                            val galleryPicture = image[pos]
                            imageViewModel.delete(galleryPicture)
                            dialog.dismiss()
                        })

                    }
                    .setNegativeButton("Cancel") { dialog, which ->
                        dialog.cancel()
                    }
                builder.show()
                imageViewModel.allPhotos.observe(this, Observer { images ->
                    val screenSlidePagerAdapter = ScreenSlidePagerAdapter(this, images)
                    viewPager.adapter = screenSlidePagerAdapter
                })
                viewPager.setCurrentItem(pos, false)
            }
        }
        return super.onOptionsItemSelected(item)
    }*/

    /*fun sort(images: List<GalleryPicture>) : Int{
        Collections.sort(images, object : Comparator<GalleryPicture> {
            override fun compare(o1: GalleryPicture?, o2: GalleryPicture?): Int {
                if (o1 != null) {
                    if (o2 != null) {
                        if (o1.dateTaken < o2.dateTaken) {
                            return -1
                        } else
                            return 1
                    }
                }
            }

        }) : Int
    }*/
    fun setInitialPos() {
        val pos: Int = intent.getIntExtra("position", 0)
        if(pos!= 0) {
            viewPager.setCurrentItem(pos,false)
        }
    }

    companion object{
        private var activity_name : Int = 0
    }
}