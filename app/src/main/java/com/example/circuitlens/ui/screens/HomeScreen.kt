package com.example.circuitlens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.ActivityItem
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.components.OverviewCard
import com.example.circuitlens.ui.theme.*

@Composable
fun HomeScreen(onProfileClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        CircuitHeader(onProfileClick = onProfileClick)

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Welcome back,", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Medium)
            Text("Abc Xyz", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Let's analyze and perfect your circuits", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            // Overview Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, BorderGreen), RoundedCornerShape(16.dp))
                    .background(DarkBg)
                    .padding(16.dp)
            ) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Overview", color = Color.White, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Today", color = Color.White, fontSize = 14.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown", tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OverviewCard(modifier = Modifier.weight(1f), count = "7", label = "Circuits Analyzed", icon = Icons.Default.Settings)
                        OverviewCard(modifier = Modifier.weight(1f), count = "4", label = "Issues Fixed", icon = Icons.Default.CheckCircle)
                        OverviewCard(modifier = Modifier.weight(1f), count = "3", label = "Errors Detected", icon = Icons.Default.Warning)
                    }
                }
            }

            // Action Button Card
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBg, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(48.dp).background(DarkBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Scan", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Scan a circuit", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Capture or upload a photo to analyze", color = TextGray, fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.White)
            }

            // Recent Activity Section
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Recent Activity", color = Color.White, fontWeight = FontWeight.Bold)
                Text("View all", color = LimePrimary, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            ActivityItem("Circuit #5", "19 June 2026, 21:57 PM")
            Spacer(modifier = Modifier.height(8.dp))
            ActivityItem("Circuit #4", "11 June 2026, 07:31 AM")
        }
    }
}
