package com.example.timetableapp.generatetable

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
fun SubjectListScreen(onSubjectSelected: (String) -> Unit, onBack: () -> Unit) {
    // UPDATED: Focused list with REHAT and Perhimpunan, removing Geografi and RBT
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

    // Button and Text Styling
    val itemBgColor = Color(0xFFF5E6D3)
    val itemTextColor = Color(0xFF3E5A51)
    val specialItemColor = Color(0xFFB58B43) // Gold color for REHAT/Perhimpunan

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp)) {
            Spacer(Modifier.height(50.dp))

            // Header Row
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Select Subject",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            // Scrollable List of Subjects
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(subjects) { subject ->
                    // Logic to highlight REHAT or Perhimpunan
                    val isSpecial = subject == "REHAT" || subject == "Perhimpunan"

                    Button(
                        onClick = { onSubjectSelected(subject) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(65.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpecial) specialItemColor else itemBgColor
                        ),
                        shape = RoundedCornerShape(15.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = subject,
                            color = if (isSpecial) Color.White else itemTextColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}