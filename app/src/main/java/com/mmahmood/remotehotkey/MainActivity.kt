package com.mmahmood.remotehotkey

import android.R
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.mmahmood.remotehotkey.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    // TODO [] Make adjustable.
    private var mRows = 6
    private var mCols = 8

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO [2022/03/27]: Create layout programmatically:

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Code adapted from - https://stackoverflow.com/questions/51430129/create-grid-n-%C3%97-n-in-android-constraintlayout-with-variable-number-of-n
        val layout = binding.conlayout

        val color1 = ContextCompat.getColor(this, R.color.holo_blue_bright)
        val color2 = ContextCompat.getColor(this, R.color.holo_blue_light)
        var textView: TextView
        var lp: ConstraintLayout.LayoutParams
        var id: Int
        val idArray = Array(mRows) { IntArray(mCols) }
        val cs = ConstraintSet()

        // TODO: Replace TextView with
        // Add our views to the ConstraintLayout.
        for (iRow in 0 until mRows) {
            for (iCol in 0 until mCols) {
                textView = TextView(this)
                lp = ConstraintLayout.LayoutParams(
                    ConstraintSet.MATCH_CONSTRAINT,
                    ConstraintSet.MATCH_CONSTRAINT
                )
                id = View.generateViewId()
                idArray[iRow][iCol] = id
                textView.id = id
                textView.text = id.toString()
                textView.gravity = Gravity.CENTER
                textView.setBackgroundColor(if ((iRow + iCol) % 2 == 0) color1 else color2)
                layout.addView(textView, lp)
            }
        }

        // Create horizontal chain for each row and set the 1:1 dimensions.
        // but first make sure the layout frame has the right ratio set.

        // Create horizontal chain for each row and set the 1:1 dimensions.
        // but first make sure the layout frame has the right ratio set.
        cs.clone(layout)
        cs.setDimensionRatio(binding.gridFrame.id, "$mCols:$mRows")
        for (iRow in 0 until mRows) {
            for (iCol in 0 until mCols) {
                id = idArray[iRow][iCol]
                cs.setDimensionRatio(id, "1:1")
                if (iRow == 0) {
                    // Connect the top row to the top of the frame.
                    cs.connect(id, ConstraintSet.TOP, binding.gridFrame.id, ConstraintSet.TOP)
                } else {
                    // Connect top to bottom of row above.
                    cs.connect(id, ConstraintSet.TOP, idArray[iRow - 1][0], ConstraintSet.BOTTOM)
                }
            }
            // Create a horiontal chain that will determine the dimensions of our squares.
            // Could also be createHorizontalChainRtl() with START/END.
            cs.createHorizontalChain(
                binding.gridFrame.id, ConstraintSet.LEFT,
                binding.gridFrame.id, ConstraintSet.RIGHT,
                idArray[iRow], null, ConstraintSet.CHAIN_PACKED
            )
        }

        // Make views clickable
        for (iRow in 0 until mRows) {
            for (iCol in 0 until mCols) {
                findViewById<TextView>(idArray[iRow][iCol]).setOnClickListener {
                    sendCommand(idArray[iRow][iCol], iRow, iCol)
                }
            }
        }

        cs.applyTo(layout)

        // Example of a call to a native method
//        binding.sampleText.text = stringFromJNI()
    }

    private fun sendCommand(id: Int, row: Int, col: Int) {
        Log.e(TAG, "Request Sent for [R$row, C$col], id=$id.")
        // TODO use lookup table to send appropriate command.
    }

    /**
     * A native method that is implemented by the 'remotehotkey' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        // Used to load the 'remotehotkey' library on application startup.
        init {
            System.loadLibrary("remotehotkey")
        }
    }
}