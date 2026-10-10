package com.basair.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val Emerald = Color(0xFF0B3B2C)
private val DeepGreen = Color(0xFF041C14)
private val Gold = Color(0xFFD4AF37)
private val Cream = Color(0xFFF7F1DF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Gold,
                    background = DeepGreen,
                    surface = Emerald,
                    onPrimary = DeepGreen,
                    onBackground = Color.White,
                    onSurface = Color.White
                )
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    BasairApp()
                }
            }
        }
    }
}

private enum class Page { HOME, GAME, LEADERBOARD }

@Composable
fun BasairApp() {
    var page by remember { mutableStateOf(Page.HOME) }
    var score by remember { mutableIntStateOf(0) }
    var questionIndex by remember { mutableIntStateOf(0) }
    var answerMessage by remember { mutableStateOf("") }

    when (page) {
        Page.HOME -> LuxuryHomeScreen(
            onStartGameClicked = {
                questionIndex = 0
                answerMessage = ""
                page = Page.GAME
            },
            onLeaderboardClicked = { page = Page.LEADERBOARD }
        )
        Page.GAME -> GameScreen(
            questionIndex = questionIndex,
            score = score,
            answerMessage = answerMessage,
            onAnswer = { correct ->
                if (correct) score += 10
                answerMessage = if (correct) "إجابة صحيحة! +10 نقاط" else "ليست الإجابة الصحيحة، حاول مجددًا."
            },
            onNext = {
                questionIndex = (questionIndex + 1) % questions.size
                answerMessage = ""
            },
            onHome = { page = Page.HOME }
        )
        Page.LEADERBOARD -> LeaderboardScreen(
            score = score,
            onBack = { page = Page.HOME }
        )
    }
}

@Composable
fun LuxuryHomeScreen(
    onStartGameClicked: () -> Unit,
    onLeaderboardClicked: () -> Unit
) {
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Emerald, DeepGreen)
    )
    Box(
        modifier = Modifier.fillMaxSize().background(backgroundBrush).padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.size(90.dp).clip(RoundedCornerShape(24.dp))
                    .background(Gold.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("✨", fontSize = 40.sp)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "بَصَائِر",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Gold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
            Text(
                "رحلة معرفية تفاعلية راقية",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )
            Button(
                onClick = onStartGameClicked,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Text("ابدأ الرحلة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DeepGreen)
            }
            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = onLeaderboardClicked,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, Gold),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Gold)
            ) {
                Text("لوحة الصدارة الملكية", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

private data class Question(
    val text: String,
    val answers: List<String>,
    val correctIndex: Int
)

private val questions = listOf(
    Question("كم عدد أركان الإسلام؟", listOf("أربعة", "خمسة", "ستة", "سبعة"), 1),
    Question("ما السورة التي تُسمّى قلب القرآن؟", listOf("البقرة", "يس", "الرحمن", "الإخلاص"), 1),
    Question("ما أول شهر في السنة الهجرية؟", listOf("رمضان", "ربيع الأول", "محرم", "شعبان"), 2),
    Question("كم عدد الصلوات المفروضة في اليوم والليلة؟", listOf("ثلاث", "أربع", "خمس", "ست"), 2)
)

@Composable
private fun GameScreen(
    questionIndex: Int,
    score: Int,
    answerMessage: String,
    onAnswer: (Boolean) -> Unit,
    onNext: () -> Unit,
    onHome: () -> Unit
) {
    val question = questions[questionIndex]
    ScreenBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = onHome) { Text("الرئيسية", color = Gold) }
                Text("النقاط: $score", color = Gold, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(30.dp))
            Text("السؤال ${questionIndex + 1} من ${questions.size}", color = Cream)
            Spacer(Modifier.height(16.dp))
            Text(
                question.text,
                color = Gold,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))
            question.answers.forEachIndexed { index, answer ->
                Button(
                    onClick = { onAnswer(index == question.correctIndex) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp).heightIn(min = 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald)
                ) {
                    Text(answer, color = Color.White, fontSize = 17.sp)
                }
            }
            if (answerMessage.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                Text(answerMessage, color = Gold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(12.dp))
                Button(onClick = onNext, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                    Text("السؤال التالي", color = DeepGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardScreen(score: Int, onBack: () -> Unit) {
    ScreenBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🏆", fontSize = 54.sp)
            Spacer(Modifier.height(16.dp))
            Text("لوحة الصدارة الملكية", color = Gold, fontSize = 27.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            Text("نتيجتك الحالية", color = Color.White.copy(alpha = 0.8f), fontSize = 16.sp)
            Text("$score نقطة", color = Gold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("هذه نسخة أولية؛ النتائج محفوظة خلال الجلسة فقط.", color = Cream, textAlign = TextAlign.Center)
            Spacer(Modifier.height(30.dp))
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                Text("العودة للرئيسية", color = DeepGreen, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ScreenBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Emerald, DeepGreen))
        ),
        content = content
    )
}
