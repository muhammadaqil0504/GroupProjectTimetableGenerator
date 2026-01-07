package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditLecturerInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    bgColor: Color = Color(0xFFF5E6D3) // Default to the cream color from mockup
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp, // Slightly larger to match Edit screen
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp), // Height adjusted to match other form fields
            colors = TextFieldDefaults.colors(
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                disabledContainerColor = bgColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            textStyle = TextStyle(
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            ),
            singleLine = true
        )
    }
}