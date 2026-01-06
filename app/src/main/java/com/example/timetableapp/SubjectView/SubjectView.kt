package com.example.timetableapp.SubjectView

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.ScallopedHeader

@Composable
fun SubjectInterface(onBackClick: () -> Unit) {
    val chalkboardGreen = Color(0xFF4B6E63)
    val cardColor = Color(0xFFF5E6D3) // Cream color from your screenshots

    val subjectList = listOf(
        "Bahasa Melayu", "Bahasa Inggeris", "Matematik",
        "Sains", "Pendidikan Islam / Moral", "Sejarah",
        "PJPK", "Muzik", "Seni Visual"
    )

    Box(modifier = Modifier.fillMaxSize().background(chalkboardGreen)) {
        ScallopedHeader()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp)
        ) {
            Spacer(Modifier.height(50.dp))

            // Back Button and Title
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Subjects",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            // Scrollable List of Subjects
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Makes the list scrollable
                    .padding(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                subjectList.forEach { subject ->
                    SubjectItem(subject, cardColor)
                }
            }
        }
    }
}

@Composable
fun SubjectItem(name: String, bgColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}