package com.example.timetableapp

import android.content.Context
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun exportTimetableToPdf(context: Context, schedule: List<TimetableEntry>): File? {
        val pdfDocument = PdfDocument()

        // Landscape A4 size (842 x 595 points)
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // --- PAINTS ---
        val linePaint = Paint().apply { color = Color.BLACK; strokeWidth = 1f; style = Paint.Style.STROKE }
        val specialBgPaint = Paint().apply { color = Color.rgb(209, 209, 209); style = Paint.Style.FILL }
        val academicBgPaint = Paint().apply { color = Color.rgb(253, 245, 230); style = Paint.Style.FILL }

        val titlePaint = Paint().apply { color = Color.BLACK; textSize = 20f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val headerPaint = Paint().apply { color = Color.BLACK; textSize = 10f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val subjectPaint = Paint().apply { color = Color.BLACK; textSize = 8f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val teacherPaint = Paint().apply { color = Color.DKGRAY; textSize = 6f; isFakeBoldText = false; textAlign = Paint.Align.CENTER }
        val dayLabelPaint = Paint().apply { color = Color.BLACK; textSize = 11f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }

        // --- DIMENSIONS ---
        val startX = 40f
        val startY = 100f
        val dayColWidth = 60f   // Width of the "SUN, MON..." label column
        val timeColWidth = 72f  // Width of each time slot column (fits 10 slots + labels)
        val rowHeight = 75f     // Taller rows to accommodate icon, subject, and teacher

        val days = listOf("SUN", "MON", "TUE", "WED", "THU")
        val timeSlots = listOf(
            "7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00",
            "10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00"
        )

        // Draw Title
        canvas.drawText("SCHOOL WEEKLY TIMETABLE", 421f, 50f, titlePaint)

        // 1. Draw Time Column Headers (X-Axis)
        timeSlots.forEachIndexed { index, time ->
            val x = startX + dayColWidth + (index * timeColWidth) + (timeColWidth / 2)
            canvas.drawText(time, x, startY - 10f, headerPaint)
        }

        var currentY = startY

        // 2. Draw Rows (Days)
        days.forEach { dayLabel ->
            // Draw Day Label (Y-Axis)
            canvas.drawText(dayLabel, startX + (dayColWidth / 2), currentY + (rowHeight / 2) + 5f, dayLabelPaint)

            timeSlots.forEachIndexed { tIndex, slotTime ->
                val cellX = startX + dayColWidth + (tIndex * timeColWidth)
                val rect = RectF(cellX + 2, currentY + 2, cellX + timeColWidth - 2, currentY + rowHeight - 2)

                // Match logic: Find entry for this specific Day and Time
                val entry = schedule.find { item ->
                    val dayPart = item.dayAndTime.split(" ").firstOrNull()?.uppercase() ?: ""
                    val savedTime = item.dayAndTime.split(" ").lastOrNull()?.trim() ?: ""
                    dayPart.startsWith(dayLabel) && savedTime == slotTime
                }

                if (entry != null) {
                    val isSpecial = entry.subject.uppercase() == "REHAT" || entry.subject.uppercase() == "PERHIMPUNAN"

                    // Background & Border
                    canvas.drawRoundRect(rect, 4f, 4f, if (isSpecial) specialBgPaint else academicBgPaint)
                    canvas.drawRoundRect(rect, 4f, 4f, linePaint)

                    // Draw Icon
                    val iconRes = if (entry.subject.uppercase() == "PERHIMPUNAN") R.drawable.perhimpunan_icon else entry.iconRes
                    if (iconRes != null) {
                        val bitmap = drawableToBitmap(context, iconRes)
                        if (bitmap != null) {
                            val iconSize = 22f
                            val destRect = RectF(
                                rect.centerX() - (iconSize / 2),
                                rect.top + 8f,
                                rect.centerX() + (iconSize / 2),
                                rect.top + 8f + iconSize
                            )
                            canvas.drawBitmap(bitmap, null, destRect, null)
                        }
                    }

                    // Draw Full Subject Name
                    canvas.drawText(entry.subject, rect.centerX(), rect.centerY() + 8f, subjectPaint)

                    // Draw Teacher Name (if not REHAT)
                    if (entry.lecturer != "-" && entry.lecturer.isNotBlank()) {
                        canvas.drawText(entry.lecturer, rect.centerX(), rect.bottom - 10f, teacherPaint)
                    }
                } else {
                    // Empty Cell Placeholder
                    canvas.drawRoundRect(rect, 4f, 4f, linePaint.apply { strokeWidth = 0.2f; color = Color.LTGRAY })
                    linePaint.apply { strokeWidth = 1f; color = Color.BLACK }
                }
            }
            currentY += rowHeight
        }

        pdfDocument.finishPage(page)

        // --- SAVE TO DOWNLOADS ---
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "My_Timetable.pdf")

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("application/pdf"), null)
            file
        } catch (e: Exception) {
            null
        } finally {
            pdfDocument.close()
        }
    }

    private fun drawableToBitmap(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth.coerceAtLeast(1),
            drawable.intrinsicHeight.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}