package com.playground.lamme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassFeature(
    modifier: Modifier = Modifier,
    compassColor: Color = Color.White,
    accentColor: Color = Color.Red
) {
    val context = LocalContext.current
    var azimuth by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        val rotationMatrix = FloatArray(9)
        val orientation = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, gravity, 0, 3)
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                    }
                }

                if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(200.dp)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 20f

            rotate(-azimuth, center) {
                // Outer circle
                drawCircle(
                    color = compassColor,
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )

                // Inner circle
                drawCircle(
                    color = compassColor.copy(alpha = 0.3f),
                    radius = radius * 0.85f,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
                )

                // Cardinal directions
                val directions = listOf("N", "E", "S", "W")
                val angles = listOf(0f, 90f, 180f, 270f)

                angles.forEachIndexed { index, angle ->
                    val rad = Math.toRadians(angle.toDouble())
                    val startX = center.x + (radius - 15) * sin(rad).toFloat()
                    val startY = center.y - (radius - 15) * cos(rad).toFloat()
                    val endX = center.x + radius * sin(rad).toFloat()
                    val endY = center.y - radius * cos(rad).toFloat()

                    val lineColor = if (index == 0) accentColor else compassColor

                    drawLine(
                        color = lineColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = if (index == 0) 4f else 2f
                    )
                }

                // Tick marks
                for (i in 0 until 36) {
                    val angle = i * 10f
                    val rad = Math.toRadians(angle.toDouble())
                    val tickLength = if (i % 9 == 0) 15f else if (i % 3 == 0) 10f else 5f
                    
                    val startX = center.x + (radius - tickLength) * sin(rad).toFloat()
                    val startY = center.y - (radius - tickLength) * cos(rad).toFloat()
                    val endX = center.x + radius * sin(rad).toFloat()
                    val endY = center.y - radius * cos(rad).toFloat()

                    drawLine(
                        color = compassColor.copy(alpha = 0.5f),
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 1f
                    )
                }

                // Center dot
                drawCircle(
                    color = accentColor,
                    radius = 8f,
                    center = center
                )
            }
        }
    }
}