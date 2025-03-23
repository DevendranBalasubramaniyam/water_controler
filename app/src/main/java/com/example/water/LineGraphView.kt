package com.example.water

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineGraphView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paintLine = Paint()
    private val paintAxis = Paint()
    private val paintText = Paint()
    private var dataPoints: List<Pair<String, Float>> = listOf()

    init {
        // Paint for the line graph
        paintLine.color = Color.BLUE
        paintLine.strokeWidth = 8f
        paintLine.isAntiAlias = true

        // Paint for axes
        paintAxis.color = Color.BLACK
        paintAxis.strokeWidth = 4f

        // Paint for text labels
        paintText.color = Color.BLACK
        paintText.textSize = 36f // Larger text size
        paintText.isAntiAlias = true
    }

    // Set data with labels and values
    fun setDataPoints(data: List<Pair<String, Float>>) {
        dataPoints = data
        invalidate() // Redraw the view
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw axes
        drawAxes(canvas)

        if (dataPoints.isEmpty()) return // If no data, stop here

        val width = width.toFloat()
        val height = height.toFloat()
        val paddingBottom = 100f
        val paddingLeft = 100f

        val graphWidth = width - paddingLeft * 2
        val graphHeight = height - paddingBottom * 2

        val spacingX = graphWidth / (dataPoints.size - 1).coerceAtLeast(1)
        val maxY = dataPoints.maxOfOrNull { it.second } ?: 1f

        var previousX = paddingLeft
        var previousY = height - paddingBottom - (dataPoints[0].second / maxY * graphHeight)

        for ((index, point) in dataPoints.withIndex()) {
            val currentX = paddingLeft + index * spacingX
            val currentY = height - paddingBottom - (point.second / maxY * graphHeight)

            // Draw data point lines
            if (index > 0) {
                canvas.drawLine(previousX, previousY, currentX, currentY, paintLine)
            }

            // Draw X-axis labels
            canvas.drawText(point.first, currentX - 20, height - paddingBottom + 40, paintText)

            // Update previous points
            previousX = currentX
            previousY = currentY
        }
    }

    private fun drawAxes(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val paddingBottom = 100f
        val paddingLeft = 100f

        // Draw Y-axis
        canvas.drawLine(paddingLeft, 0f, paddingLeft, height - paddingBottom, paintAxis)

        // Draw X-axis
        canvas.drawLine(paddingLeft, height - paddingBottom, width, height - paddingBottom, paintAxis)
    }
}
