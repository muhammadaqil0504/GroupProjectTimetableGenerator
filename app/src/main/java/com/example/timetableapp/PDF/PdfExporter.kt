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
        val specialBgPaint = Paint().apply { color = Color.rgb(209, 209, 209); style = Paint.Style.FILL } // Grey for REHAT
        val academicBgPaint = Paint().apply { color = Color.rgb(253, 245, 230); style = Paint.Style.FILL } // Beige

        val titlePaint = Paint().apply { color = Color.BLACK; textSize = 18f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val headerPaint = Paint().apply { color = Color.BLACK; textSize = 12f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val subjectPaint = Paint().apply { color = Color.BLACK; textSize = 9f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val teacherPaint = Paint().apply { color = Color.DKGRAY; textSize = 7f; isFakeBoldText = false; textAlign = Paint.Align.CENTER }
        val timeLabelPaint = Paint().apply { color = Color.BLACK; textSize = 8f; textAlign = Paint.Align.CENTER }

        // --- DIMENSIONS ---
        val startX = 50f
        val startY = 80f
        val timeColWidth = 70f
        val dayColWidth = 135f // Wider columns because we only have 5 days now
        val rowHeight = 45f

        val days = listOf("SUN", "MON", "TUE", "WED", "THU")
        val allSlots = listOf(
            "7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00",
            "10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00"
        )

        // Draw Title
        canvas.drawText("SCHOOL TIMETABLE", 421f, 40f, titlePaint)

        // 1. Draw Day Headers
        days.forEachIndexed { index, day ->
            val x = startX + timeColWidth + (index * dayColWidth) + (dayColWidth / 2)
            canvas.drawText(day, x, startY, headerPaint)
        }

        var currentY = startY + 15f

        // 2. Row Drawing Logic
        allSlots.forEach { slotTime ->
            // Draw Row Background and Time
            canvas.drawText(slotTime, startX + (timeColWidth / 2), currentY + (rowHeight / 2) + 4f, timeLabelPaint)

            days.forEachIndexed { dIndex, day ->
                val cellX = startX + timeColWidth + (dIndex * dayColWidth)
                val rect = RectF(cellX + 2, currentY + 2, cellX + dayColWidth - 2, currentY + rowHeight - 2)

                // Matching logic
                val entry = schedule.find { item ->
                    val dayPart = item.dayAndTime.split(" ").firstOrNull()?.uppercase() ?: ""
                    val savedTime = item.dayAndTime.split(" ").lastOrNull()?.trim() ?: ""
                    dayPart.startsWith(day) && savedTime == slotTime
                }

                if (entry != null) {
                    val isSpecial = entry.subject.uppercase() == "REHAT" || entry.subject.uppercase() == "PERHIMPUNAN"

                    // Draw Background
                    canvas.drawRoundRect(rect, 6f, 6f, if (isSpecial) specialBgPaint else academicBgPaint)
                    canvas.drawRoundRect(rect, 6f, 6f, linePaint)

                    // Draw Icon (if exists)
                    val iconRes = if (entry.subject.uppercase() == "PERHIMPUNAN") R.drawable.perhimpunan_icon else entry.iconRes
                    if (iconRes != null) {
                        val bitmap = drawableToBitmap(context, iconRes)
                        if (bitmap != null) {
                            val iconSize = 18f
                            val destRect = RectF(
                                rect.centerX() - (iconSize / 2),
                                rect.top + 5f,
                                rect.centerX() + (iconSize / 2),
                                rect.top + 5f + iconSize
                            )
                            canvas.drawBitmap(bitmap, null, destRect, null)
                        }
                    }

                    // Draw Subject Name (Full)
                    canvas.drawText(entry.subject, rect.centerX(), rect.centerY() + 6f, subjectPaint)

                    // Draw Teacher Name (if not REHAT)
                    if (entry.lecturer != "-" && entry.lecturer.isNotBlank()) {
                        canvas.drawText(entry.lecturer, rect.centerX(), rect.bottom - 6f, teacherPaint)
                    }
                } else {
                    // Draw Empty Cell
                    canvas.drawRoundRect(rect, 6f, 6f, linePaint.apply { strokeWidth = 0.2f; color = Color.LTGRAY })
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

    // Helper to convert Vector Drawables (icons) to Bitmaps for PDF drawing
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