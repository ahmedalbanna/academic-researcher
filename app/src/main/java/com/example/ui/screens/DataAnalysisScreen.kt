package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DataAnalysisEntity
import com.example.ui.AcademicViewModel
import com.example.ui.UiState
import com.example.util.StatisticsResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAnalysisScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var rawInput by remember { mutableStateOf("12, 18, 25, 30, 22, 28, 35, 40, 15, 32") }

    var selectedTab by remember { mutableIntStateOf(0) }

    val dataState by viewModel.dataAnalysisUiState.collectAsState()
    val savedAnalyses by viewModel.allDataAnalyses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تحليل البيانات والمساعدة الإحصائية", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_data")) {
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
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("تحليل بيانات جديدة") },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("التحليلات المحفوظة (${savedAnalyses.size})") },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) }
                )
            }

            if (selectedTab == 0) {
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
                                Icon(Icons.Default.Equalizer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "يقوم النظام بإجراء الإحصاء الوصفي، إنشاء رسم بياني توضيحي، وصياغة التفسير العلمي لدلالات البيانات الإحصائية.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("عنوان أو وصف مجموعة البيانات *") },
                            placeholder = { Text("مثال: درجات اختبار عينة الطلاب في القياس القبلي والبعدي") },
                            modifier = Modifier.fillMaxWidth().testTag("input_data_title"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = rawInput,
                            onValueChange = { rawInput = it },
                            label = { Text("القيم العددية (مفصولة بفاصلة أو مسافة) *") },
                            placeholder = { Text("10, 20, 15, 30, 25, 40...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("input_data_values"),
                            maxLines = 4
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                val t = title.ifBlank { "تحليل إحصائي" }
                                viewModel.analyzeDataset(t, rawInput)
                            },
                            enabled = rawInput.isNotBlank() && dataState !is UiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_run_data_analysis")
                        ) {
                            if (dataState is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري معالجة البيانات الإحصائية...")
                            } else {
                                Icon(Icons.Default.Calculate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("حساب المؤشرات ورسم البياني")
                            }
                        }
                    }

                    item {
                        when (val state = dataState) {
                            is UiState.Error -> {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(state.message, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                                }
                            }
                            is UiState.Success -> {
                                val (stats, interpretation) = state.data
                                var isSaved by remember { mutableStateOf(false) }

                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // Statistical Summary Indicators Grid
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = CardDefaults.outlinedCardBorder()
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
                                                Text(
                                                    text = "المؤشرات الإحصائية الوصفية",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                )
                                                Button(
                                                    onClick = {
                                                        val t = title.ifBlank { "تحليل إحصائي" }
                                                        viewModel.saveDataAnalysis(t, rawInput, stats, interpretation)
                                                        isSaved = true
                                                    },
                                                    enabled = !isSaved
                                                ) {
                                                    Icon(if (isSaved) Icons.Default.Check else Icons.Default.Bookmark, contentDescription = null)
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Text(if (isSaved) "تم الحفظ" else "حفظ التحليل")
                                                }
                                            }

                                            HorizontalDivider()

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceAround
                                            ) {
                                                StatMetricItem("حجم العينة (N)", stats.count.toString())
                                                StatMetricItem("الوسط الحسابي", String.format("%.2f", stats.mean))
                                                StatMetricItem("الوسيط", String.format("%.2f", stats.median))
                                            }

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceAround
                                            ) {
                                                StatMetricItem("الانحراف المعياري", String.format("%.2f", stats.stdDev))
                                                StatMetricItem("أقل قيمة", stats.min.toString())
                                                StatMetricItem("أعلى قيمة", stats.max.toString())
                                            }
                                        }
                                    }

                                    // Custom Compose Canvas Chart
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "الرسم البياني للتوزيع التكراري والقيم",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )

                                            AcademicBarChart(
                                                numbers = stats.numbers,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                            )
                                        }
                                    }

                                    // AI Interpretation Output
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "التفسير الأكاديمي الموصى به للنتائج",
                                                style = MaterialTheme.typography.titleSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            HorizontalDivider()
                                            Text(
                                                text = interpretation,
                                                style = MaterialTheme.typography.bodyMedium,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                if (savedAnalyses.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("لا توجد تحليلات إحصائية محفوظة.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedAnalyses, key = { it.id }) { item ->
                            SavedAnalysisCard(analysis = item, onDelete = { viewModel.deleteDataAnalysis(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AcademicBarChart(
    numbers: List<Double>,
    modifier: Modifier = Modifier
) {
    val barColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (numbers.isEmpty()) return@Canvas

            val maxVal = (numbers.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
            val canvasWidth = size.width
            val canvasHeight = size.height

            val padding = 30f
            val chartWidth = canvasWidth - padding * 2
            val chartHeight = canvasHeight - padding * 2

            // Draw axis lines
            drawLine(
                color = axisColor,
                start = Offset(padding, padding),
                end = Offset(padding, canvasHeight - padding),
                strokeWidth = 2f
            )
            drawLine(
                color = axisColor,
                start = Offset(padding, canvasHeight - padding),
                end = Offset(canvasWidth - padding, canvasHeight - padding),
                strokeWidth = 2f
            )

            val barCount = numbers.size
            val barWidth = (chartWidth / barCount) * 0.6f
            val spaceWidth = (chartWidth / barCount) * 0.4f

            numbers.forEachIndexed { idx, valNum ->
                val barHeight = (valNum / maxVal).toFloat() * chartHeight
                val left = padding + (idx * (barWidth + spaceWidth)) + spaceWidth / 2
                val top = (canvasHeight - padding) - barHeight

                drawRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight)
                )
            }
        }
    }
}

@Composable
fun SavedAnalysisCard(
    analysis: DataAnalysisEntity,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(analysis.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }

            Text("العينة N = ${analysis.sampleCount} | الوسط = ${String.format("%.2f", analysis.meanValue)} | الانحراف = ${String.format("%.2f", analysis.stdDevValue)}", style = MaterialTheme.typography.bodySmall)

            if (expanded) {
                HorizontalDivider()
                Text("التفسير:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Text(analysis.statisticalSummaryText, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
            }

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (expanded) "طي التفاصيل" else "عرض تفسير النتائج")
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
        }
    }
}
