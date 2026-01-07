package com.example.timetableapp.EditTimetable

import androidx.compose.runtime.*
import com.example.timetableapp.TimetableEntry

@Composable
fun EditTimetableScreen(
    entryToEdit: TimetableEntry,
    onUpdateClick: (TimetableEntry) -> Unit,
    onCancelClick: () -> Unit
) {
    // Local state to hold edits before saving
    var editedEntry by remember { mutableStateOf(entryToEdit) }

    // Navigation state: 0=Main Form, 1=Subject, 2=Icon, 3=Duration, 4=Day/Time
    var currentStep by remember { mutableIntStateOf(0) }

    when (currentStep) {
        // STEP 1: SUBJECT SELECTION
        1 -> SubjectEditScreen(
            onSubjectSelected = { selectedSubject ->
                editedEntry = editedEntry.copy(subject = selectedSubject)
                currentStep = 2 // Move to Icon selection automatically
            },
            onBack = { currentStep = 0 }
        )

        // STEP 2: ICON SELECTION
        2 -> IconPickerScreen(
            subjectName = editedEntry.subject,
            currentIconRes = editedEntry.iconRes,
            onIconSelected = { selectedIcon ->
                editedEntry = editedEntry.copy(iconRes = selectedIcon)
                currentStep = 0 // Return to main form
            },
            onBack = { currentStep = 1 }
        )

        // STEP 3: DURATION SELECTION (FIXED TO SYNC WITH WEEKLY VIEW)
        3 -> EditDurationScreen(
            // We pass the current day so the Duration screen can rebuild the dayAndTime string
            currentDay = editedEntry.dayAndTime.split(" ").firstOrNull() ?: "SUN",
            onDurationSelected = { selectedDuration, newDayAndTime ->
                // CRITICAL: We update BOTH fields so Weekly View finds the correct slot
                editedEntry = editedEntry.copy(
                    duration = selectedDuration,
                    dayAndTime = newDayAndTime
                )
                currentStep = 0
            },
            onBack = { currentStep = 0 }
        )

        // STEP 4: DAY SELECTION (FIXED TO PRESERVE TIME)
        4 -> EditDaySelectionScreen(
            // Pass the time part only (e.g., "08.00")
            currentTime = editedEntry.dayAndTime.split(" ").lastOrNull()?.replace(":", ".") ?: "08.00",
            onSelectionComplete = { selectedDayTime ->
                editedEntry = editedEntry.copy(dayAndTime = selectedDayTime)
                currentStep = 0
            },
            onBack = { currentStep = 0 }
        )

        // STEP 0: THE MAIN EDIT FORM
        else -> EditFormContent(
            entry = editedEntry,
            onSubjectFieldClick = { currentStep = 1 },
            onIconFieldClick = { currentStep = 2 },
            onDurationFieldClick = { currentStep = 3 },
            onDayTimeFieldClick = { currentStep = 4 },
            onLecturerChange = { newLecturer ->
                editedEntry = editedEntry.copy(lecturer = newLecturer)
            },
            onSave = { onUpdateClick(editedEntry) },
            onCancel = onCancelClick
        )
    }
}