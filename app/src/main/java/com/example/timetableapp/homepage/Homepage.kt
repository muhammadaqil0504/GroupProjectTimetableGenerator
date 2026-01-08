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
    // Colors based on Mode
    val cardColor = if (isDarkMode) Color(0xFF2B2B2B).copy(alpha = 0.85f) else Color(0xFFF5E6D3).copy(alpha = 0.85f)
    val buttonTextColor = if (isDarkMode) Color.White else Color.Black
    val solidRed = Color(0xFFD32F2F)

    var showResetDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- 1. EXTRA LARGE FLOATING DARK MODE ICON (NO SHAPE) ---
        IconButton(
            onClick = onDarkModeToggle,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 10.dp, end = 10.dp)
                .size(100.dp) // The clickable area is now 100dp
        ) {
            Icon(
                painter = painterResource(
                    id = if (isDarkMode) R.drawable.light else R.drawable.dark
                ),
                contentDescription = "Toggle Dark Mode",
                tint = if (isDarkMode) Color(0xFFFFD700) else Color.White,
                modifier = Modifier.size(70.dp) // The icon itself is now 70dp
            )
        }

        // --- 2. MAIN CONTENT COLUMN ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Increased spacer to account for the much larger icon
            Spacer(Modifier.height(100.dp))

            // Greeting Texts
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

            // Menu Buttons
            MenuButton("Generate Timetable", Icons.Default.DateRange, cardColor, buttonTextColor, onGenerateNavClick)
            MenuButton("Edit Timetable", Icons.Default.Edit, cardColor, buttonTextColor, onEditNavClick)
            MenuButton("Subject", Icons.Default.Star, cardColor, buttonTextColor, onSubjectNavClick)
            MenuButton("Timetable", Icons.Outlined.Notifications, cardColor, buttonTextColor, onTimetableClick)

            Spacer(modifier = Modifier.weight(1f))

            // RESET BUTTON
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

        // Reset Confirmation Dialog
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
            Icon(icon, null, tint = textColor, modifier = Modifier.size(28.dp))
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