package com.example.periody.grafik

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
            .height(maxHeight + 32.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        values.forEachIndexed { index, value ->
            val ratio = value.toFloat() / maxValue.toFloat()
            val barHeight = maxHeight * ratio

            Column(
                modifier = Modifier.width(barWidth),
                verticalArrangement = Arrangement.Bottom
            ) {
                // Label angka di atas batang
                Text(
                    text = value.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                )

                Canvas(
                    modifier = Modifier
                        .height(maxHeight)
                        .fillMaxWidth()
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

                // Label kategori di bawah batang
                Text(
                    text = labels[index],
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                )
            }
        }
    }
}
