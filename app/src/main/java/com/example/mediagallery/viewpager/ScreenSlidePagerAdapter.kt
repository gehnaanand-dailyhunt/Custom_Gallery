package com.example.mediagallery.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.model.GalleryPicture

class ScreenSlidePagerAdapter(private val context: Context, private val galleryPicture: List<GalleryPicture>) : RecyclerView.Adapter<ImageViewHolder>() {
    private val flag: Boolean = true
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = galleryPicture.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val vh = ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.detail_image,
                parent,
                false
            )
        )
        return vh
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        val image = galleryPicture[position]
        Glide.with(holder.imageView)
            .load(image.contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (context as ScreenSlidePagerActivity).setInitialPos()
    }
}
class ImageViewHolder( containerView: View) : RecyclerView.ViewHolder(containerView)
{
    val imageView: ImageView = containerView.findViewById(R.id.image)
}