package com.example.mediagallery.photoEditor

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.camera.Camera
import com.example.mediagallery.databinding.ActivityPreviewBinding
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.photoEditor.adapter.FilterAdapter
import com.example.mediagallery.photoEditor.fragment.BrushFragment
import com.example.mediagallery.photoEditor.fragment.EmojiBSFragment
import com.example.mediagallery.photoEditor.fragment.TextEditorDialogFragment
import com.example.mediagallery.viewmodel.ImageViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import ja.burhanrashid52.photoeditor.*
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.coroutines.delay
import java.io.File

open class PreviewActivity : AppCompatActivity(),OnPhotoEditorListener, EmojiBSFragment.EmojiListener, FilterAdapter.FilterListener,
    BrushFragment.Properties {
    private lateinit var viewModel: ImageViewModel
    private lateinit var binding: ActivityPreviewBinding
    private lateinit var mPhotoEditor: PhotoEditor
    private lateinit var mEmojiBSFragment: EmojiBSFragment
    private lateinit var mEmojiTypeFace : Typeface
    private lateinit var mFilterAdapter: FilterAdapter
    private lateinit var mProgressDialog : ProgressDialog
    private lateinit var mBrushFragment: BrushFragment


    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        viewModel = ViewModelProvider(this).get(ImageViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)
        mEmojiBSFragment = EmojiBSFragment()
        mBrushFragment = BrushFragment()

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
        mBrushFragment.setPropertiesChangeListener(this)

        binding.addText.setOnClickListener {
            binding.rvFilter.visibility = View.GONE
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
            binding.rvFilter.visibility = View.GONE
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
            showSaveDialog()
            //binding.close.visibility = View.GONE
        }

        binding.save.setOnClickListener {
            saveImage()
        }

        binding.addBrush.setOnClickListener {
            binding.rvFilter.visibility = View.GONE
            mPhotoEditor.setBrushDrawingMode(true)
            mBrushFragment.show(supportFragmentManager, mBrushFragment.tag)
        }

        binding.undo.setOnClickListener {
            mPhotoEditor.undo()
        }

        binding.redo.setOnClickListener {
            mPhotoEditor.redo()
        }
    }

    fun saveImage(){
        val file: File = intent.extras?.get("file") as File
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        } else{

            val saveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(true)
                .build()

            mPhotoEditor.saveAsFile(file.absolutePath, saveSettings, object : PhotoEditor.OnSaveListener{
                override fun onSuccess(imagePath: String) {
                    insert_to_database(file)
                }
                override fun onFailure(exception: Exception) {
                    showSnackbar("Failed to save image")
                }
            })
        }
    }

    fun showSaveDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit without saving?")
        builder.setPositiveButton("Save"){dialog, which ->
            saveImage()
        }
        builder.setNegativeButton("Yes"){dialog, which ->
            finish()
        }
        builder.setNeutralButton("Cancel"){dialog, which ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    fun showLoading(@NonNull message: String?) {
        mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage(message)
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        mProgressDialog.setCancelable(false)
        mProgressDialog.show()
    }

    fun hideLoading() {
        mProgressDialog.dismiss()
    }

    protected fun showSnackbar(@NonNull message: String?) {
        val view: View = findViewById(android.R.id.content)
        if (message != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }
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
                showLoading("Saving...")
                showSnackbar("Image saved successfully")
                startActivity(Intent(this, Camera::class.java))
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

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor.brushSize = brushSize.toFloat()
    }

    override fun onBackPressed() {
        showSaveDialog()
    }
}