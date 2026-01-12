package com.example.timetableapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun exportTimetableToPdf(context: Context, schedule: List<TimetableEntry>): File? {
        val pdfDocument = PdfDocument()

        // Landscape A4 size
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // --- PAINTS ---
        val linePaint = Paint().apply { color = Color.BLACK; strokeWidth = 1f; style = Paint.Style.STROKE }
        val rehatBgPaint = Paint().apply { color = Color.rgb(209, 209, 209); style = Paint.Style.FILL }
        val subjectBgPaint = Paint().apply { color = Color.rgb(253, 245, 230); style = Paint.Style.FILL }
        val textPaint = Paint().apply { color = Color.BLACK; textSize = 8f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val labelPaint = Paint().apply { color = Color.BLACK; textSize = 10f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }

        // --- DIMENSIONS ---
        val startX = 40f
        val startY = 60f
        val timeColWidth = 80f
        val dayColWidth = 95f // Fits 7 days perfectly
        val rowHeight = 40f

        val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
        val morningSlots = listOf("7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00")
        val afternoonSlots = listOf("10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00", "1.00-1.30", "1.30-2.00")

        // 1. Draw Day Headers
        days.forEachIndexed { index, day ->
            val x = startX + timeColWidth + (index * dayColWidth) + (dayColWidth / 2)
            canvas.drawText(day, x, startY, labelPaint)
        }

        var currentY = startY + 20f

        // FIXED: Normalization logic to match your UI exactly
        fun normalize(t: String): String {
            return t.uppercase()
                .replace(" AM", "")
                .replace(" PM", "")
                .replace(":", ".")
                .split("-")[0]
                .trim()
        }

        // 2. Row Drawing Logic
        fun drawGridRows(slots: List<String>, yStart: Float): Float {
            var y = yStart
            slots.forEach { slotTime ->
                // Draw Time Label
                canvas.drawText(slotTime, startX + (timeColWidth / 2), y + (rowHeight / 2) + 5f, labelPaint)

                days.forEachIndexed { dIndex, day ->
                    val cellX = startX + timeColWidth + (dIndex * dayColWidth)
                    val rect = RectF(cellX + 2, y + 2, cellX + dayColWidth - 2, y + rowHeight - 2)

                    // Match logic exactly like your TimetableRow screen
                    val entry = schedule.find { item ->
                        val dayMatch = item.dayAndTime.uppercase().contains(day)
                        val savedTime = normalize(item.dayAndTime.split(" ").lastOrNull() ?: "")
                        val slotStart = normalize(slotTime)

                        // Check for both "8.00" and "08.00" formats
                        dayMatch && (savedTime == slotStart || "0$savedTime" == slotStart || savedTime == "0$slotStart")
                    }

                    if (entry != null) {
                        canvas.drawRoundRect(rect, 4f, 4f, subjectBgPaint)
                        canvas.drawRoundRect(rect, 4f, 4f, linePaint)
                        // Use the Alias (BM, MAT, etc) so it fits in the small PDF box
                        val alias = when (entry.subject.trim()) {
                            "Bahasa Melayu" -> "BM"
                            "Bahasa Inggeris" -> "BI"
                            "Matematik" -> "MAT"
                            "Sains" -> "SNS"
                            else -> if (entry.subject.length > 6) entry.subject.take(5) + "." else entry.subject
                        }
                        canvas.drawText(alias, rect.centerX(), rect.centerY() + 4f, textPaint)
                    } else {
                        canvas.drawRoundRect(rect, 4f, 4f, linePaint.apply { strokeWidth = 0.2f; color = Color.LTGRAY })
                        linePaint.apply { strokeWidth = 1f; color = Color.BLACK }
                    }
                }
                y += rowHeight
            }
            return y
        }

        currentY = drawGridRows(morningSlots, currentY)

        // 3. REHAT Row
        val rehatRect = RectF(startX + timeColWidth, currentY + 5, startX + timeColWidth + (7 * dayColWidth), currentY + 25f)
        canvas.drawRoundRect(rehatRect, 4f, 4f, rehatBgPaint)
        canvas.drawRoundRect(rehatRect, 4f, 4f, linePaint)
        canvas.drawText("REHAT", rehatRect.centerX(), rehatRect.centerY() + 5f, labelPaint)

        currentY += 40f
        drawGridRows(afternoonSlots, currentY)

        pdfDocument.finishPage(page)

        // --- SAVE TO DOWNLOADS ---
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, "My_Timetable.pdf")

        return try {
            pdfDocument.writeTo(FileOutputStream(file))
            // This line ensures the file "appears" in the phone's file list immediately
            MediaScannerConnection.scanFile(context, arrayOf(file.absolutePath), arrayOf("application/pdf"), null)
            file
        } catch (e: Exception) {
            null
        } finally {
            pdfDocument.close()
        }
    }
}