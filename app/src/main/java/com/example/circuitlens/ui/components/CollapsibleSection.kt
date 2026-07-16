package com.example.circuitlens.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.theme.BorderGreen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray

@Composable
fun CollapsibleSection(title: String, placeholder: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BorderGreen.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .border(BorderStroke(1.dp, LimePrimary), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand", tint = Color.Black)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(top = 8.dp)
                .background(CardBg.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            Text(placeholder, color = TextGray, fontSize = 12.sp)
        }
    }
}
