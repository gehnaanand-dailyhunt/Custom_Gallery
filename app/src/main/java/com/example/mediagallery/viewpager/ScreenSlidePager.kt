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


/**
 * The number of pages (wizard steps) to show in this demo.
 */
private const val NUM_PAGES = 5

class ScreenSlidePagerActivity : AppCompatActivity() {

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private lateinit var viewPager: ViewPager2

    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_pager)
        Log.i("DDDD", "adapter")

        viewPager = findViewById(R.id.pager)
        viewPager.setPageTransformer(ZoomOutPageTransformer())

        viewModel.images.observe(this, Observer<List<GalleryPicture>> { images ->
            viewPager.adapter =ScreenSlidePagerAdapter(this,images)

        })
    }

    fun setInitialPos() {
        val pos: Int = intent.getIntExtra("position", 0)

        Log.i("DDDD", "POS= " + pos)
        if(pos!=0) {
            viewPager.setCurrentItem(pos,false)

        }

    }
    private inner class ScreenSlidePagerAdapter(
        private val context: Context,
        private val mediaStoreImage: List<GalleryPicture>
    ) : RecyclerView.Adapter<ImageViewHolder>() {
        private val flag: Boolean = true
        private val inflater: LayoutInflater
        init {

            inflater = LayoutInflater.from(context)

        }

        override fun getItemCount(): Int = mediaStoreImage.size
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

            val vh = ImageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.view_pager,
                    parent,
                    false
                )
            )
            Log.i("DDDD","2")
            if(flag)
            {
            }
            return vh
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

            Log.i("DDDD","1")
            val image = mediaStoreImage[position]


            Glide.with(holder.imageView)
                .load(image.contentUri)
                .thumbnail(0.33f)
                .centerCrop()
                .into(holder.imageView)
        }



        override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
            super.onAttachedToRecyclerView(recyclerView)
            Log.i("DDDD","-----------------------")
            (context as ScreenSlidePagerActivity).setInitialPos()
        }


    }


    class ImageViewHolder( containerView: View) :
        RecyclerView.ViewHolder(containerView)
    {
        val imageView: ImageView = containerView.findViewById(R.id.image)

    }

}