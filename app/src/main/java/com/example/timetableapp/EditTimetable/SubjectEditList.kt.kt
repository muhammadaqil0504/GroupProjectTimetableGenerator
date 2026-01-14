package com.example.timetableapp.EditTimetable

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

@Composable
fun SubjectEditScreen(
    onSubjectSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    // UPDATED: Standardized list to match Generate screen
    val subjects = listOf(
        "REHAT",
        "Perhimpunan",
        "Bahasa Melayu",
        "Bahasa Inggeris",
        "Matematik",
        "Sains",
        "Pendidikan Islam",
        "Sejarah",
        "PJPK",
        "Muzik",
        "Seni Visual"
    )

    val itemBgColor = Color(0xFFF5E6D3)
    val itemTextColor = Color(0xFF3E5A51)
    val specialItemColor = Color(0xFFB58B43) // Gold color for REHAT/Perhimpunan

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            // Navigation and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
            }

            Text(
                text = "Edit Subject",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Scrollable List of Subjects
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(subjects) { subject ->
                    val isSpecial = subject == "REHAT" || subject == "Perhimpunan"

                    Button(
                        onClick = { onSubjectSelected(subject) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpecial) specialItemColor else itemBgColor
                        ),
                        shape = RoundedCornerShape(12.dp), // Slightly more rounded for modern look
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = subject,
                            color = if (isSpecial) Color.White else itemTextColor,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}