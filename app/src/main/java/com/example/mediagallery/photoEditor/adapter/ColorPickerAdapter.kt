package com.example.mediagallery.photoEditor.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mediagallery.R

class ColorPickerAdapter : RecyclerView.Adapter<ColorPickerAdapter.ViewHolder>{
    private var inflater: LayoutInflater
    private var context: Context
    private var colorPickerColors: List<Int>
    private lateinit var onColorPickerClickListener: (Int) -> Unit

    fun setOnColorPickerClickListener(onClick : (Int) -> Unit){
        this.onColorPickerClickListener = onClick
    }

    constructor(@NonNull context: Context){
        this.context = context
        this.inflater = LayoutInflater.from(context)
        this.colorPickerColors =
            getDefaultColors(
                context
            )
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = inflater.inflate(R.layout.photo_editor_color_picker_item_list, parent, false)
        return ViewHolder(
            view
        )
    }

    companion object{
        fun getDefaultColors(context: Context): List<Int>{
            var colorPickerColors = arrayListOf<Int>()
            colorPickerColors.add(ContextCompat.getColor(context, R.color.blue_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.brown_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.green_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.orange_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.red_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.black))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.red_orange_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.sky_blue_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.violet_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.white))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_color_picker))
            colorPickerColors.add(ContextCompat.getColor(context, R.color.yellow_green_color_picker))
            return colorPickerColors

        }
    }

    /*interface OnColorPickerClickListener{
        fun onColorPickerClickListener(colorCode: Int)
    }*/

    override fun getItemCount(): Int {
        return colorPickerColors.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.colorPickerView.setBackgroundColor(colorPickerColors.get(position))

        holder.colorPickerView.setOnClickListener {
            onColorPickerClickListener(colorPickerColors[position])
        }
    }

    class ViewHolder : RecyclerView.ViewHolder{
        var colorPickerView : View
        constructor(view: View) : super(view) {
            colorPickerView = view.findViewById(R.id.color_picker_view)

        }
    }
}