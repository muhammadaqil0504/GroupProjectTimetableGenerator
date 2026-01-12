package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.TimetableEntry

@Composable
fun EditSelectionScreen(
    userSchedule: List<TimetableEntry>,
    onEntrySelected: (Int, TimetableEntry) -> Unit,
    onDeleteEntry: (Int) -> Unit, // New parameter
    onBack: () -> Unit
) {
    val itemBgColor = Color(0xFFF5E6D3)
    val itemTextColor = Color(0xFF3E5A51)

    // State to handle the delete confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }
    var indexToDelete by remember { mutableIntStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp)) {
            Spacer(Modifier.height(50.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Spacer(Modifier.width(8.dp))
                Text("Manage Timetable", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(20.dp))

            if (userSchedule.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No subjects saved.", color = Color.White.copy(alpha = 0.6f), fontSize = 18.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 30.dp)
                ) {
                    itemsIndexed(userSchedule) { index, entry ->
                        Button(
                            onClick = { onEntrySelected(index, entry) },
                            modifier = Modifier.fillMaxWidth().height(95.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = itemBgColor),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            contentPadding = PaddingValues(start = 20.dp, end = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(entry.subject, color = itemTextColor, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                    Text("${entry.dayAndTime.split(" ")[0]} | ${entry.duration}", color = Color.DarkGray, fontSize = 13.sp)
                                    Text(entry.lecturer, color = Color.Gray, fontSize = 12.sp)
                                }

                                // Trash button inside the card
                                IconButton(onClick = {
                                    indexToDelete = index
                                    showDeleteDialog = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFB22222))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Entry?") },
            text = { Text("Are you sure you want to remove this subject from your timetable?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteEntry(indexToDelete)
                    showDeleteDialog = false
                }) {
                    Text("DELETE", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }
}