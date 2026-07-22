package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CitationEntity
import com.example.ui.AcademicViewModel
import com.example.ui.UiState
import com.example.util.AcademicUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitationManagerScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Formatter, 1: AI Reference Finder, 2: Bibliography List

    val context = LocalContext.current
    val savedCitations by viewModel.allCitations.collectAsState()

    // Formatter State
    var sourceType by remember { mutableStateOf("مجلة علمية") }
    var authors by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var publisherOrJournal by remember { mutableStateOf("") }
    var doiOrUrl by remember { mutableStateOf("") }

    // Finder State
    var topic by remember { mutableStateOf("") }
    var field by remember { mutableStateOf("") }
    val suggestedRefsState by viewModel.suggestedReferencesUiState.collectAsState()

    val sourceTypes = listOf("مجلة علمية", "كتاب محكم", "موقع إلكتروني", "أطروحة ماجستير/دكتوراه")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تنظيم المراجع والاقتباسات", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_citation")) {
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
                    text = { Text("تنسيق مرجع") },
                    icon = { Icon(Icons.Default.FormatQuote, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("اقتراح مراجع") },
                    icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("المراجع المحفوظة (${savedCitations.size})") },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Formatter Tab
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
                                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = "تنشئ هذه الأداة الاقتباس التوثيقي الدقيق للأنماط القياسية APA 7, IEEE, MLA 9, Harvard للاستخدام المباشر في قائمة المراجع.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        item {
                            Text("نوع المصدر:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                sourceTypes.take(3).forEach { option ->
                                    FilterChip(
                                        selected = sourceType == option,
                                        onClick = { sourceType = option },
                                        label = { Text(option, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = authors,
                                onValueChange = { authors = it },
                                label = { Text("المؤلفون *") },
                                placeholder = { Text("مثال: د. عبد الله بن سعيد وآخرون") },
                                modifier = Modifier.fillMaxWidth().testTag("input_citation_authors"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = title,
                                onValueChange = { title = it },
                                label = { Text("عنوان المقال / الكتاب / الدراسة *") },
                                placeholder = { Text("مثال: أصول البحث العلمي والمناهج الحديثة") },
                                modifier = Modifier.fillMaxWidth().testTag("input_citation_title"),
                                singleLine = true
                            )
                        }

                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                OutlinedTextField(
                                    value = year,
                                    onValueChange = { year = it },
                                    label = { Text("السنة") },
                                    placeholder = { Text("2024") },
                                    modifier = Modifier.weight(1f).testTag("input_citation_year"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = publisherOrJournal,
                                    onValueChange = { publisherOrJournal = it },
                                    label = { Text("اسم المجلة / دار النشر") },
                                    placeholder = { Text("مجلة العلوم الإنسانية") },
                                    modifier = Modifier.weight(2f).testTag("input_citation_publisher"),
                                    singleLine = true
                                )
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = doiOrUrl,
                                onValueChange = { doiOrUrl = it },
                                label = { Text("معرف DOI أو الرابط") },
                                placeholder = { Text("https://doi.org/10.1000/xyz") },
                                modifier = Modifier.fillMaxWidth().testTag("input_citation_doi"),
                                singleLine = true
                            )
                        }

                        if (authors.isNotBlank() && title.isNotBlank()) {
                            val apaFormat = AcademicUtils.formatApa(authors, title, year, publisherOrJournal, doiOrUrl)
                            val ieeeFormat = AcademicUtils.formatIeee(authors, title, year, publisherOrJournal, doiOrUrl)
                            val mlaFormat = AcademicUtils.formatMla(authors, title, year, publisherOrJournal, doiOrUrl)

                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text("التوثيق المولد بالتنسيقات القياسية:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                    CitationResultCard(label = "APA (7th Edition)", formattedText = apaFormat, context = context)
                                    CitationResultCard(label = "IEEE Style", formattedText = ieeeFormat, context = context)
                                    CitationResultCard(label = "MLA (9th Edition)", formattedText = mlaFormat, context = context)

                                    Button(
                                        onClick = {
                                            viewModel.saveCitation(sourceType, authors, title, year, publisherOrJournal, doiOrUrl)
                                            Toast.makeText(context, "تم حفظ المرجع بنجاح!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Icon(Icons.Default.Bookmark, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("حفظ المرجع إلى القائمة")
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // AI Reference Finder Tab
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
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    Text(
                                        text = "ادخل موضوع بحثك ليقوم المساعد بتقديم مقترحات مراجع علمية حديثة وموثوقة في تخصصك مع الكلمات المفتاحية.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = topic,
                                onValueChange = { topic = it },
                                label = { Text("موضوع البحث أو عنوانه *") },
                                placeholder = { Text("مثال: تطبيقات التعلم العميق في التشخيص الطبي") },
                                modifier = Modifier.fillMaxWidth().testTag("input_citation_find_topic"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = field,
                                onValueChange = { field = it },
                                label = { Text("مجال التخصص العلمى *") },
                                placeholder = { Text("مثال: الذكاء الاصطناعي، الطب الرقمي") },
                                modifier = Modifier.fillMaxWidth().testTag("input_citation_find_field"),
                                singleLine = true
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    if (topic.isNotBlank() && field.isNotBlank()) {
                                        viewModel.suggestAcademicReferences(topic, field)
                                    }
                                },
                                enabled = topic.isNotBlank() && field.isNotBlank() && suggestedRefsState !is UiState.Loading,
                                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("btn_find_references")
                            ) {
                                if (suggestedRefsState is UiState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("جاري البحث واقتراح المراجع...")
                                } else {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("اقتراح مراجع حديثة في التخصص")
                                }
                            }
                        }

                        item {
                            when (val state = suggestedRefsState) {
                                is UiState.Error -> {
                                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                                        Text(state.message, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                                    }
                                }
                                is UiState.Success -> {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        border = CardDefaults.outlinedCardBorder()
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Text("المراجع المقترحة في مجال البحث", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            HorizontalDivider()
                                            Text(state.data, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                                        }
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }
                2 -> {
                    // Saved Citations Tab
                    if (savedCitations.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("لا توجد مراجع محفوظة في قائمة التوثيق.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Button(
                                    onClick = {
                                        val allApaText = savedCitations.joinToString("\n\n") { it.formattedApa }
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("All APA Citations", allApaText)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "تم نسخ قائمة المراجع كاملة (APA)!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("نسخ جميع المراجع لتضمينها في البحث (APA)")
                                }
                            }

                            items(savedCitations, key = { it.id }) { citation ->
                                SavedCitationCard(citation = citation, onDelete = { viewModel.deleteCitation(citation) }, context = context)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CitationResultCard(label: String, formattedText: String, context: Context) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText(label, formattedText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ توثيق $label", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "نسخ", modifier = Modifier.size(16.dp))
                }
            }
            Text(formattedText, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
        }
    }
}

@Composable
fun SavedCitationCard(
    citation: CitationEntity,
    onDelete: () -> Unit,
    context: Context
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(onClick = {}, label = { Text(citation.sourceType) })
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }

            Text("APA:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text(citation.formattedApa, style = MaterialTheme.typography.bodyMedium)

            TextButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Citation", citation.formattedApa)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "تم النسخ!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("نسخ التوثيق")
            }
        }
    }
}
