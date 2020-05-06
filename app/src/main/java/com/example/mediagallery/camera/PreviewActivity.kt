package com.example.mediagallery.camera

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.adapter.FilterAdapter
import com.example.mediagallery.databinding.ActivityPreviewBinding
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.viewmodel.ImageViewModel
import com.google.android.material.textfield.TextInputLayout
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter
import ja.burhanrashid52.photoeditor.ViewType
import java.io.File

class PreviewActivity : AppCompatActivity(),OnPhotoEditorListener, EmojiBSFragment.EmojiListener, FilterAdapter.FilterListener {
    private lateinit var viewModel: ImageViewModel
    private lateinit var binding: ActivityPreviewBinding
    private lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mEmojiBSFragment: EmojiBSFragment
    lateinit var mEmojiTypeFace : Typeface
    private lateinit var mFilterAdapter: FilterAdapter


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeFullScreen()
        setContentView(R.layout.activity_preview)

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)
        mEmojiBSFragment = EmojiBSFragment()

        val uri = intent.getStringExtra("uri")
        Glide.with(binding.photoEditorView)
            .load(uri)
            .into(binding.photoEditorView.source)

        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium)
        mEmojiTypeFace = Typeface.createFromAsset(assets, "NotoColorEmoji.ttf")

        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mEmojiTypeFace)
            .build()
        //mPhotoEditor.setOnPhotoEditorListener(this)

        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()

        mEmojiBSFragment.setEmojiListener(this)

        binding.addText.setOnClickListener {
            val textEditorDialogFragment = TextEditorDialogFragment.show(this)
            textEditorDialogFragment.setOnTextEditorListener(object: TextEditorDialogFragment.TextEditor{
                override fun onDone(inputText: String, colorCode: Int) {
                    mPhotoEditor.addText(inputText, colorCode)
                }
            })
        }

        binding.addEmoji.setOnClickListener {
            //val emojiList: ArrayList<String> = PhotoEditor.getEmojis(getActivity(this))
            //mPhotoEditor.addEmoji(mEmojiTypeFace, emojiList[0])
            mEmojiBSFragment.show(supportFragmentManager, mEmojiBSFragment.tag)
        }

        mFilterAdapter = FilterAdapter(this)
        binding.rvFilter.adapter = mFilterAdapter
        binding.addFilter.setOnClickListener {
            binding.rvFilter.visibility = View.VISIBLE
            binding.close.visibility = View.VISIBLE
        }

        binding.close.setOnClickListener {
            binding.rvFilter.visibility = View.GONE
            binding.close.visibility = View.GONE
        }

    }

    fun makeFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int){
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {

            override fun onDone(inputText: String, colorCode: Int) {
                Log.i("Check", "0000000000000")
                mPhotoEditor.editText(rootView, inputText, colorCode)
                Log.i("Check", "11111111111")
            }
        })
    }

    override fun onStartViewChangeListener(viewType: ViewType?) {
        TODO("Not yet implemented")
    }

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        TODO("Not yet implemented")
    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        TODO("Not yet implemented")
    }

    override fun onStopViewChangeListener(viewType: ViewType?) {
        TODO("Not yet implemented")
    }

    override fun onEmojiClick(emojiUnicode: String) {
        mPhotoEditor.addEmoji(mEmojiTypeFace, emojiUnicode)
    }

    fun insert_to_database(image_file : File){
        val custLayout = LayoutInflater.from(this).inflate(R.layout.text_input_dialog,null)
        val image_title: TextInputLayout = custLayout.findViewById(R.id.title)
        val image_tag : TextInputLayout = custLayout.findViewById(R.id.tag)
        val builder = AlertDialog.Builder(this).setView(custLayout)
            .setPositiveButton("Save"){dialog, _ ->
                val title = image_title.editText!!.text.toString()
                val tag = image_tag.editText!!.text.toString()
                val uri = image_file.absolutePath
                val date = image_file.lastModified()
                val galleryPicture = GalleryPicture(0, title, tag, date, uri)
                viewModel.insert(galleryPicture)
                dialog.dismiss()
                Toast.makeText(this, title+" "+ date, Toast.LENGTH_SHORT).show()

            }
            .setNegativeButton("Cancel"){ dialog, _ ->
                image_file.delete()
                dialog.cancel()
            }
        builder.show()
        return
    }

    override fun onFilterSelected(photoFilter: PhotoFilter?) {
        mPhotoEditor.setFilterEffect(photoFilter)
    }


}