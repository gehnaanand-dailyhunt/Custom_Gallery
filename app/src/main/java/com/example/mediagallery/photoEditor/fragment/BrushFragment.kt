package com.example.mediagallery.photoEditor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.example.mediagallery.R
import com.example.mediagallery.photoEditor.adapter.ColorPickerAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BrushFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener{

    lateinit var mProperties: Properties

    interface Properties{
        fun onColorChanged(colorCode: Int)
        fun onOpacityChanged(opacity: Int)
        fun onBrushSizeChanged(brushSize: Int)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.photo_editor_brush, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvColor = view.findViewById(R.id.rvColors) as RecyclerView
        val sbOpacity = view.findViewById(R.id.sbOpacity) as SeekBar
        val sbBrushSize = view.findViewById(R.id.sbSize) as SeekBar

        sbOpacity.setOnSeekBarChangeListener(this)
        sbBrushSize.setOnSeekBarChangeListener(this)

        rvColor.setHasFixedSize(true)
        val colorPickerAdapter = activity?.let { ColorPickerAdapter(it) }
        colorPickerAdapter?.setOnColorPickerClickListener { colorCode ->
            dismiss()
            mProperties.onColorChanged(colorCode)
        }
        rvColor.adapter = colorPickerAdapter
    }

    fun setPropertiesChangeListener(properties: Properties){
        mProperties = properties
    }
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar != null) {
            when(seekBar.id){
                R.id.sbOpacity -> {
                    mProperties.onOpacityChanged(progress)
                }
                R.id.sbSize -> {
                    mProperties.onBrushSizeChanged(progress)
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

}

