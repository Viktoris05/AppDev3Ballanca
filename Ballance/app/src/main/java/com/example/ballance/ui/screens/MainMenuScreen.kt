package com.example.ballance.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ballance.R
import com.example.ballance.ui.navigation.Screen

@Composable
fun MainMenuScreen(navController: NavController) {
    val backgroundColor = Color(0xFF121212)
    val accentColor = Color(0xFFC89B5C)
    val textColor = Color.White

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Ballance Logo",
                modifier = Modifier
                    .size(160.dp)
                    .padding(bottom = 32.dp)
            )

            // Stylischer Titel
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp,
                            shadow = Shadow(color = accentColor.copy(alpha = 0.5f), blurRadius = 4f)
                        )
                    ) {
                        append("Ball")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = textColor,
                            fontWeight = FontWeight.Light,
                            fontSize = 36.sp
                        )
                    ) {
                        append("ance")
                    }
                },
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            MenuButton("Spiel Starten", accentColor) {
                navController.navigate(Screen.Game.route)
            }

            MenuButton("Level Auswählen", accentColor) {
                navController.navigate(Screen.LevelSelect.route)
            }

            MenuButton("Editor", accentColor) {
                navController.navigate(Screen.Editor.route)
            }
        }
    }
}

@Composable
fun MenuButton(text: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = 18.sp)
    }
}
