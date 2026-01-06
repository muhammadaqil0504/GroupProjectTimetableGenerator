package com.example.timetableapp.generatetable

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
import com.example.timetableapp.ScallopedHeader

@Composable
fun IconPickerScreen(subjectName: String, onIconSelected: (Int) -> Unit, onBack: () -> Unit) {
    val chalkboardGreen = Color(0xFF4B6E63)
    val iconBoxColor = Color(0xFFF5E6D3)
    val highlightColor = Color(0xFFB58B43)

    var tempSelectedIcon by remember { mutableStateOf<Int?>(null) }

    // Use a Map instead of 'when' for cleaner icon management
    val subjectIconsMap = mapOf(
        "Matematik" to listOf(R.drawable.math_icon, R.drawable.math_icon2, R.drawable.math_icon3, R.drawable.math_icon4, R.drawable.math_icon5),
        "Sains" to listOf(R.drawable.sains_icon, R.drawable.sains_icon2, R.drawable.sains_icon3, R.drawable.sains_icon4, R.drawable.sains_icon5),
        "Sejarah" to listOf(R.drawable.sejarah_icon, R.drawable.sejarah_icon2, R.drawable.sejarah_icon3, R.drawable.sejarah_icon4, R.drawable.sejarah_icon5),
        "Bahasa Melayu" to listOf(R.drawable.bm_icon, R.drawable.bm_icon2, R.drawable.bm_icon3, R.drawable.bm_icon4, R.drawable.bm_icon5),
        "Bahasa Inggeris" to listOf(R.drawable.bi_icon, R.drawable.bi_icon2, R.drawable.bi_icon3, R.drawable.bi_icon4, R.drawable.bi_icon5),
        "Pendidikan Islam" to listOf(R.drawable.pai_icon, R.drawable.pai_icon2, R.drawable.pai_icon3, R.drawable.pai_icon4, R.drawable.pai_icon5),
        "Seni Visual" to listOf(R.drawable.psv_icon, R.drawable.psv_icon2, R.drawable.psv_icon3, R.drawable.psv_icon4, R.drawable.psv_icon5),
        "PJPK" to listOf(R.drawable.pjk_icon, R.drawable.pjk_icon2, R.drawable.pjk_icon3, R.drawable.pjk_icon4, R.drawable.pjk_icon5),
        "Muzik" to listOf(R.drawable.muzik_icon, R.drawable.muzik_icon2, R.drawable.muzik_icon3, R.drawable.muzik_icon4, R.drawable.muzik_icon5)
    )

    // Fallback icons if the subject is not found in the map
    val defaultIcons = listOf(
        R.drawable.math_icon,
        R.drawable.sains_icon,
        R.drawable.bm_icon,
        R.drawable.bi_icon,
        R.drawable.sejarah_icon
    )

    // Retrieve icons from the map or use the default
    val iconsToShow = subjectIconsMap[subjectName] ?: defaultIcons

    Box(modifier = Modifier.fillMaxSize().background(chalkboardGreen)) {
        ScallopedHeader()

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    "Icon Selection",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Pick one of 5 icons for $subjectName",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(15.dp),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(items = iconsToShow) { iconRes: Int ->
                    Card(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clickable { tempSelectedIcon = iconRes },
                        colors = CardDefaults.cardColors(
                            containerColor = if (tempSelectedIcon == iconRes) Color.White else iconBoxColor
                        ),
                        border = if (tempSelectedIcon == iconRes) BorderStroke(3.dp, highlightColor) else null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { tempSelectedIcon?.let { onIconSelected(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
                    .height(55.dp),
                enabled = tempSelectedIcon != null,
                colors = ButtonDefaults.buttonColors(containerColor = highlightColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "SAVE ICON",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}