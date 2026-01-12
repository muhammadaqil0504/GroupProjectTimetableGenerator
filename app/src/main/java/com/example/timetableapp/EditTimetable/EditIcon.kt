package com.example.timetableapp.EditTimetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.timetableapp.R

@Composable
fun IconPickerScreen(
    subjectName: String,
    currentIconRes: Int?, // Shows the "Current Icon" preview
    onIconSelected: (Int) -> Unit,
    onBack: () -> Unit
) {
    // Removed chalkboardGreen variable for the background
    val iconBoxColor = Color(0xFFF5E6D3)
    val saveButtonGold = Color(0xFFB58B43)

    var tempSelectedIcon by remember { mutableStateOf<Int?>(null) }

    val subjectIconsMap = mapOf(
        "Perhimpunan" to listOf(R.drawable.perhimpunan_icon),
        "Matematik" to listOf(R.drawable.math_icon, R.drawable.math_icon2, R.drawable.math_icon3, R.drawable.math_icon4, R.drawable.math_icon5),
        "Sains" to listOf(R.drawable.sains_icon, R.drawable.sains_icon2, R.drawable.sains_icon3, R.drawable.sains_icon4, R.drawable.sains_icon5),
        "Sejarah" to listOf(R.drawable.sejarah_icon, R.drawable.sejarah_icon2, R.drawable.sejarah_icon3, R.drawable.sejarah_icon4, R.drawable.sejarah_icon5),
        "Bahasa Melayu" to listOf(R.drawable.bm_icon, R.drawable.bm_icon2, R.drawable.bm_icon3, R.drawable.bm_icon4, R.drawable.bm_icon5),
        "Bahasa Inggeris" to listOf(R.drawable.bi_icon, R.drawable.bi_icon2, R.drawable.bi_icon3, R.drawable.bi_icon4, R.drawable.bi_icon5),
        "Pendidikan Islam / Moral" to listOf(R.drawable.pai_icon, R.drawable.pai_icon2, R.drawable.pai_icon3, R.drawable.pai_icon4, R.drawable.pai_icon5),
        "Seni Visual" to listOf(R.drawable.psv_icon, R.drawable.psv_icon2, R.drawable.psv_icon3, R.drawable.psv_icon4, R.drawable.psv_icon5),
        "PJPK" to listOf(R.drawable.pjk_icon, R.drawable.pjk_icon2, R.drawable.pjk_icon3, R.drawable.pjk_icon4, R.drawable.pjk_icon5),
        "Muzik" to listOf(R.drawable.muzik_icon, R.drawable.muzik_icon2, R.drawable.muzik_icon3, R.drawable.muzik_icon4, R.drawable.muzik_icon5)
    )

    val iconsToShow = subjectIconsMap[subjectName] ?: listOf(R.drawable.math_icon, R.drawable.sains_icon, R.drawable.bm_icon)

    // CHANGED: Removed .background(chalkboardGreen)
    Box(modifier = Modifier.fillMaxSize()) {


        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            // Header Section
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "Icon",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).wrapContentWidth(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.width(48.dp)) // Balanced the back button
            }

            Text("Subject", color = Color.White, fontSize = 14.sp)
            Text(
                text = subjectName,
                color = Color.Black,
                modifier = Modifier
                    .background(iconBoxColor, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))

            // Current Icon Preview
            Text("Current Icon :", color = Color.White)
            Card(
                modifier = Modifier.size(80.dp).padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = iconBoxColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    currentIconRes?.let {
                        Image(painter = painterResource(id = it), contentDescription = null, modifier = Modifier.size(50.dp))
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Choose New Icon :", color = Color.White)

            // Grid Selection
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f).padding(top = 10.dp)
            ) {
                items(iconsToShow) { iconRes ->
                    Card(
                        modifier = Modifier.aspectRatio(1f).clickable { tempSelectedIcon = iconRes },
                        colors = CardDefaults.cardColors(containerColor = iconBoxColor),
                        border = if (tempSelectedIcon == iconRes) BorderStroke(3.dp, saveButtonGold) else null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Image(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(70.dp))
                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = { tempSelectedIcon?.let { onIconSelected(it) } },
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp).height(50.dp),
                enabled = tempSelectedIcon != null,
                colors = ButtonDefaults.buttonColors(containerColor = saveButtonGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("✓ SAVE", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}