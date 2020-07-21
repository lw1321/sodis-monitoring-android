package de.sodis.monitoring.todolist

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import de.sodis.monitoring.R

interface TodoDeleteButtonListener {
    fun onClick(pos: Int)
}

class TodoDeleteButton(val context: Context, val listener:TodoDeleteButtonListener) {
    var pos: Int = 0
    var clickRegion: RectF?=null
    val resources: Resources
    init {
        resources = context.resources
    }

    fun onClick(x: Float, y: Float):Boolean{
        if(clickRegion!=null && clickRegion!!.contains(x, y)) {
            listener.onClick(pos)
            return true
        }
        return false
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
        val p = Paint()
        p.color = Color.RED
        c.drawRect(rectF, p)

        p.color = Color.WHITE

        val d = ContextCompat.getDrawable(context, R.drawable.ic_delete)
        val bitmap = drawableToBitmap(d)
        c.drawBitmap(bitmap, (rectF.left+rectF.right)/2-bitmap.width/2, (rectF.top + rectF.bottom)/2-bitmap.height/2, p)
        clickRegion = rectF
        this.pos = pos
    }

    private fun drawableToBitmap(d: Drawable?): Bitmap {
        if(d is BitmapDrawable) return d.bitmap
        val bitmap = Bitmap.createBitmap(d!!.intrinsicWidth, d. intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        d.setBounds(0, 0, canvas.width, canvas.height)
        d.draw(canvas)
        return bitmap
    }


}