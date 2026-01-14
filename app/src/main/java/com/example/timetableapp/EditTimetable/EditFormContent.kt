package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
fun EditTimetableManager(
    scheduleData: List<TimetableEntry>,
    onUpdateEntry: (TimetableEntry, TimetableEntry) -> Unit, // (Original, Updated)
    onDeleteEntry: (TimetableEntry) -> Unit,
    onBack: () -> Unit
) {
    // 1: Select Subject, 2: Select Slot, 3: Edit Form
    var step by remember { mutableIntStateOf(1) }
    var selectedSubject by remember { mutableStateOf("") }
    var selectedEntry by remember { mutableStateOf<TimetableEntry?>(null) }
    var editedLecturer by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        when (step) {
            1 -> {
                val subjects = scheduleData.map { it.subject }.distinct().sorted()
                SelectionListLayout(title = "Select Subject", onBack = onBack) {
                    if (subjects.isEmpty()) {
                        item { Text("No subjects found.", color = Color.White.copy(alpha = 0.6f)) }
                    }
                    items(subjects) { subject ->
                        EditChoiceCard(title = subject, subtitle = "View all sessions") {
                            selectedSubject = subject
                            step = 2
                        }
                    }
                }
            }
            2 -> {
                val slots = scheduleData.filter { it.subject == selectedSubject }
                SelectionListLayout(title = "Select Slot", onBack = { step = 1 }) {
                    items(slots) { entry ->
                        EditChoiceCard(
                            title = entry.dayAndTime,
                            subtitle = "Teacher: ${entry.lecturer}",
                            isSlot = true,
                            onDelete = { onDeleteEntry(entry) }
                        ) {
                            selectedEntry = entry
                            editedLecturer = entry.lecturer
                            step = 3
                        }
                    }
                }
            }
            3 -> {
                selectedEntry?.let { entry ->
                    EditFormContent(
                        entry = entry.copy(lecturer = editedLecturer),
                        onCancel = { step = 2 },
                        onLecturerChange = { editedLecturer = it },
                        onSave = {
                            onUpdateEntry(entry, entry.copy(lecturer = editedLecturer))
                            step = 1
                        },
                        onDelete = {
                            onDeleteEntry(entry)
                            step = 1
                        },
                        onSubjectFieldClick = { /* Link to your Subject Picker Screen */ },
                        onIconFieldClick = { /* Link to your Icon Picker Screen */ },
                        onDurationFieldClick = { /* Link to your Duration Picker Screen */ },
                        onDayTimeFieldClick = { /* Link to your Day Picker Screen */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectionListLayout(
    title: String,
    onBack: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp)) {
        Spacer(Modifier.height(50.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
            Text(title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(20.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
            content = content
        )
    }
}

@Composable
fun EditChoiceCard(
    title: String,
    subtitle: String,
    isSlot: Boolean = false,
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E6D3)),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                Text(subtitle, fontSize = 14.sp, color = Color.DarkGray)
            }
            if (isSlot && onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFFF3B30))
                }
            }
            Icon(Icons.Default.Edit, "Edit", tint = Color(0xFFB58B43))
        }
    }
}

@Composable
fun EditFormContent(
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
    val isRehat = entry.subject.uppercase() == "REHAT"

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(50.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text("Edit Details", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(20.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("Icon", color = Color.White, fontSize = 14.sp)
                    Surface(
                        onClick = onIconFieldClick,
                        modifier = Modifier.size(60.dp).padding(top = 4.dp),
                        color = inputBgColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            entry.iconRes?.let { Image(painter = painterResource(id = it), null, Modifier.size(35.dp)) }
                        }
                    }
                }
                Spacer(Modifier.width(15.dp))
                Column(modifier = Modifier.weight(1f)) {
                    ReadOnlyField("Subject", entry.subject, inputBgColor, onSubjectFieldClick)
                }
            }
            Spacer(Modifier.height(15.dp))
        }

        item {
            Text("Teacher Name", color = Color.White, fontSize = 14.sp)
            TextField(
                value = if (isRehat) "-" else entry.lecturer,
                onValueChange = { if (!isRehat) onLecturerChange(it) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isRehat,
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
            ReadOnlyField("Day", entry.dayAndTime.split(" ").first(), inputBgColor, onDayTimeFieldClick)
            Spacer(Modifier.height(15.dp))
            ReadOnlyField("Time Slot", entry.duration, inputBgColor, onDurationFieldClick)
            Spacer(Modifier.height(30.dp))
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB58B43)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, null); Spacer(Modifier.width(8.dp)); Text("SAVE")
                }
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f).height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("CANCEL", color = Color.White)
                }
            }

            Spacer(Modifier.height(15.dp))

            Button(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3B30)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Delete, null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("DELETE ENTRY", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
fun ReadOnlyField(label: String, value: String, bgColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = Color.White, fontSize = 14.sp)
        Surface(
            modifier = Modifier.fillMaxWidth().height(50.dp),
            onClick = onClick,
            color = bgColor,
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = value, color = Color.Black)
            }
        }
    }
}