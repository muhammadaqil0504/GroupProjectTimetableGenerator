package com.example.timetableapp.generatetable

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.TimetableEntry
import kotlinx.coroutines.launch

@Composable
fun GenerateTimetableInterface(
    onBackClick: () -> Unit,
    onAddEntry: (TimetableEntry) -> Unit,
    onGenerateAutomatically: () -> Unit
) {
    // Styling Colors
    val inputBgColor = Color(0xFFF5E6D3)
    val buttonGold = Color(0xFFB58B43)

    // Snackbar (Notice) States
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Form States
    var selectedSubject by remember { mutableStateOf("Select Subject") }
    var selectedIconRes by remember { mutableStateOf<Int?>(null) }
    var lecturerName by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf("Select Duration") }
    var selectedDayOnly by remember { mutableStateOf("Select Day") }
    var currentStep by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.Transparent, // Shows the main app background
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF333333),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (currentStep) {
                1 -> SubjectListScreen(
                    onSubjectSelected = { selectedSubject = it; currentStep = 2 },
                    onBack = { currentStep = 0 }
                )
                2 -> IconPickerScreen(
                    subjectName = selectedSubject,
                    onIconSelected = { selectedIconRes = it; currentStep = 0 },
                    onBack = { currentStep = 1 }
                )
                3 -> DurationSelectionScreen(
                    onDurationSelected = { selectedDuration = it; currentStep = 0 },
                    onBack = { currentStep = 0 }
                )
                4 -> DayTimeSelectionScreen(
                    onSelectionComplete = { selectedDayOnly = it; currentStep = 0 },
                    onBack = { currentStep = 0 }
                )
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 25.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Spacer(Modifier.height(50.dp))
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                        }

                        Text(
                            text = "Generate Timetable",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        InputField("Subject", selectedSubject, true, inputBgColor) { currentStep = 1 }

                        if (selectedIconRes != null) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                                Text("Selected Icon: ", color = Color.White, fontSize = 14.sp)
                                Surface(modifier = Modifier.size(40.dp), color = inputBgColor, shape = RoundedCornerShape(8.dp)) {
                                    Image(painter = painterResource(id = selectedIconRes!!), contentDescription = null, modifier = Modifier.padding(4.dp))
                                }
                            }
                        }

                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                            Text(text = "Teacher", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            TextField(
                                value = lecturerName,
                                onValueChange = { lecturerName = it },
                                modifier = Modifier.fillMaxWidth().height(55.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = inputBgColor,
                                    unfocusedContainerColor = inputBgColor,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true
                            )
                        }

                        InputField("Duration", selectedDuration, true, inputBgColor) { currentStep = 3 }
                        InputField("Day", selectedDayOnly, true, inputBgColor) { currentStep = 4 }

                        Spacer(Modifier.height(30.dp))

                        // --- ADD BUTTON WITH NOTICE ---
                        GenerateActionButton("ADD", Icons.Default.AddCircle, buttonGold) {
                            if (selectedSubject != "Select Subject" &&
                                selectedDayOnly != "Select Day" &&
                                selectedDuration != "Select Duration") {

                                onAddEntry(
                                    TimetableEntry(
                                        subject = selectedSubject,
                                        lecturer = lecturerName,
                                        duration = selectedDuration,
                                        dayAndTime = "$selectedDayOnly $selectedDuration",
                                        iconRes = selectedIconRes
                                    )
                                )

                                // Show Notice
                                scope.launch {
                                    snackbarHostState.showSnackbar("Subject added to your schedule!")
                                }

                                // Reset Form
                                selectedSubject = "Select Subject"
                                selectedIconRes = null
                                lecturerName = ""
                                selectedDuration = "Select Duration"
                                selectedDayOnly = "Select Day"
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // --- GENERATE AUTO BUTTON WITH NOTICE ---
                        GenerateActionButton("Generate Automatically", Icons.Default.Check, buttonGold) {
                            onGenerateAutomatically()

                            // Show Notice
                            scope.launch {
                                snackbarHostState.showSnackbar("Full schedule generated! Check Timetable View.")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputField(label: String, value: String, isDropdown: Boolean, bgColor: Color, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Surface(
            modifier = Modifier.fillMaxWidth().height(50.dp).clickable { onClick() },
            shape = RoundedCornerShape(8.dp),
            color = bgColor
        ) {
            Box(modifier = Modifier.padding(horizontal = 15.dp), contentAlignment = Alignment.CenterStart) {
                Text(text = value, color = Color.Black)
                if (isDropdown) {
                    Icon(Icons.Default.KeyboardArrowDown, null, Modifier.align(Alignment.CenterEnd), Color.Black)
                }
            }
        }
    }
}

@Composable
fun GenerateActionButton(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(55.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(10.dp))
            Text(text = text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}