package com.example.exchangerates.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/*
    Часть 5.1 – Добавить простые графики исторических курсов для выбранной валюты.
    Данный компонент рисует линейный график (LineChart) на основе списка значений курсов.
    Используется на экране истории (HistoryScreen) и на экране сравнения (ComparisonScreen).

    Часть 5.2  – на экране сравнения этот же график отображается для нескольких валют,
    что позволяет сравнивать динамику курсов визуально.

    Часть 5.4 – Базовые анимации могут быть добавлены, но в данном компоненте анимации нет —
    это статический Canvas (однако при переключении валют Compose перерисовывает график,
    что можно считать базовой анимацией смены данных).
*/

@Composable
fun LineChart( //5.1 рисуется линейный график на канвас
    data: List<Double>,
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF4CAF50),
    strokeWidth: Float = 4f
) {
    if (data.isEmpty()) return

    Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
        val width = size.width
        val height = size.height
        if (data.size == 1) {
            // Если только одна точка – рисуем одну точку (для единственного исторического значения)
            drawCircle(
                color = lineColor,
                radius = 8f,
                center = Offset(width / 2, height / 2)
            )
            return@Canvas
        }

        val stepX = width / (data.size - 1)
        val maxY = data.maxOrNull() ?: 1.0
        val minY = data.minOrNull() ?: 0.0
        val range = if (maxY == minY) 1.0 else maxY - minY

        val points = data.mapIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - minY) / range) * height
            Offset(x, y.toFloat())
        }

        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        drawPath(path, color = lineColor, style = Stroke(width = strokeWidth))

        points.forEach { point ->
            drawCircle(
                color = lineColor,
                radius = 6f,
                center = point
            )
        }
    }
}