package campa.aracelyl.myfeelings

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

import android.graphics.*


class CustomCircleDrawable(context: Context, emociones: ArrayList<Emociones>) : Drawable() {

    private var coordenadas: RectF? = null
    private var anguloBarrido: Float = 0.0F
    private var anguloInicio: Float = 0.0F
    private var grosorMetrica: Int = 0
    private var grosorFondo: Int = 0
    private var context: Context? = null
    private var emociones: ArrayList<Emociones> = ArrayList()

    init {
        grosorMetrica = context.resources.getDimensionPixelSize(R.dimen.graphWith)
        grosorFondo = context.resources.getDimensionPixelSize(R.dimen.graphWith)
        this.context = context
        this.grosorMetrica = 10
        this.grosorFondo = 15
        this.emociones = emociones
    }

    override fun draw(canvas: Canvas) {
        val fondo = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = grosorFondo.toFloat()
            isAntiAlias = true
            strokeCap = Paint.Cap.ROUND
            color = ContextCompat.getColor(context!!, R.color.gray)
        }

        val ancho: Float = (canvas.width - 25).toFloat()
        val alto: Float = (canvas.height - 25).toFloat()
        coordenadas = RectF(25.0F, 25.0F, ancho, alto)
        canvas.drawArc(coordenadas!!, 0.0F, 360.0F, false, fondo)

        if (emociones.isNotEmpty()) {
            for (e in emociones) {
                val degree: Float = (e.porcentaje * 360) / 100
                anguloBarrido = degree

                val seccion = Paint().apply {
                    style = Paint.Style.STROKE
                    isAntiAlias = true
                    strokeWidth = grosorMetrica.toFloat()
                    strokeCap = Paint.Cap.SQUARE
                    color = ContextCompat.getColor(context!!, e.color)
                }

                canvas.drawArc(coordenadas!!, anguloInicio, anguloBarrido, false, seccion)
                anguloInicio += anguloBarrido
            }
        }
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(colorFilter: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }
}
