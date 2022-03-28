package com.mmahmood.remotehotkey

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.custom_tile.view.*

class CustomTileView(context: Context/*, @Nullable attrs: AttributeSet*/) : ConstraintLayout(context/*, attrs*/) {

    private var view: View = LayoutInflater.from(context).inflate(R.layout.custom_tile, this, true)
    private var imageView: ImageView
    private var titleTextView: TextView
    private var title: String?
    private var imageDrawable: Drawable?


    init {
        imageView = view.imageView as ImageView
        titleTextView = view.titleTextView as TextView
        val typedArray = context.theme.obtainStyledAttributes(null, R.styleable.CustomViewTile, 0, 0)

        try {
            // TODO: Replace drawable with bitmap
            imageDrawable = typedArray.getDrawable(R.styleable.CustomViewTile_setImageDrawable)
            title = typedArray.getString(R.styleable.CustomViewTile_setTitle)
            imageView.setImageDrawable(imageDrawable)
            titleTextView.text = title
        }
        finally {
            typedArray.recycle()
        }
    }

    /*fun setImageDrawable(drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }
    fun getImageDrawable() : Drawable? {
        return imageDrawable
    }*/

    fun setTitle(text: String?) {
        titleTextView.text = text
    }
    fun getTitle() : String? {
        return title
    }


}