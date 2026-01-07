package com.example.timetableapp.homepage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
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
import com.example.timetableapp.R
import com.example.timetableapp.ScallopedHeader

@Composable
fun HomepageInterface(
    isDarkMode: Boolean,
    onDarkModeToggle: () -> Unit,
    onGenerateNavClick: () -> Unit,
    onEditNavClick: () -> Unit,
    onSubjectNavClick: () -> Unit,
    onTimetableClick: () -> Unit,
    onResetConfirm: () -> Unit
) {
    // Dynamic styling for Dark Mode - Background colors for buttons
    // We use slightly lower alpha (0.85f) so the global background peeks through the buttons
    val cardColor = if (isDarkMode) Color(0xFF2B2B2B).copy(alpha = 0.85f) else Color(0xFFF5E6D3).copy(alpha = 0.85f)
    val buttonTextColor = if (isDarkMode) Color.White else Color.Black
    val solidRed = Color(0xFFD32F2F)

    var showResetDialog by remember { mutableStateOf(false) }

    // Use a Box to layer the ScallopedHeader and Toggle on top of the Column
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Header (Decorative white edge)
        ScallopedHeader()

        // 2. Mode Toggle Switch - Large touch area and large icon
        IconButton(
            onClick = onDarkModeToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 10.dp, end = 10.dp)
                .size(64.dp)
        ) {
            Icon(
                painter = painterResource(
                    id = if (isDarkMode) R.drawable.light else R.drawable.dark
                ),
                contentDescription = "Toggle Dark Mode",
                tint = if (isDarkMode) Color.Yellow else Color.White,
                modifier = Modifier.size(42.dp)
            )
        }

        // 3. Main Content Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Text Greeting
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                Text(
                    text = "HI STUDENT !",
                    color = Color.White,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "READY TO PLAN YOUR WEEK ?",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(35.dp))

            // Menu Buttons - Colors update based on isDarkMode
            MenuButton("Generate Timetable", Icons.Default.DateRange, cardColor, buttonTextColor, onGenerateNavClick)
            MenuButton("Edit Timetable", Icons.Default.Edit, cardColor, buttonTextColor, onEditNavClick)
            MenuButton("Subject", Icons.Default.Star, cardColor, buttonTextColor, onSubjectNavClick)
            MenuButton("TIMETABLE", Icons.Outlined.Notifications, cardColor, buttonTextColor, onTimetableClick)

            Spacer(modifier = Modifier.weight(1f))

            // THE SOLID RED RESET BUTTON
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = solidRed,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(Icons.Default.Warning, null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text("RESET FOR NEW YEAR", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        // Reset Logic Dialog
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text("Permanent Reset?", fontWeight = FontWeight.Bold) },
                text = { Text("This will delete all your data for the current year. You cannot undo this action.") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = solidRed),
                        onClick = {
                            showResetDialog = false
                            onResetConfirm()
                        }
                    ) { Text("Yes, Reset All", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    icon: ImageVector,
    bgColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .height(68.dp),
        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = textColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(20.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}