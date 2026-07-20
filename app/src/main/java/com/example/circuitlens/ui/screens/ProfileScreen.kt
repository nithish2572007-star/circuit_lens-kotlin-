package com.example.circuitlens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.CircuitButton
import com.example.circuitlens.ui.components.CircuitInputField
import com.example.circuitlens.ui.state.CircuitStateHolder
import com.example.circuitlens.ui.theme.BorderGreen
import com.example.circuitlens.ui.theme.LimePrimary

@Composable
fun ProfileScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    var name by remember { mutableStateOf(CircuitStateHolder.loggedInUserName) }
    var email by remember { mutableStateOf(CircuitStateHolder.loggedInUserEmail) }
    var username by remember { mutableStateOf(if (email.contains("@")) "@" + email.substringBefore("@") else "@user") }
    var password by remember { mutableStateOf("********") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LimePrimary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.weight(0.3f))
            Text("Profile", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(0.6f))
        }

        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(120.dp).background(LimePrimary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = "Profile Picture", tint = Color.White, modifier = Modifier.size(64.dp))
                Box(
                    modifier = Modifier.size(32.dp).background(Color.Black, CircleShape).align(Alignment.BottomEnd).border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = "Edit Picture", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        CircuitInputField(value = name, onValueChange = { name = it }, label = "Name", placeholder = "")
        CircuitInputField(value = email, onValueChange = { email = it }, label = "Email Address", placeholder = "")
        CircuitInputField(value = username, onValueChange = { username = it }, label = "Username", placeholder = "")
        CircuitInputField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "", isPassword = true)

        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                CircuitButton(text = "Save", onClick = onBack)
            }
            Box(modifier = Modifier.weight(1f)) {
                CircuitButton(
                    text = "Log Out",
                    onClick = onLogout
                )
            }
        }
    }
}
