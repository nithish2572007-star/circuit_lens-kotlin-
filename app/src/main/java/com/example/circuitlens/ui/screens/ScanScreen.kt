package com.example.circuitlens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.CircuitHeader
import com.example.circuitlens.ui.theme.*

@Composable
fun ScanScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader()
        Text("Capture or Upload", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Your Circuits", color = LimePrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("Take a clear photo or upload an image of your circuit board or diagram.", color = TextGray, modifier = Modifier.padding(top = 8.dp, bottom = 24.dp))

        // Large Option 1
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(LimePrimary, LimeGradientEnd)), RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PhotoCamera, contentDescription = "Take Photo", tint = Color.Black, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Take a photo", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Use your camera to capture the circuit", color = Color.Black.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.Black)
            }
        }

        // Large Option 2
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(BorderStroke(1.dp, BorderGreen), RoundedCornerShape(16.dp))
                .background(CardBg)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Image, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Choose from Gallery", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Select an existing image", color = TextGray, fontSize = 12.sp)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Go", tint = Color.White)
            }
        }
    }
}
