package com.example.circuitlens.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.AuthToggle
import com.example.circuitlens.ui.components.CircuitButton
import com.example.circuitlens.ui.components.CircuitInputField
import com.example.circuitlens.ui.navigation.Screen

@Composable
fun SignUpScreen(onNavigate: (Screen) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Get Started", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        AuthToggle(isLogin = false, onNavigate = onNavigate)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                CircuitInputField(value = firstName, onValueChange = { firstName = it }, label = "First Name", placeholder = "")
            }
            Box(modifier = Modifier.weight(1f)) {
                CircuitInputField(value = lastName, onValueChange = { lastName = it }, label = "Last Name", placeholder = "")
            }
        }
        CircuitInputField(value = email, onValueChange = { email = it }, label = "Email", placeholder = "Enter email")
        CircuitInputField(value = password, onValueChange = { password = it }, label = "Create Password", placeholder = "Password", isPassword = true)

        Spacer(modifier = Modifier.height(24.dp))
        CircuitButton(text = "Register", onClick = { onNavigate(Screen.HOME) })
    }
}
