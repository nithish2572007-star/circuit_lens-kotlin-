package com.example.circuitlens.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



// Color Palette Definition
val DarkBg = Color(0xFF121212)
val CardBg = Color(0xFF1E1E1E)
val LimePrimary = Color(0xFFC0EB4E)
val LimeGradientEnd = Color(0xFF8BB71D)
val BorderGreen = Color(0xFF5A7A21)
val TextGray = Color(0xFF9E9E9E)

enum class Screen { LOGIN, SIGNUP, HOME, SCAN, CHAT, HISTORY, PROFILE }

@Composable
fun CircuitLensApp() {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = DarkBg
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    when (currentScreen) {
                        Screen.LOGIN -> LoginScreen(onNavigate = { currentScreen = it })
                        Screen.SIGNUP -> SignUpScreen(onNavigate = { currentScreen = it })
                        Screen.HOME -> HomeScreen(onProfileClick = { currentScreen = Screen.PROFILE })
                        Screen.SCAN -> ScanScreen()
                        Screen.CHAT -> ChatScreen()
                        Screen.HISTORY -> HistoryScreen()
                        Screen.PROFILE -> ProfileScreen(onBack = { currentScreen = Screen.HOME })
                    }
                }

                // Show bottom bar only on core operational screens
                if (currentScreen in listOf(Screen.HOME, Screen.SCAN, Screen.CHAT, Screen.HISTORY)) {
                    CircuitLensBottomBar(
                        currentScreen = currentScreen,
                        onTabSelected = { currentScreen = it }
                    )
                }
            }
        }
    }
}

// --- Reusable UI Components ---

@Composable
fun CircuitHeader(titleSuffix: String = "Lens", onProfileClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Text("Circuit", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(titleSuffix, color = LimePrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        onProfileClick?.let {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(BorderGreen)
                    .clickable { it() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.White)
            }
        }
    }
}

@Composable
fun CircuitInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isPassword: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray) },
            singleLine = true,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle Password",
                            tint = TextGray
                        ) // Fixed: This was mistakenly a '}' in the previous code
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LimePrimary,
                unfocusedBorderColor = BorderGreen,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )
    }
}

@Composable
fun CircuitButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.horizontalGradient(listOf(LimePrimary, LimeGradientEnd))),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// --- Authentication Screens ---

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

@Composable
fun LoginScreen(onNavigate: (Screen) -> Unit) {
    var email by remember { mutableStateOf("abc@gmail.com") }
    var password by remember { mutableStateOf("*********") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Get Started", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Create an account or log in\nto explore about our app", color = TextGray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))

        AuthToggle(isLogin = true, onNavigate = onNavigate)

        CircuitInputField(value = email, onValueChange = { email = it }, label = "Email", placeholder = "Enter email")
        CircuitInputField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "Enter password", isPassword = true)

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
        CircuitButton(text = "Log In", onClick = { onNavigate(Screen.HOME) })

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

@Composable
fun SignUpScreen(onNavigate: (Screen) -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("abc@gmail.com") }
    var password by remember { mutableStateOf("*********") }

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

// --- Dashboards & Internal Screens ---

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

@Composable
fun OverviewCard(modifier: Modifier = Modifier, count: String, label: String, icon: ImageVector) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = label, tint = LimePrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(count, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextGray, fontSize = 10.sp, lineHeight = 12.sp)
        }
    }
}

@Composable
fun ActivityItem(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, BorderGreen), RoundedCornerShape(16.dp))
            .background(CardBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(40.dp).background(BorderGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextGray, fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = "View", tint = Color.White)
    }
}

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

@Composable
fun ChatScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OverviewCard(modifier = Modifier.weight(1f), count = "2", label = "Components Detected", icon = Icons.Default.Memory)
            OverviewCard(modifier = Modifier.weight(1f), count = "3", label = "Connections Detected", icon = Icons.Default.DeviceHub)
            OverviewCard(modifier = Modifier.weight(1f), count = "1", label = "Error Detected", icon = Icons.Default.BugReport)
        }

        Spacer(modifier = Modifier.height(16.dp))
        CollapsibleSection(title = "DETECTED NETLIST", placeholder = "(Netlist here)")
        Spacer(modifier = Modifier.height(12.dp))
        CollapsibleSection(title = "ISSUES DETECTED (1)", placeholder = "(Issues here)")

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Query Box
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Ask CircuitLens...", color = TextGray) },
            leadingIcon = { Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = Color.White) },
            trailingIcon = { Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LimePrimary,
                unfocusedBorderColor = BorderGreen,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg
            )
        )
    }
}

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

@Composable
fun HistoryScreen() {
    val itemsList = listOf("CHAT 1", "CHAT 2", "CHAT 3", "CHAT 4", "CHAT 5")
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        CircuitHeader()
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

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    var name by remember { mutableStateOf("A B C") }
    var email by remember { mutableStateOf("abc@gmail.com") }
    var username by remember { mutableStateOf("@abc123") }
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
            Box(modifier = Modifier.size(120.dp).background(BorderGreen, CircleShape), contentAlignment = Alignment.Center) {
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
        CircuitButton(text = "Save", onClick = onBack)
    }
}

// --- Navigation ---

@Composable
fun CircuitLensBottomBar(currentScreen: Screen, onTabSelected: (Screen) -> Unit) {
    Surface(
        color = CardBg,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                screen = Screen.HOME,
                icon = Icons.Default.Home,
                label = "Home",
                isActive = currentScreen == Screen.HOME,
                onClick = onTabSelected
            )
            BottomNavItem(
                screen = Screen.SCAN,
                icon = Icons.Default.QrCodeScanner,
                label = "Scan",
                isActive = currentScreen == Screen.SCAN,
                onClick = onTabSelected
            )
            BottomNavItem(
                screen = Screen.CHAT,
                icon = Icons.Default.ChatBubbleOutline,
                label = "Chat",
                isActive = currentScreen == Screen.CHAT,
                onClick = onTabSelected
            )
            BottomNavItem(
                screen = Screen.HISTORY,
                icon = Icons.Default.AccessTime,
                label = "History",
                isActive = currentScreen == Screen.HISTORY,
                onClick = onTabSelected
            )
        }
    }
}

@Composable
fun BottomNavItem(
    screen: Screen,
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: (Screen) -> Unit
) {
    val activeColor by animateColorAsState(if (isActive) Color.Black else TextGray, label = "")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick(screen) }
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 54.dp, height = 36.dp)
                .background(
                    color = if (isActive) LimePrimary else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = activeColor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isActive) LimePrimary else TextGray,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}