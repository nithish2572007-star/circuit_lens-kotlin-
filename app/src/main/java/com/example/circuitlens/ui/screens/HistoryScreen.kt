package com.example.circuitlens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.theme.BorderGreen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.TextGray

@Composable
fun HistoryScreen(onProfileClick: () -> Unit) {
    val itemsList = listOf("CHAT 1", "CHAT 2", "CHAT 3", "CHAT 4", "CHAT 5")
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader(onProfileClick = onProfileClick)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(itemsList) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, BorderGreen), RoundedCornerShape(16.dp))
                        .background(CardBg)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(44.dp).background(BorderGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item, color = Color.White, fontWeight = FontWeight.Bold)
                        Text("Date", color = TextGray, fontSize = 12.sp)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "Open Chat", tint = Color.White)
                }
            }
        }
    }
}
