package com.example.mediagallery.camera

import android.annotation.SuppressLint
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediagallery.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ja.burhanrashid52.photoeditor.PhotoEditor

class EmojiBSFragment() : BottomSheetDialogFragment() {

    lateinit var mEmojiListener : EmojiListener
    //lateinit var binding : PhotoEditorStickerEmojiDialogBinding
    lateinit var emojiUnicode: String

    interface EmojiListener {
        fun onEmojiClick(emojiUnicode: String)
    }

    /*private var mBottomSheetBehaviorCallback: BottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback(){
        override fun onSlide(bottomSheet: View, slideOffset: Float) {

        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if(newState == BottomSheetBehavior.STATE_HIDDEN){
                dismiss()
            }
        }
    }*/

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int){
        super.setupDialog(dialog, style)
        val contentView: View = View.inflate(context, R.layout.photo_editor_sticker_emoji_dialog, null)
        dialog.setContentView(contentView)

        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))

        val rvEmoji = contentView.findViewById(R.id.rvEmoji) as RecyclerView
        val gridLayoutManager = GridLayoutManager(activity, 5)
        rvEmoji.layoutManager = gridLayoutManager
        val adapter: EmojiAdapter = EmojiAdapter()
        rvEmoji.adapter = adapter

        /*adapter.setOnClickEmojiListener {
            emojiUnicode = it

        }*/
        //var params :CoordinatorLayout.LayoutParams = (CoordinatorLayout.LayoutParams)((View) contentView.getParent()).getLayoutParams()
    }

    fun setEmojiListener(emojiListener: EmojiListener){
        mEmojiListener = emojiListener
    }

    inner class EmojiAdapter : RecyclerView.Adapter<ViewHolder>() {

        var emojiList : ArrayList<String> = PhotoEditor.getEmojis(activity)
        /*private lateinit var onClick: (String) -> Unit

        fun setOnClickEmojiListener(onClick : (String) -> Unit){
            this.onClick = onClick
        }*/

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            //emojiList = PhotoEditor.getEmojis(parent.context)
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.photo_editor_row_emoji, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return emojiList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.txtEmoji.text = emojiList[position]

            holder.txtEmoji.setOnClickListener {
                mEmojiListener.onEmojiClick(emojiList[holder.layoutPosition])
                //onClick(emojiList[position])
                dismiss()
            }
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtEmoji : TextView = view.findViewById(R.id.txtEmoji)

    }
}