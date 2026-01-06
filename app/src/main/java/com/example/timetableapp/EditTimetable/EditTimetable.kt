package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.ScallopedHeader
import com.example.timetableapp.TimetableEntry // Import the shared data class
import com.example.timetableapp.generatetable.*

@Composable
fun EditTimetableScreen(
    entryToEdit: TimetableEntry,
    onUpdateClick: (TimetableEntry) -> Unit,
    onCancelClick: () -> Unit
) {
    val chalkboardGreen = Color(0xFF4B6E63)
    val inputBgColor = Color(0xFFF5E6D3)
    val buttonGold = Color(0xFFB58B43)
    val buttonRed = Color(0xFFFF3B30)


    var editedSubject by remember { mutableStateOf(entryToEdit.subject) }
    var editedLecturer by remember { mutableStateOf(entryToEdit.lecturer) }
    var editedDuration by remember { mutableStateOf(entryToEdit.duration) }
    var editedDayAndTime by remember { mutableStateOf(entryToEdit.dayAndTime) }

    var currentStep by remember { mutableIntStateOf(0) }

    when (currentStep) {
        1 -> SubjectListScreen(onSubjectSelected = { editedSubject = it; currentStep = 0 }, onBack = { currentStep = 0 })
        3 -> DurationSelectionScreen(onDurationSelected = { editedDuration = it; currentStep = 0 }, onBack = { currentStep = 0 })
        4 -> DayTimeSelectionScreen(onSelectionComplete = { editedDayAndTime = it; currentStep = 0 }, onBack = { currentStep = 0 })

        else -> {
            Box(modifier = Modifier.fillMaxSize().background(chalkboardGreen)) {
                ScallopedHeader()
                Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(Modifier.height(50.dp))

                    IconButton(onClick = onCancelClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }

                    Text(
                        text = "Edit Timetable",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 30.dp)
                    )

                    // Form Fields
                    InputField("Subject", editedSubject, true, inputBgColor) { currentStep = 1 }

                    LecturerInputField(
                        label = "Lecturer",
                        value = editedLecturer,
                        onValueChange = { editedLecturer = it },
                        bgColor = inputBgColor
                    )

                    InputField("Duration", editedDuration, true, inputBgColor) { currentStep = 3 }

                    InputField("Days & Time", editedDayAndTime, true, inputBgColor) { currentStep = 4 }

                    Spacer(Modifier.height(60.dp))

                    // Action Buttons (Gold & Red)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        // SAVE BUTTON
                        Button(
                            onClick = {

                                onUpdateClick(entryToEdit.copy(
                                    subject = editedSubject,
                                    lecturer = editedLecturer,
                                    duration = editedDuration,
                                    dayAndTime = editedDayAndTime
                                ))
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SAVE", fontWeight = FontWeight.Bold)
                        }

                        // CANCEL BUTTON
                        Button(
                            onClick = onCancelClick,
                            modifier = Modifier.weight(1f).height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = buttonRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("CANCEL", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}