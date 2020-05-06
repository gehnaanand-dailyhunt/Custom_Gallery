package com.example.mediagallery.camera

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mediagallery.R
import com.example.mediagallery.adapter.ColorPickerAdapter
//import com.example.mediagallery.databinding.AddTextDialogBinding

class TextEditorDialogFragment : DialogFragment() {
    //private lateinit var binding: AddTextDialogBinding
    private lateinit var inputMethodManager : InputMethodManager
    private var mColorCode: Int = 1
    private lateinit var mTextEditor: TextEditor
    private lateinit var addTextEditText: EditText
    private lateinit var addTextDoneTextView : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding = DataBindingUtil.inflate(inflater, R.layout.photo_editor_add_text_dialog, container, false)
        //return binding.root
        return inflater.inflate(R.layout.photo_editor_add_text_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        addTextEditText = view.findViewById(R.id.add_text_edit_text)
        addTextDoneTextView = view.findViewById(R.id.add_text_done_tv)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        val addTextColorPickerRecyclerView = view.findViewById<RecyclerView>(R.id.add_text_color_picker_recycler_view)
        addTextColorPickerRecyclerView.layoutManager = linearLayoutManager
        addTextColorPickerRecyclerView.setHasFixedSize(true)
        val colorPickerAdapter = ColorPickerAdapter(activity!!)

        colorPickerAdapter.setOnColorPickerClickListener { colorCode ->
            mColorCode = colorCode
            addTextEditText.setTextColor(colorCode)
        }
        addTextColorPickerRecyclerView.adapter = colorPickerAdapter
        addTextEditText.setText(arguments?.getString(EXTRA_INPUT_TEXT))
        mColorCode = arguments?.getInt(EXTRA_COLOR_CODE)!!
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        addTextDoneTextView.setOnClickListener { v ->
            Log.i("Check", "222222222")
            if (v != null) {
                Log.i("Check", "44444444444")
                inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
            }
            dismiss()
            val inputText = addTextEditText.text.toString()
            Log.i("Check", inputText)
            if(!TextUtils.isEmpty(inputText)){
                Log.i("Check", mColorCode.toString())

                Log.i("Check", mColorCode.toString())
                mTextEditor.onDone(inputText, mColorCode)
                Log.i("Check", "3333333333")
            }
        }
    }

    fun setOnTextEditorListener(textEditor: TextEditor){
        mTextEditor = textEditor
    }

    interface TextEditor {
        fun onDone(inputText: String, colorCode: Int)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if(dialog!= null){
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width,height)
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        }
    }


    companion object{
        var EXTRA_INPUT_TEXT: String = "extra_input_text"
        var EXTRA_COLOR_CODE: String = "extra_color_code"
        var TAG = TextEditorDialogFragment.javaClass.simpleName
        fun show(@NonNull appCompatActivity: AppCompatActivity, @NonNull inputText: String, @ColorInt colorCode: Int) : TextEditorDialogFragment{
            val args: Bundle = Bundle()
            args.putString(EXTRA_INPUT_TEXT, inputText)
            args.putInt(EXTRA_COLOR_CODE, colorCode)
            val textEditorDialogFragment = TextEditorDialogFragment()
            textEditorDialogFragment.arguments = args
            textEditorDialogFragment.show(appCompatActivity.supportFragmentManager, TAG)
            return textEditorDialogFragment
        }

        fun show(@NonNull appCompatActivity: AppCompatActivity) : TextEditorDialogFragment{
            return show(appCompatActivity, "", ContextCompat.getColor(appCompatActivity, R.color.white))
        }
    }
}