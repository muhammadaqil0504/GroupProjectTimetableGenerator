package com.example.timetableapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

object PdfExporter {

    fun exportTimetableToPdf(context: Context, schedule: List<TimetableEntry>) {
        val pdfDocument = PdfDocument()

        // Landscape orientation (842 x 595) matches your weekly grid layout
        val pageInfo = PdfDocument.PageInfo.Builder(842, 595, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        // Define Paints for matching your UI colors
        val linePaint = Paint().apply { color = Color.BLACK; strokeWidth = 1f; style = Paint.Style.STROKE }
        val rehatBgPaint = Paint().apply { color = Color.rgb(209, 209, 209); style = Paint.Style.FILL }
        val subjectBgPaint = Paint().apply { color = Color.rgb(253, 245, 230); style = Paint.Style.FILL }
        val textPaint = Paint().apply { color = Color.BLACK; textSize = 9f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }
        val timeLabelPaint = Paint().apply { color = Color.BLACK; textSize = 10f; isFakeBoldText = true; textAlign = Paint.Align.CENTER }

        val startX = 40f
        val startY = 60f
        val timeColWidth = 80f
        val dayColWidth = 140f
        val rowHeight = 45f
        val headerHeight = 35f

        val days = listOf("SUN", "MON", "TUE", "WED", "THU")
        val morningSlots = listOf("7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00")
        val afternoonSlots = listOf("10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30")

        // 1. Draw Table Header (Day Names)
        days.forEachIndexed { index, day ->
            val x = startX + timeColWidth + (index * dayColWidth) + (dayColWidth / 2)
            canvas.drawText(day, x, startY + 20f, timeLabelPaint)
        }

        var currentY = startY + headerHeight

        // Normalization Helper to match App's logic
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
                canvas.drawText(slotTime, startX + (timeColWidth / 2), y + (rowHeight / 2) + 5f, timeLabelPaint)

                days.forEachIndexed { dIndex, day ->
                    val cellX = startX + timeColWidth + (dIndex * dayColWidth)
                    val rect = RectF(cellX + 2, y + 2, cellX + dayColWidth - 2, y + rowHeight - 2)

                    // Match logic exactly like your TimetableRow
                    val entry = if (day == "MON" && slotTime == "7.30-8.00") {
                        TimetableEntry("Perhimpunan", "Tapak", slotTime, "MON $slotTime")
                    } else {
                        schedule.find { item ->
                            val dayMatch = item.dayAndTime.uppercase().contains(day)
                            val savedTime = normalize(item.dayAndTime.split(" ").lastOrNull() ?: "")
                            val slotStart = normalize(slotTime)
                            dayMatch && (savedTime == slotStart || "0$savedTime" == slotStart || savedTime == "0$slotStart")
                        }
                    }

                    if (entry != null) {
                        canvas.drawRoundRect(rect, 6f, 6f, subjectBgPaint)
                        canvas.drawRoundRect(rect, 6f, 6f, linePaint)
                        canvas.drawText(entry.subject, rect.centerX(), rect.centerY() + 4f, textPaint)
                    } else {
                        // Draw empty cell border
                        canvas.drawRoundRect(rect, 6f, 6f, linePaint.apply { strokeWidth = 0.2f; color = Color.GRAY })
                        linePaint.apply { strokeWidth = 1f; color = Color.BLACK } // reset paint
                    }
                }
                y += rowHeight
            }
            return y
        }

        // Morning
        currentY = drawGridRows(morningSlots, currentY)

        // 3. REHAT Row
        val rehatRect = RectF(startX + 2, currentY + 4, startX + timeColWidth + (5 * dayColWidth) - 2, currentY + 30f)
        canvas.drawRoundRect(rehatRect, 4f, 4f, rehatBgPaint)
        canvas.drawRoundRect(rehatRect, 4f, 4f, linePaint)
        canvas.drawText("REHAT", rehatRect.centerX(), rehatRect.centerY() + 5f, timeLabelPaint)
        currentY += 40f

        // Afternoon
        drawGridRows(afternoonSlots, currentY)

        // Save and Close
        pdfDocument.finishPage(page)
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Weekly_Timetable.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF Exported Successfully!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}