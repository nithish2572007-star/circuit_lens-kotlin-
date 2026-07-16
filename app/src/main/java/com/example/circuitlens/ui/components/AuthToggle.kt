package com.example.circuitlens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.circuitlens.ui.navigation.Screen
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary

@Composable
fun AuthToggle(isLogin: Boolean, onNavigate: (Screen) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
            .background(CardBg, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .background(if (isLogin) LimePrimary else Color.Transparent, RoundedCornerShape(10.dp))
                .clickable { onNavigate(Screen.LOGIN) },
            contentAlignment = Alignment.Center
        ) {
            Text("Log In", color = if (isLogin) Color.Black else Color.White, fontWeight = FontWeight.Bold)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .background(if (!isLogin) LimePrimary else Color.Transparent, RoundedCornerShape(10.dp))
                .clickable { onNavigate(Screen.SIGNUP) },
            contentAlignment = Alignment.Center
        ) {
            Text("Sign Up", color = if (!isLogin) Color.Black else Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
