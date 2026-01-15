package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.TimetableEntry

@Composable
fun EditTimetableScreen(
    scheduleData: List<TimetableEntry>,
    onUpdateClick: (TimetableEntry) -> Unit,
    onDeleteClick: (TimetableEntry) -> Unit,
    onCancelClick: () -> Unit
) {
    // 0: Subject, 1: Slot, 2: Form, 3: Day, 4: Time, 5: List, 6: Icon
    var currentStep by remember { mutableIntStateOf(0) }
    var selectedSubjectFilter by remember { mutableStateOf("") }
    var editedEntry by remember { mutableStateOf<TimetableEntry?>(null) }

    val longFormSubjects = listOf(
        "Matematik", "Sains", "Sejarah", "Bahasa Melayu", "Bahasa Inggeris",
        "Pendidikan Islam / Moral", "Seni Visual", "PJPK", "Muzik", "Perhimpunan", "REHAT"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentStep) {
            0 -> {
                val availableInSchedule = scheduleData.map { it.subject }.distinct().sorted()
                EditSelectionContainer(title = "Select Subject", onBack = onCancelClick) {
                    items(availableInSchedule) { subject ->
                        EditUniqueChoiceCard(title = subject, subtitle = "Tap to see all time slots") {
                            selectedSubjectFilter = subject
                            currentStep = 1
                        }
                    }
                }
            }

            1 -> {
                val slots = scheduleData.filter { it.subject == selectedSubjectFilter }
                EditSelectionContainer(title = "Select Slot", onBack = { currentStep = 0 }) {
                    items(slots) { entry ->
                        EditUniqueChoiceCard(
                            title = entry.dayAndTime,
                            subtitle = "Teacher: ${entry.lecturer}",
                            showDelete = true,
                            onDelete = { onDeleteClick(entry) }
                        ) {
                            editedEntry = entry
                            currentStep = 2
                        }
                    }
                }
            }

            2 -> {
                editedEntry?.let { entry ->
                    EditFormBody(
                        entry = entry,
                        onLecturerChange = { editedEntry = editedEntry?.copy(lecturer = it) },
                        onSave = { editedEntry?.let { onUpdateClick(it) } },
                        onDelete = { editedEntry?.let { onDeleteClick(it) } },
                        onCancel = { currentStep = 1 },
                        onSubjectFieldClick = { currentStep = 5 },
                        onIconFieldClick = { currentStep = 6 },
                        onDayTimeFieldClick = { currentStep = 3 },
                        onDurationFieldClick = { currentStep = 4 }
                    )
                }
            }

            3 -> {
                val days = listOf("SUN", "MON", "TUE", "WED", "THU")
                EditSelectionContainer(title = "Select Day", onBack = { currentStep = 2 }) {
                    items(days) { d ->
                        EditUniqueChoiceCard(title = d, subtitle = "") {
                            val timePart = editedEntry?.dayAndTime?.split(" ")?.getOrNull(1) ?: ""
                            editedEntry = editedEntry?.copy(dayAndTime = "$d $timePart")
                            currentStep = 2
                        }
                    }
                }
            }

            4 -> {
                val times = listOf("7.30-8.00", "8.00-8.30", "8.30-9.00", "9.00-9.30", "9.30-10.00","10.00-10.30",
                    "10.30-11.00", "11.00-11.30", "11.30-12.00", "12.00-12.30", "12.30-1.00")
                EditSelectionContainer(title = "Select Time", onBack = { currentStep = 2 }) {
                    items(times) { t ->
                        EditUniqueChoiceCard(title = t, subtitle = "") {
                            val dayPart = editedEntry?.dayAndTime?.split(" ")?.firstOrNull() ?: ""
                            editedEntry = editedEntry?.copy(duration = t, dayAndTime = "$dayPart $t")
                            currentStep = 2
                        }
                    }
                }
            }

            5 -> {
                EditSelectionContainer(title = "Change Subject", onBack = { currentStep = 2 }) {
                    items(longFormSubjects) { sub ->
                        EditUniqueChoiceCard(title = sub, subtitle = "Change to long form") {
                            editedEntry = editedEntry?.copy(subject = sub)
                            currentStep = 2
                        }
                    }
                }
            }

            6 -> {
                editedEntry?.let { entry ->
                    IconPickerScreen(
                        subjectName = entry.subject,
                        currentIconRes = entry.iconRes,
                        onIconSelected = { newIcon ->
                            editedEntry = editedEntry?.copy(iconRes = newIcon)
                            currentStep = 2
                        },
                        onBack = { currentStep = 2 }
                    )
                }
            }
        }
    }
}

