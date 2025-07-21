package com.example.ballance.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun InfoScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF121212)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Über Ballance",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Ballance ist ein Geschicklichkeitsspiel, bei dem du eine Kugel durch ein Labyrinth steuerst, indem du dein Smartphone kippst.\n\n" +
                        "Ziel ist es, das Loch am Ende jedes Levels zu erreichen .\n\n" +
                        "Die App wurde im Jahr 2025 im Rahmen der Lehrveranstaltung \"App Development\" entwickelt.",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Entwickelt von:",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "• Dizdarević Admir\n• Ismailov Viktor\n• Pirker Martin",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Zurück", fontSize = 16.sp)
            }
        }
    }
}
