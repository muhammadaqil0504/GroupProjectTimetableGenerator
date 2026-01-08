package com.example.timetableapp.generatetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
fun DurationSelectionScreen(onDurationSelected: (String) -> Unit, onBack: () -> Unit) {
    val itemBgColor = Color(0xFFF5E6D3)

    val timeSlots = listOf(
        "7.30-8.00", "8.00-8.30",
        "8.30-9.00", "9.00-9.30",
        "9.30-10.00", "10.30-11.00",
        "11.00-11.30", "11.30-12.00",
        "12.00-12.30", "12.30-1.00",
        "1.00-1.30", "1.30-2.00"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "Select Time Slot",
                    color = Color.White,
                    fontSize = 22.sp, // Slightly smaller to save space
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(20.dp))

            // CHANGED: Use LazyVerticalGrid to make the form more compact
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 items per row
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(timeSlots) { time ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp) // Reduced height from 55dp to 50dp
                            .clickable { onDurationSelected(time) },
                        shape = RoundedCornerShape(10.dp),
                        color = itemBgColor
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = time,
                                fontSize = 15.sp, // Slightly smaller text for grid layout
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}