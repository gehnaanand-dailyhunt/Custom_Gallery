package com.example.mediagallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.multi_gallery_listitem.*
import com.bumptech.glide.Glide
import com.example.mediagallery.GlideApp
import com.example.mediagallery.R
import com.example.mediagallery.model.GalleryPicture

class GalleryAdapter(private val list: List<GalleryPicture>) : RecyclerView.Adapter<GVH>() {

    private lateinit var onClick: (GalleryPicture) -> Unit
    fun setOnClickListener(onClick: (GalleryPicture) -> Unit) {
        this.onClick = onClick
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): GVH {
        val vh = GVH(LayoutInflater.from(p0.context).inflate(R.layout.multi_gallery_listitem, p0, false))
        return vh
    }

    override fun onBindViewHolder(p0: GVH, p1: Int) {
        val picture = list[p1]
        Glide.with(p0.containerView).load(picture.path).into(p0.ivImg)

        if (picture.isSelected) {
            p0.vSelected.visibility = View.VISIBLE
        } else {
            p0.vSelected.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
            return list.size
    }
}