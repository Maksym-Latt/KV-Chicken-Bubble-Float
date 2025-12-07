package com.chicken.bubblefloat.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.chicken.bubblefloat.R

private val Tillana = FontFamily(
    Font(R.font.tillana_regular, weight = FontWeight.Normal),
    Font(R.font.tillana_medium, weight = FontWeight.Medium),
    Font(R.font.tillana_semi_bold, weight = FontWeight.SemiBold),
    Font(R.font.tillana_bold, weight = FontWeight.Bold),
    Font(R.font.tillana_extra_bold, weight = FontWeight.ExtraBold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = Tillana,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 52.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Tillana,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 36.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Tillana,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Tillana,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Tillana,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Tillana,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 18.sp
    )
)