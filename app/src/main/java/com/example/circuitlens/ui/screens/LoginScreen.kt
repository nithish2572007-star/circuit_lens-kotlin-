package com.example.circuitlens.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.circuitlens.ui.components.AuthToggle
import com.example.circuitlens.ui.components.CircuitButton
import com.example.circuitlens.ui.components.CircuitInputField
import com.example.circuitlens.ui.navigation.Screen
import com.example.circuitlens.ui.state.CircuitStateHolder
import com.example.circuitlens.ui.theme.CardBg
import com.example.circuitlens.ui.theme.LimePrimary
import com.example.circuitlens.ui.theme.TextGray

@Composable
fun LoginScreen(onNavigate: (Screen) -> Unit) {
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
        Text("Create an account or log in\nto explore about our app", color = TextGray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))

        AuthToggle(isLogin = true, onNavigate = onNavigate)

        CircuitInputField(value = email, onValueChange = { email = it; localError = null }, label = "Email", placeholder = "Enter email")
        CircuitInputField(value = password, onValueChange = { password = it; localError = null }, label = "Password", placeholder = "Enter password", isPassword = true)

        if (localError != null) {
            Text(localError!!, color = Color(0xFFFF5252), fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = true, onCheckedChange = {}, colors = CheckboxDefaults.colors(checkedColor = LimePrimary))
                Text("Remember me", color = TextGray, fontSize = 14.sp)
            }
            Text("Forgot Password ?", color = LimePrimary, fontSize = 14.sp, modifier = Modifier.clickable { })
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(color = LimePrimary)
        } else {
            CircuitButton(
                text = "Log In",
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        localError = "Please enter email and password"
                        return@CircuitButton
                    }
                    CircuitStateHolder.login(
                        emailVal = email,
                        passwordVal = password,
                        onSuccess = { onNavigate(Screen.HOME) },
                        onError = { localError = it }
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Or login with", color = TextGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, CardBg)
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = "Google", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue with Google", color = Color.White)
        }
    }
}
