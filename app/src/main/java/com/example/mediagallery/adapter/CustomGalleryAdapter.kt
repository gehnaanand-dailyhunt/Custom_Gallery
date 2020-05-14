package com.example.mediagallery.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.model.GalleryPicture
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.post_list_item.view.*

class CustomGalleryAdapter : ListAdapter<GalleryPicture, ImageViewHolder2>(GalleryPicture.DiffCallback){
    private lateinit var onClickImage : (GalleryPicture, Int) -> Unit
    private lateinit var onClickLike : (GalleryPicture) -> Unit
    private lateinit var onClickPlay : (String) -> Unit

    fun setOnClickListenerImage(onClick: (GalleryPicture, Int) -> Unit){
        this.onClickImage = onClick
    }

    fun setOnClickListenerLike(onClick: (GalleryPicture) -> Unit){
        this.onClickLike = onClick
    }

    fun setOnClickListenerVideo(onClick: (String) -> Unit){
        this.onClickPlay = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder2 {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.post_list_item, parent, false)
        val holder = ImageViewHolder2(view)

        holder.imageView.setOnClickListener {
            val position = holder.adapterPosition
            val image = getItem(position)
            onClickImage(image, position)
        }

        holder.play_button.setOnClickListener {
            val position = holder.adapterPosition
            val video = getItem(position)
            onClickPlay(video.contentUri)
            Log.i("----------------","Success")
        }
        return holder
    }

    override fun onBindViewHolder(holder: ImageViewHolder2, position: Int) {
        val galleryPicture = getItem(position)
        holder.pos = position
        holder.rootView.tag = galleryPicture
        holder.title.text = galleryPicture.title
        holder.tags.text = galleryPicture.tag

        if(galleryPicture.isLiked){
            holder.like.setImageResource(R.drawable.ic_toast_like)
        }else {
            holder.like.setImageResource(R.drawable.ic_toast_unlike)
        }

        holder.like.setOnClickListener {
            galleryPicture.isLiked = !galleryPicture.isLiked
            onClickLike(galleryPicture)
            notifyDataSetChanged()
        }

        val uri = galleryPicture.contentUri
        val index = uri.lastIndexOf(".")
        if(index > 0 && uri.substring(index) == ".mp4")
            holder.play_button.visibility = View.VISIBLE
        else
            holder.play_button.visibility = View.GONE

        Glide.with(holder.imageView)
            .load(galleryPicture.contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)
    }


}
class ImageViewHolder2(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{
    val rootView = containerView
    var pos: Int? = null
    val imageView: ImageView = containerView.findViewById(R.id.image)
    val title: TextView = containerView.findViewById(R.id.title)
    val tags: TextView = containerView.findViewById(R.id.tags)
    val like: ImageView = containerView.findViewById(R.id.like)
    val play_button: ImageButton = containerView.findViewById(R.id.play_button)
}