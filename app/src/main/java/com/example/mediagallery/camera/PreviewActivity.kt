package com.example.mediagallery.camera

import android.graphics.Typeface
import android.graphics.fonts.FontFamily
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.databinding.ActivityPreviewBinding
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.viewmodel.ImageViewModel
import com.google.android.material.textfield.TextInputLayout
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.ViewType
import java.io.File

class PreviewActivity : AppCompatActivity(),OnPhotoEditorListener {
    private lateinit var viewModel: ImageViewModel
    private lateinit var binding: ActivityPreviewBinding
    private lateinit var mPhotoEditor: PhotoEditor


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)

        val uri = intent.getStringExtra("uri")
        Glide.with(binding.photoEditorView)
            .load(uri)
            .into(binding.photoEditorView.source)

        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium)
        val mEmojiTypeFace = Typeface.createFromAsset(assets, "emojione-android.ttf")

        mPhotoEditor = PhotoEditor.Builder(this, binding.photoEditorView)
            .setPinchTextScalable(true)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mEmojiTypeFace)
            .build()
        //mPhotoEditor.setOnPhotoEditorListener(this)
        //mPhotoEditor.setBrushDrawingMode(true)
        Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show()

        binding.addText.setOnClickListener {
            //binding.addEditText.visibility = View.VISIBLE
            //var text: String = binding.addEditText.text.toString()
            //mPhotoEditor.addText(text, R.color.blue_color_picker)
            val textEditorDialogFragment = TextEditorDialogFragment.show(this)
            textEditorDialogFragment.setOnTextEditorListener(object: TextEditorDialogFragment.TextEditor{
                override fun onDone(inputText: String, colorCode: Int) {
                    mPhotoEditor.addText(inputText, colorCode)
                }
            })
        }

        //mPhotoEditor.setOnPhotoEditorListener(this)
        /*mPhotoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener{
            override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
                if (rootView != null) {
                    mPhotoEditor.editText(rootView, text, colorCode)
                }
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
        })*/
    }


    override fun onEditTextChangeListener(rootView: View, text: String, colorCode: Int){
        val textEditorDialogFragment = TextEditorDialogFragment.show(this, text, colorCode)
        textEditorDialogFragment.setOnTextEditorListener(object : TextEditorDialogFragment.TextEditor {

            override fun onDone(inputText: String, colorCode: Int) {
                mPhotoEditor.editText(rootView, inputText, colorCode)
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
}