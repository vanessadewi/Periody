package com.example.periody.grafik

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BarChart(
    labels: List<String>,
    values: List<Int>,
    barColor: Color = MaterialTheme.colorScheme.primary
) {
    val maxValue = (values.maxOrNull() ?: 1).coerceAtLeast(1)
    val barWidth = 40.dp
    val spacing = 16.dp
    val maxHeight = 140.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxHeight),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        values.forEachIndexed { index, value ->
            val ratio = value.toFloat() / maxValue.toFloat()
            val barHeight = maxHeight * ratio

            Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                Canvas(
                    modifier = Modifier
                        .width(barWidth)
                        .height(maxHeight)
                ) {
                    drawRect(
                        color = barColor,
                        topLeft = Offset(0f, size.height - barHeight.toPx()),
                        size = androidx.compose.ui.geometry.Size(
                            barWidth.toPx(),
                            barHeight.toPx()
                        )
                    )
                }
                Spacer(Modifier.height(4.dp))
                androidx.compose.material3.Text(labels[index])
            }
        }
    }
}
