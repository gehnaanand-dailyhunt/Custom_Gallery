package com.example.mediagallery.viewpager

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.model.GalleryPicture
import kotlinx.android.extensions.LayoutContainer

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
        /*val galleryPicture = getItem(position)
        holder.pos = position
        holder.rootView.tag = galleryPicture
        holder._title.text = galleryPicture.title
        holder._tags.text = galleryPicture.tag

        if(galleryPicture.isLiked){
            holder._like.setImageResource(R.drawable.ic_toast_like)
        }else {
            holder._like.setImageResource(R.drawable.ic_toast_unlike)
        }

        holder.like.setOnClickListener {
            galleryPicture.isLiked = !galleryPicture.isLiked
            onClickLike(galleryPicture)
            notifyDataSetChanged()
        }*/

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
class ImageViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
{
    val imageView: ImageView = containerView.findViewById(R.id.image)

}