package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LiteratureSummaryEntity
import com.example.ui.AcademicViewModel
import com.example.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiteratureSummaryScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    var paperTitle by remember { mutableStateOf("") }
    var authorsAndYear by remember { mutableStateOf("") }
    var paperText by remember { mutableStateOf("") }

    var selectedTab by remember { mutableIntStateOf(0) }

    val summaryState by viewModel.summaryUiState.collectAsState()
    val savedSummaries by viewModel.allSummaries.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تلخيص الأبحاث والتحليل النقدي", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_summary")) {
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
                    text = { Text("تحليل دراسة جديدة") },
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("الملخصات المحفوظة (${savedSummaries.size})") },
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
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Text(
                                    text = "يقوم النظام باستخلاص الأفكار المحورية، نقد المنهجية، وتحديد الفجوة البحثية لبناء التفكير المستقل والناقد لدى الطالب والباحث.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = paperTitle,
                            onValueChange = { paperTitle = it },
                            label = { Text("عنوان الدراسة / المقال العلمي *") },
                            placeholder = { Text("مثال: دراسة تحليليّة لمؤشرات جودة التعليم الجامعي") },
                            modifier = Modifier.fillMaxWidth().testTag("input_summary_title"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = authorsAndYear,
                            onValueChange = { authorsAndYear = it },
                            label = { Text("الباحثون وسنة النشر") },
                            placeholder = { Text("مثال: د. أحمد وسعيد (2024)") },
                            modifier = Modifier.fillMaxWidth().testTag("input_summary_authors"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = paperText,
                            onValueChange = { paperText = it },
                            label = { Text("نص الورقة العلمية / المستخلص الكامل *") },
                            placeholder = { Text("التق بضع فقرات أو مستخلص الدراسة هنا للتحليل والتلخيص...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .testTag("input_summary_text"),
                            maxLines = 8
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                if (paperTitle.isNotBlank() && paperText.isNotBlank()) {
                                    viewModel.summarizeAndAnalyzeLiterature(
                                        paperTitle = paperTitle,
                                        authorsAndYear = authorsAndYear,
                                        paperText = paperText
                                    )
                                }
                            },
                            enabled = paperTitle.isNotBlank() && paperText.isNotBlank() && summaryState !is UiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_analyze_summary")
                        ) {
                            if (summaryState is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري التحليل النقدي والتلخيص...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("تلخيص وتحليل نقدى دراسة")
                            }
                        }
                    }

                    item {
                        when (val state = summaryState) {
                            is UiState.Error -> {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(state.message, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                                }
                            }
                            is UiState.Success -> {
                                val summaryEntity = state.data
                                var isSaved by remember { mutableStateOf(false) }

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
                                                text = "نتائج التحليل النقدي للدراسة",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.secondary
                                                )
                                            )
                                            Button(
                                                onClick = {
                                                    viewModel.saveSummary(summaryEntity)
                                                    isSaved = true
                                                },
                                                enabled = !isSaved
                                            ) {
                                                Icon(if (isSaved) Icons.Default.Check else Icons.Default.Bookmark, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isSaved) "تم الحفظ" else "حفظ التلخيص")
                                            }
                                        }

                                        HorizontalDivider()

                                        Text(
                                            text = summaryEntity.summaryFindings,
                                            style = MaterialTheme.typography.bodyMedium,
                                            lineHeight = 22.sp
                                        )
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            } else {
                if (savedSummaries.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("لا توجد دراسات أو ملخصات محفوظة.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedSummaries, key = { it.id }) { item ->
                            SavedSummaryCard(summary = item, onDelete = { viewModel.deleteSummary(item) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedSummaryCard(
    summary: LiteratureSummaryEntity,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(summary.paperTitle, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }

            if (summary.authorsAndYear.isNotBlank()) {
                Text("المؤلفون: ${summary.authorsAndYear}", style = MaterialTheme.typography.bodySmall)
            }

            if (expanded) {
                HorizontalDivider()
                Text(summary.summaryFindings, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
            }

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (expanded) "طي التلخيص" else "عرض التلخيص الكامل")
                Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
            }
        }
    }
}
