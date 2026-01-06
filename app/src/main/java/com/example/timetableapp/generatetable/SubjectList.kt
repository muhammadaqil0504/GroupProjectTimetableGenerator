package com.example.timetableapp.generatetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun SubjectListScreen(onSubjectSelected: (String) -> Unit, onBack: () -> Unit) {
    // List matching the Malaysian primary/secondary school curriculum
    val subjects = listOf(
        "Bahasa Melayu", "Bahasa Inggeris", "Matematik", "Sains",
        "Pendidikan Islam", "Sejarah", "PJPK", "Muzik", "Seni Visual"
    )

    val chalkboardGreen = Color(0xFF4B6E63)
    val itemBgColor = Color(0xFFF5E6D3)

    Box(modifier = Modifier.fillMaxSize().background(chalkboardGreen)) {
        ScallopedHeader()

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp)) {
            Spacer(Modifier.height(50.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Select Subject",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            // Scrollable List of Subjects
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp) // Adds space at the bottom of the list
            ) {
                items(subjects) { subject ->
                    Button(
                        onClick = { onSubjectSelected(subject) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = itemBgColor),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = subject,
                            color = Color(0xFF4B6E63), // Text matches chalkboard color for better design
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}