// --- PRIVATE/UNIQUE HELPER COMPONENTS ---

@Composable
private fun EditSelectionContainer(title: String, onBack: () -> Unit, content: LazyListScope.() -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp)) {
        Spacer(Modifier.height(50.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
            Text(title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize(), content = content)
    }
}

@Composable
private fun EditUniqueChoiceCard(title: String, subtitle: String, showDelete: Boolean = false, onDelete: (() -> Unit)? = null, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E6D3)),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                if (subtitle.isNotEmpty()) Text(subtitle, fontSize = 14.sp, color = Color.DarkGray)
            }
            if (showDelete && onDelete != null) {
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFFF3B30)) }
            }
            Icon(Icons.Default.Edit, "Edit", tint = Color(0xFFB58B43))
        }
    }
}

@Composable
private fun EditFormBody(
    entry: TimetableEntry,
    onSubjectFieldClick: () -> Unit,
    onIconFieldClick: () -> Unit,
    onDurationFieldClick: () -> Unit,
    onDayTimeFieldClick: () -> Unit,
    onLecturerChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    val inputBgColor = Color(0xFFF5E6D3)
    val isSpecial = entry.subject.uppercase() == "REHAT" || entry.subject.uppercase() == "PERHIMPUNAN"

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Spacer(Modifier.height(50.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
                Text("Edit Details", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("Icon", color = Color.White, fontSize = 14.sp)
                    Surface(onClick = onIconFieldClick, modifier = Modifier.size(60.dp).padding(top = 4.dp), color = inputBgColor, shape = RoundedCornerShape(8.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            entry.iconRes?.let { Image(painter = painterResource(id = it), null, Modifier.size(35.dp)) }
                        }
                    }
                }
                Spacer(Modifier.width(15.dp))
                Column(modifier = Modifier.weight(1f)) {
                    EditCustomField("Subject", entry.subject, inputBgColor, onSubjectFieldClick)
                }
            }
            Spacer(Modifier.height(15.dp))
        }

        item {
            Text("Teacher Name", color = Color.White, fontSize = 14.sp)
            TextField(
                value = if (isSpecial) "-" else entry.lecturer,
                onValueChange = { if (!isSpecial) onLecturerChange(it) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSpecial,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = inputBgColor,
                    unfocusedContainerColor = inputBgColor,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(Modifier.height(15.dp))
        }

        item {
            val dayOnly = entry.dayAndTime.split(" ").firstOrNull() ?: ""
            EditCustomField("Day", dayOnly, inputBgColor, onDayTimeFieldClick)
            Spacer(Modifier.height(15.dp))
            EditCustomField("Time Slot", entry.duration, inputBgColor, onDurationFieldClick)
            Spacer(Modifier.height(30.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                Button(onClick = onSave, modifier = Modifier.weight(1f).height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB58B43)), shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Default.Check, null); Spacer(Modifier.width(8.dp)); Text("SAVE")
                }
                Button(onClick = onCancel, modifier = Modifier.weight(1f).height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)), shape = RoundedCornerShape(10.dp)) {
                    Text("CANCEL", color = Color.White)
                }
            }
            Spacer(Modifier.height(15.dp))
            Button(onClick = onDelete, modifier = Modifier.fillMaxWidth().height(55.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)), shape = RoundedCornerShape(10.dp)) {
                Icon(Icons.Default.Delete, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text("DELETE ENTRY", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EditCustomField(label: String, value: String, bgColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Surface(modifier = Modifier.fillMaxWidth().height(50.dp), onClick = onClick, color = bgColor, shape = RoundedCornerShape(8.dp)) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = value, color = Color.Black)
            }
        }
    }
}