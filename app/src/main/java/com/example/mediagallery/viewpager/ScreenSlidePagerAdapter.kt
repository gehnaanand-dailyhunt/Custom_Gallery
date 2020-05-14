package com.example.mediagallery.viewpager

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.model.GalleryPicture
import kotlinx.android.extensions.LayoutContainer
import okio.blackholeSink

class ScreenSlidePagerAdapter(private val context: Context, private val galleryPicture: List<GalleryPicture>) : RecyclerView.Adapter<ImageViewHolder>()
{
    private lateinit var onClickLike : (GalleryPicture) -> Unit
    //private lateinit var onClickLike2 : (GalleryPicture) -> Unit
    private lateinit var onClickPlay : (String) -> Unit
    private lateinit var onClickEdit : (String) -> Unit

    //var flag = 0

    fun setOnClickListenerLike(onClick: (GalleryPicture) -> Unit){
        //flag = 0
        Log.i("000000000","00000000")
        this.onClickLike = onClick
    }

    fun setOnClickListenerEdit(onClick: (String) -> Unit) {
        this.onClickEdit = onClick
    }
    /*fun setOnClickListenerLike2(onClick: (GalleryPicture) -> Unit){
        flag = 1
        this.onClickLike2 = onClick
    }*/
    fun setOnClickListenerVideo(onClick: (String) -> Unit){
        this.onClickPlay = onClick
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {

        val holder =  ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.detail_image,
                parent,
                false
            )
        )
        holder.play_button.setOnClickListener {
            val video = galleryPicture[holder.adapterPosition]
            onClickPlay(video.contentUri)
        }
        return holder
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = galleryPicture[position]
        holder.galleryPicture = image
        holder.pos = position
        //holder.rootView.tag = image
        holder.image_title.text = image.title
        holder.image_tags.text = image.tag

        if(image.isLiked){
            holder.image_like.setImageResource(R.drawable.ic_toast_like)
        }else {
            holder.image_like.setImageResource(R.drawable.ic_toast_unlike)
        }

        Log.i("DDDD", position.toString())
        holder.image_like.setOnClickListener {
            //if(flag == 0) {
                image.isLiked = !image.isLiked
                onClickLike(image)
                Log.i("1111111","11111111")
                notifyDataSetChanged()
            /*} else {
                if(!image.isLiked) {
                    image.isLiked = !image.isLiked
                    onClickLike2(image)
                    notifyDataSetChanged()
                    flag = 2
                }
            }*/
        }

        holder.edit.setOnClickListener {
            onClickEdit(image.contentUri)
        }

        Glide.with(holder.imageView)
            .load(image.contentUri)
            .into(holder.imageView)

        val uri = galleryPicture[position].contentUri
        val index = uri.lastIndexOf(".")
        if(index > 0 && uri.substring(index) == ".mp4")
            holder.play_button.visibility = View.VISIBLE
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        (context as ScreenSlidePagerActivity).setInitialPos()
    }

    override fun getItemCount(): Int {
        return galleryPicture.size
    }
}
class ImageViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer
{
    val rootView = containerView
    lateinit var galleryPicture: GalleryPicture
    val imageView: ImageView = containerView.findViewById(R.id.image)
    var pos:Int? = null
    val image_title: TextView = containerView.findViewById(R.id.title)
    val image_tags: TextView = containerView.findViewById(R.id.tags)
    val image_like: ImageView = containerView.findViewById(R.id.like)
    val play_button: ImageButton = containerView.findViewById(R.id.play_button)
    val edit: ImageButton = containerView.findViewById(R.id.edit)
}