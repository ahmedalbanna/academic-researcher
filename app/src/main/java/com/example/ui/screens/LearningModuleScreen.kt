package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LearningModule
import com.example.data.model.PracticalExercise
import com.example.data.model.QuizQuestion
import com.example.ui.AcademicViewModel
import com.example.ui.UiState
import com.example.util.LearningDataSupplier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningModuleScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val userScores by viewModel.userQuizScores.collectAsState()
    val aiQuizState by viewModel.aiQuizUiState.collectAsState()

    var paperTitle by remember { mutableStateOf("") }
    var paperContent by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الوحدات التعليمية والاختبارات التفاعلية", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_learning")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("المواضيع والتمارين") },
                    icon = { Icon(Icons.Default.School, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("مولّد الاختبارات من الأوراق") },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("سجل التقدم") },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    // Modules & Practical Exercises Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "وحدات تفاعلية مصممة لتعزيز التفكير النقدي ومهارات البحث العلمي مع شروح مرئية، تمارين عملية، واختبارات قياس.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        items(LearningDataSupplier.defaultModules, key = { it.id }) { module ->
                            InteractiveModuleCard(
                                module = module,
                                savedScore = userScores[module.id],
                                onRecordScore = { score ->
                                    viewModel.recordQuizScore(module.id, score)
                                }
                            )
                        }
                    }
                }

                1 -> {
                    // AI Research Paper Quiz Generator
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Psychology,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Text(
                                        text = "أدخل عنوان ومستخلص أي ورقة بحثية ليقوم الذكاء الاصطناعي بتوليد أسئلة تفاعلية وتقييم نقدي للمنهجية والنتائج.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = paperTitle,
                                onValueChange = { paperTitle = it },
                                label = { Text("عنوان الورقة البحثية *") },
                                placeholder = { Text("مثال: أثر تطبيق الذكاء الاصطناعي في التعليم الجامعي") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_paper_quiz_title"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = paperContent,
                                onValueChange = { paperContent = it },
                                label = { Text("مستخلص أو نص الورقة البحثية *") },
                                placeholder = { Text("الصق المستخلص أو فقرات النتائج والمنهجية هنا...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("input_paper_quiz_content"),
                                maxLines = 6
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    if (paperTitle.isNotBlank() && paperContent.isNotBlank()) {
                                        viewModel.generateQuizFromPaper(paperTitle, paperContent)
                                    }
                                },
                                enabled = paperTitle.isNotBlank() && paperContent.isNotBlank() && aiQuizState !is UiState.Loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_generate_paper_quiz")
                            ) {
                                if (aiQuizState is UiState.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("جاري استخراج وتحليل الأسئلة...")
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("توليد اختبار تفاعلي بالذكاء الاصطناعي")
                                }
                            }
                        }

                        item {
                            when (val state = aiQuizState) {
                                is UiState.Error -> {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            state.message,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(16.dp)
                                        )
                                    }
                                }
                                is UiState.Success -> {
                                    val questions = state.data
                                    InteractiveQuizComponent(
                                        quizTitle = "اختبار نقدي للورقة البحثية: $paperTitle",
                                        questions = questions,
                                        onQuizCompleted = { score ->
                                            viewModel.recordQuizScore("paper_ai_quiz", score)
                                        }
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                }

                2 -> {
                    // Progress & Score Dashboard
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(
                                        Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        modifier = Modifier.size(56.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "مؤشر التقدم والتفكير النقدي الأكاديمي",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )

                                    val totalModules = LearningDataSupplier.defaultModules.size
                                    val completedCount = userScores.size
                                    val avgScore = if (userScores.isNotEmpty()) userScores.values.average().toInt() else 0

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceAround
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("$completedCount / $totalModules", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                            Text("الوحدات المكتملة", style = MaterialTheme.typography.labelSmall)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("$avgScore%", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            Text("متوسط النتائج", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                text = "نتائج التقييم للوحدات المكتملة:",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        items(LearningDataSupplier.defaultModules) { module ->
                            val score = userScores[module.id]
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(module.title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text(module.subjectArea, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }

                                    if (score != null) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("درجة الاختبار: $score%") },
                                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary) }
                                        )
                                    } else {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("لم يكتمل بعد") }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveModuleCard(
    module: LearningModule,
    savedScore: Int?,
    onRecordScore: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = module.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${module.subjectArea} • ${module.durationMinutes} دقيقة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (savedScore != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "$savedScore%",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Explanatory Video Guide Banner
            VideoGuideBanner(
                title = module.videoTitle,
                description = module.videoDescription,
                duration = module.videoDuration
            )

            if (expanded) {
                HorizontalDivider()

                // Key Takeaways
                Text(
                    text = "أبرز المحاور والمفاهيم الأكاديمية:",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    module.keyTakeaways.forEach { takeaway ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                            Text(takeaway, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                HorizontalDivider()

                // Practical Exercise Section
                PracticalExerciseComponent(exercise = module.practicalExercise)

                HorizontalDivider()

                // Interactive Quiz Section
                InteractiveQuizComponent(
                    quizTitle = "اختبار النقد والتعزيز التفاعلي",
                    questions = module.quizQuestions,
                    onQuizCompleted = onRecordScore
                )
            }

            Button(
                onClick = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (expanded) "إغلاق الموديول التعليمي" else "بدء الدرس والتمرين التفاعلي")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.PlayArrow, contentDescription = null)
            }
        }
    }
}

@Composable
fun VideoGuideBanner(
    title: String,
    description: String,
    duration: String
) {
    var isPlaying by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "تشغيل الشرح المرئي",
                        tint = Color.White
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }

            if (isPlaying) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LinearProgressIndicator(
                        progress = { 0.45f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("جاري عرض الشرح المرئي والتطبيقي...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(duration, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun PracticalExerciseComponent(exercise: PracticalExercise) {
    var selectedOption by remember { mutableIntStateOf(-1) }
    var showResult by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                Text(
                    text = exercise.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Text(
                text = exercise.scenario,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            exercise.options.forEachIndexed { index, option ->
                val isSelected = selectedOption == index
                val isCorrect = index == exercise.correctOptionIndex

                OutlinedCard(
                    onClick = {
                        selectedOption = index
                        showResult = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = when {
                            showResult && isSelected && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                            showResult && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                selectedOption = index
                                showResult = true
                            }
                        )
                        Text(option, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                    }
                }
            }

            if (showResult) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedOption == exercise.correctOptionIndex) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = exercise.feedbackExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp),
                        color = if (selectedOption == exercise.correctOptionIndex) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
fun InteractiveQuizComponent(
    quizTitle: String,
    questions: List<QuizQuestion>,
    onQuizCompleted: (Int) -> Unit
) {
    var selectedAnswers by remember { mutableStateOf(mapOf<Int, Int>()) }
    var isSubmitted by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quizTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${questions.size} أسئلة",
                    style = MaterialTheme.typography.labelMedium
                )
            }

            questions.forEachIndexed { qIdx, question ->
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "${qIdx + 1}. ${question.questionText}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    question.options.forEachIndexed { optIdx, optionText ->
                        val isSelected = selectedAnswers[qIdx] == optIdx
                        val isCorrectOption = question.correctAnswerIndex == optIdx

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isSubmitted && isCorrectOption -> MaterialTheme.colorScheme.primaryContainer
                                isSubmitted && isSelected && !isCorrectOption -> MaterialTheme.colorScheme.errorContainer
                                isSelected -> MaterialTheme.colorScheme.surfaceVariant
                                else -> MaterialTheme.colorScheme.surface
                            },
                            border = TableRowBorder(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = !isSubmitted) {
                                    val current = selectedAnswers.toMutableMap()
                                    current[qIdx] = optIdx
                                    selectedAnswers = current
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        if (!isSubmitted) {
                                            val current = selectedAnswers.toMutableMap()
                                            current[qIdx] = optIdx
                                            selectedAnswers = current
                                        }
                                    }
                                )
                                Text(optionText, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                            }
                        }
                    }

                    if (isSubmitted) {
                        Text(
                            text = "تفسير: ${question.explanation}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            if (!isSubmitted) {
                Button(
                    onClick = {
                        isSubmitted = true
                        var correctCount = 0
                        questions.forEachIndexed { idx, q ->
                            if (selectedAnswers[idx] == q.correctAnswerIndex) {
                                correctCount++
                            }
                        }
                        val scorePercentage = if (questions.isNotEmpty()) (correctCount * 100) / questions.size else 0
                        onQuizCompleted(scorePercentage)
                    },
                    enabled = selectedAnswers.size == questions.size,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("إرسال الإجابات وتقييم النتيجة")
                }
            } else {
                var correctCount = 0
                questions.forEachIndexed { idx, q ->
                    if (selectedAnswers[idx] == q.correctAnswerIndex) correctCount++
                }
                val finalScore = if (questions.isNotEmpty()) (correctCount * 100) / questions.size else 0

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("النتيجة النهائية: $finalScore%", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Button(
                            onClick = {
                                isSubmitted = false
                                selectedAnswers = emptyMap()
                            }
                        ) {
                            Text("إعادة الاختبار")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableRowBorder() = CardDefaults.outlinedCardBorder()
