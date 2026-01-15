package com.example.timetableapp

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object PdfExporter {

    fun exportTimetableToPdf(context: Context, schedule: List<TimetableEntry>): Boolean {
        val pdfDocument = PdfDocument()

        // Landscape A4 size (842 x 595 points)
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // --- PAINTS ---
        val linePaint = Paint().apply { color = Color.BLACK; strokeWidth = 1f; style = Paint.Style.STROKE }
        val specialBgPaint = Paint().apply { color = Color.rgb(220, 220, 220); style = Paint.Style.FILL }
        val academicBgPaint = Paint().apply { color = Color.rgb(253, 245, 230); style = Paint.Style.FILL }

        val titlePaint = Paint().apply { color = Color.BLACK; textSize = 22f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val headerPaint = Paint().apply { color = Color.BLACK; textSize = 9f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val subjectPaint = Paint().apply { color = Color.BLACK; textSize = 8f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val teacherPaint = Paint().apply { color = Color.DKGRAY; textSize = 7f; isFakeBoldText = false; textAlign = Paint.Align.CENTER }
        val dayLabelPaint = Paint().apply { color = Color.BLACK; textSize = 11f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }

        // --- DIMENSIONS ---
        val startX = 30f
        val startY = 100f
        val dayColWidth = 50f
        val timeColWidth = 75f
        val rowHeight = 85f

        val days = listOf("SUN", "MON", "TUE", "WED", "THU")
        val timeSlots = listOf(
            "7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00",
            "10.00-10.30", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00"
        )

        // Draw Title
        canvas.drawText("SCHOOL WEEKLY TIMETABLE", 421f, 50f, titlePaint)

        // 1. Draw Time Column Headers
        timeSlots.forEachIndexed { index, time ->
            val x = startX + dayColWidth + (index * timeColWidth) + (timeColWidth / 2)
            canvas.drawText(time, x, startY - 15f, headerPaint)
        }

        var currentY = startY

        // 2. Draw Rows (Days)
        days.forEach { dayLabel ->
            canvas.drawText(dayLabel, startX + (dayColWidth / 2), currentY + (rowHeight / 2) + 5f, dayLabelPaint)

            timeSlots.forEachIndexed { tIndex, slotTime ->
                val cellX = startX + dayColWidth + (tIndex * timeColWidth)
                val rect = RectF(cellX + 1, currentY + 1, cellX + timeColWidth - 1, currentY + rowHeight - 1)

                val entry = schedule.find { item ->
                    val dayPart = item.dayAndTime.split(" ").firstOrNull()?.uppercase() ?: ""
                    val savedTime = item.dayAndTime.split(" ").lastOrNull()?.trim() ?: ""
                    dayPart.startsWith(dayLabel) && savedTime == slotTime
                }

                if (entry != null) {
                    val isSpecial = entry.subject.uppercase() == "REHAT" || entry.subject.uppercase() == "PERHIMPUNAN"
                    canvas.drawRoundRect(rect, 2f, 2f, if (isSpecial) specialBgPaint else academicBgPaint)
                    canvas.drawRoundRect(rect, 2f, 2f, linePaint)

                    val iconRes = if (entry.subject.uppercase() == "PERHIMPUNAN") R.drawable.perhimpunan_icon else entry.iconRes
                    if (iconRes != null && iconRes != 0) {
                        drawableToBitmap(context, iconRes)?.let { bitmap ->
                            val iconSize = 20f
                            val destRect = RectF(rect.centerX() - (iconSize / 2), rect.top + 10f, rect.centerX() + (iconSize / 2), rect.top + 10f + iconSize)
                            canvas.drawBitmap(bitmap, null, destRect, null)
                        }
                    }
                    canvas.drawText(entry.subject, rect.centerX(), rect.centerY() + 10f, subjectPaint)
                    if (entry.lecturer != "-" && entry.lecturer.isNotBlank()) {
                        canvas.drawText(entry.lecturer, rect.centerX(), rect.bottom - 12f, teacherPaint)
                    }
                } else {
                    canvas.drawRoundRect(rect, 2f, 2f, Paint().apply { color = Color.LTGRAY; style = Paint.Style.STROKE; strokeWidth = 0.5f })
                }
            }
            currentY += rowHeight
        }

        pdfDocument.finishPage(page)

        // --- PUBLIC MEDIASTORE SAVE LOGIC ---
        val fileName = "Timetable_${System.currentTimeMillis()}.pdf"
        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        return try {
            if (uri != null) {
                val outputStream: OutputStream? = resolver.openOutputStream(uri)
                outputStream?.use {
                    pdfDocument.writeTo(it)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawableToBitmap(context: Context, drawableId: Int): Bitmap? {
        return try {
            val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            null
        }
    }
}