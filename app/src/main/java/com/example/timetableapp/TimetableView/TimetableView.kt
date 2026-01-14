package com.example.timetableapp.TimetableView

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.DailyListLayout
import com.example.timetableapp.PdfExporter
import com.example.timetableapp.R
import com.example.timetableapp.TimetableEntry

// Shorthand for the Grid
fun getSubjectAlias(subject: String): String {
    return when (subject.trim().uppercase()) {
        "BAHASA MELAYU" -> "BM"
        "BAHASA INGGERIS" -> "BI"
        "MATEMATIK" -> "MAT"
        "SAINS" -> "SNS"
        "PENDIDIKAN ISLAM" -> "PI"
        "MUZIK" -> "MZ"
        "SEJARAH" -> "SEJ"
        "SENI VISUAL" -> "PSV"
        "PERHIMPUNAN" -> "PER"
        "REHAT" -> "REHAT"
        else -> if (subject.length > 5) subject.take(4) + ".." else subject
    }
}

@Composable
fun TimetableViewScreen(
    scheduleData: List<TimetableEntry>,
    onBackClick: () -> Unit
) {
    var isWeeklyView by remember { mutableStateOf(true) }
    val switchButtonColor = Color(0xFFB58B43)
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = if (isWeeklyView) "Weekly" else "Daily",
                    color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        val file = PdfExporter.exportTimetableToPdf(context, scheduleData)
                        if (file != null) Toast.makeText(context, "PDF Exported", Toast.LENGTH_LONG).show()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier.height(35.dp)
                ) {
                    Text("EXPORT PDF", color = Color.White, fontSize = 10.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            Box(modifier = Modifier.weight(1f)) {
                if (isWeeklyView) WeeklyTableLayout(scheduleData) else DailyListLayout(scheduleData)
            }

            Button(
                onClick = { isWeeklyView = !isWeeklyView },
                modifier = Modifier.padding(vertical = 20.dp).fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = switchButtonColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (isWeeklyView) "SWITCH TO DAILY VIEW" else "SWITCH TO WEEKLY", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun WeeklyTableLayout(scheduleData: List<TimetableEntry>) {
    val days = listOf("TIME", "SUN", "MON", "TUE", "WED", "THU")
    val allSlots = listOf(
        "7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00",
        "10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
            .padding(6.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
            days.forEach { day ->
                Text(day, Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = Color.Black)
            }
        }

        LazyColumn {
            items(allSlots.size) { index ->
                TimetableRow(time = allSlots[index], scheduleData = scheduleData)
            }
        }
    }
}

@Composable
fun TimetableRow(time: String, scheduleData: List<TimetableEntry>) {
    val dayHeaders = listOf("SUN", "MON", "TUE", "WED", "THU")
    val academicColor = Color(0xFFFDF5E6)
    val specialColor = Color(0xFFD1D1D1)

    // Helps match "07.30" with "7.30" and ignores spaces
    fun normalize(t: String): String = t.replace(" ", "").removePrefix("0").trim()

    Row(modifier = Modifier.fillMaxWidth().height(60.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(time, Modifier.weight(1f), fontSize = 8.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, color = Color.Black)

        dayHeaders.forEach { day ->
            // Logic to find entry based on Day and Time normalization
            val entry = scheduleData.find { item ->
                val dayPart = item.dayAndTime.split(" ").firstOrNull()?.uppercase() ?: ""
                val timePart = item.dayAndTime.split(" ").lastOrNull() ?: ""

                val dayMatch = dayPart.startsWith(day)
                // Match based on either the duration field OR the time inside dayAndTime string
                val timeMatch = normalize(item.duration).contains(normalize(time)) ||
                        normalize(timePart).contains(normalize(time)) ||
                        normalize(time).contains(normalize(timePart))

                dayMatch && timeMatch
            }

            val isSpecial = entry?.subject?.uppercase() == "REHAT" || entry?.subject?.uppercase() == "PERHIMPUNAN"

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(1.dp)
                    .background(
                        color = when {
                            entry == null -> Color.Transparent
                            isSpecial -> specialColor
                            else -> academicColor
                        },
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(
                        width = 0.5.dp,
                        color = if (entry != null) Color.Black else Color.LightGray.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (entry != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(2.dp)) {
                        val iconToDraw = if (entry.subject.uppercase() == "PERHIMPUNAN") R.drawable.perhimpunan_icon else entry.iconRes

                        if (iconToDraw != null && !isSpecial) {
                            Image(painter = painterResource(id = iconToDraw!!), null, Modifier.size(16.dp))
                        } else if (entry.subject.uppercase() == "PERHIMPUNAN" && iconToDraw != null) {
                            Image(painter = painterResource(id = iconToDraw!!), null, Modifier.size(14.dp))
                        }

                        Text(
                            text = getSubjectAlias(entry.subject),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}