package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.TimetableEntry

@Composable
fun EditFormContent(
    entry: TimetableEntry,
    onSubjectFieldClick: () -> Unit,
    onIconFieldClick: () -> Unit,
    onDurationFieldClick: () -> Unit,
    onDayTimeFieldClick: () -> Unit,
    onLecturerChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    // Removed chalkboardGreen to let the background image show through
    val inputBgColor = Color(0xFFF5E6D3)
    val generatedLabelColor = Color.White.copy(alpha = 0.7f)

    // CHANGED: Removed .background(chalkboardGreen)
    Box(modifier = Modifier.fillMaxSize()) {


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(Modifier.height(50.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Text(
                        text = "Edit Timetable",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(20.dp))
            }

            // Icon and Subject Section
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("Icon", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Surface(
                            onClick = onIconFieldClick,
                            modifier = Modifier.size(60.dp).padding(top = 4.dp),
                            color = inputBgColor,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                entry.iconRes?.let {
                                    Image(painter = painterResource(id = it), contentDescription = null, modifier = Modifier.size(35.dp))
                                }
                            }
                        }
                    }
                    Spacer(Modifier.width(15.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        ReadOnlyField("Subject Edit", entry.subject, inputBgColor, onSubjectFieldClick)
                    }
                }
                Spacer(Modifier.height(15.dp))
            }

            // Lecturer Section
            item {
                EditLecturerInputField(
                    label = "Teacher Edit",
                    value = entry.lecturer,
                    onValueChange = onLecturerChange,
                    bgColor = inputBgColor
                )
                Spacer(Modifier.height(15.dp))
            }

            // Day Section
            item {
                val dayOnly = entry.dayAndTime.split(" ").firstOrNull() ?: ""
                Text("Day Generated: $dayOnly", color = generatedLabelColor, fontSize = 13.sp)

                ReadOnlyField("Day Edit", dayOnly, inputBgColor, onDayTimeFieldClick)
                Spacer(Modifier.height(15.dp))
            }

            // Duration Section
            item {
                Text("Duration Generated: ${entry.duration}", color = generatedLabelColor, fontSize = 13.sp)

                ReadOnlyField("Duration Edit", entry.duration, inputBgColor, onDurationFieldClick)
                Spacer(Modifier.height(30.dp))
            }

            // Buttons
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f).height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB58B43)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("SAVE", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Button(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f).height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("CANCEL", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ReadOnlyField(label: String, value: String, bgColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = onClick,
            color = bgColor,
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = value, color = Color.Black, fontSize = 16.sp)
            }
        }
    }
}