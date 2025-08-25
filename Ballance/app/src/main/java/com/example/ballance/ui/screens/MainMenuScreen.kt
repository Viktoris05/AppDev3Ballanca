package com.example.ballance.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.navigation.NavController
import com.example.ballance.MusicPlayer
import com.example.ballance.R
import com.example.ballance.ui.navigation.Screen
import com.example.ballance.ui.theme.*

@Composable
fun MainMenuScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val backgroundColor = backgroundColor
    val accentColor = accentColor
    val textColor = Color.White
    var isPlaying by remember { mutableStateOf(MusicPlayer.isPlaying) }

    // responsive Größen/Abstände
    val iconSize = if (isLandscape) 40.dp else 48.dp
    val columnHorizontalPadding = if (isLandscape) 16.dp else 24.dp
    val logoSize = if (isLandscape) 120.dp else 160.dp
    val titleSize = if (isLandscape) 30.sp else 36.sp
    val titleBottomPadding = if (isLandscape) 6.dp else 10.dp
    val buttonWidthFraction = if (isLandscape) 0.8f else 0.75f
    val buttonVerticalPadding = if (isLandscape) 6.dp else 10.dp
    val buttonTextSize = if (isLandscape) 16.sp else 18.sp
    val betweenTopIcons = if (isLandscape) 6.dp else 8.dp

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // HAUPT-COLUMN (liegt unten, damit die Top-Right-Buttons darüber liegen können)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding() // verhindert Überlappung mit Statusleiste/Notch
                    .padding(horizontal = columnHorizontalPadding)
                    // Falls das Display extrem klein ist, verhindert Scrollen ein Abschneiden
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Ballance Logo",
                    modifier = Modifier.size(logoSize)
                )

                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = accentColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = titleSize,
                                shadow = Shadow(color = accentColor.copy(alpha = 0.5f), blurRadius = 4f)
                            )
                        ) { append("Ball") }
                        withStyle(
                            style = SpanStyle(
                                color = textColor,
                                fontWeight = FontWeight.Light,
                                fontSize = titleSize
                            )
                        ) { append("ance") }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = titleBottomPadding)
                )

                MenuButton(
                    text = "Spiel Starten",
                    color = accentColor,
                    widthFraction = buttonWidthFraction,
                    verticalPadding = buttonVerticalPadding,
                    textSize = buttonTextSize
                ) { navController.navigate(Screen.Game.route) }

                MenuButton(
                    text = "Level Auswählen",
                    color = accentColor,
                    widthFraction = buttonWidthFraction,
                    verticalPadding = buttonVerticalPadding,
                    textSize = buttonTextSize
                ) { navController.navigate(Screen.LevelSelect.route) }

                MenuButton(
                    text = "Editor",
                    color = accentColor,
                    widthFraction = buttonWidthFraction,
                    verticalPadding = buttonVerticalPadding,
                    textSize = buttonTextSize
                ) { navController.navigate(Screen.Editor.route) }
            }

            // TOP-RIGHT ICONS (NACH der Column deklariert => werden OBEN gerendert & erhalten Klicks)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = {
                        MusicPlayer.toggle(context)
                        isPlaying = MusicPlayer.isPlaying
                    },
                    modifier = Modifier.size(iconSize)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = "Musik umschalten",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(betweenTopIcons))

                IconButton(
                    onClick = { navController.navigate(Screen.Info.route) },
                    modifier = Modifier.size(iconSize)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    color: Color,
    widthFraction: Float = 0.75f,
    verticalPadding: Dp = 10.dp,
    textSize: TextUnit = 18.sp,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .padding(vertical = verticalPadding),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
    ) {
        Text(text = text, color = Color.White, fontSize = textSize)
    }
}
