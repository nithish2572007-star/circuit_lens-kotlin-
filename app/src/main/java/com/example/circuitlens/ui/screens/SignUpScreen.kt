package com.example.circuitlens.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.circuitlens.ui.state.CircuitStateHolder
import com.example.circuitlens.ui.theme.LimePrimary

@Composable
fun SignUpScreen(onNavigate: (Screen) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    val isLoading = CircuitStateHolder.isLoading

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Get Started", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        AuthToggle(isLogin = false, onNavigate = onNavigate)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                CircuitInputField(value = firstName, onValueChange = { firstName = it; localError = null }, label = "First Name", placeholder = "")
            }
            Box(modifier = Modifier.weight(1f)) {
                CircuitInputField(value = lastName, onValueChange = { lastName = it; localError = null }, label = "Last Name", placeholder = "")
            }
        }
        CircuitInputField(value = email, onValueChange = { email = it; localError = null }, label = "Email", placeholder = "Enter email")
        CircuitInputField(value = password, onValueChange = { password = it; localError = null }, label = "Create Password", placeholder = "Password", isPassword = true)

        if (localError != null) {
            Text(localError!!, color = Color(0xFFFF5252), fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(color = LimePrimary)
        } else {
            CircuitButton(
                text = "Register",
                onClick = {
                    if (firstName.isBlank() || email.isBlank() || password.isBlank()) {
                        localError = "First name, email, and password are required"
                        return@CircuitButton
                    }
                    CircuitStateHolder.signup(
                        firstNameVal = firstName,
                        lastNameVal = lastName,
                        emailVal = email,
                        passwordVal = password,
                        onSuccess = { onNavigate(Screen.HOME) },
                        onError = { localError = it }
                    )
                }
            )
        }
    }
}
