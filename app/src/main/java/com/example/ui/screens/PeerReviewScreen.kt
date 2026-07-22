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
import com.example.data.local.PeerFeedbackEntity
import com.example.data.local.PeerPaperEntity
import com.example.data.local.PeerResponseEntity
import com.example.ui.AcademicViewModel
import com.example.ui.UiState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeerReviewScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val allPapers by viewModel.allPeerPapers.collectAsState()
    val allFeedbacks by viewModel.allPeerFeedbacks.collectAsState()
    val allResponses by viewModel.allPeerResponses.collectAsState()
    val aiReviewState by viewModel.aiPeerReviewUiState.collectAsState()

    var selectedPaperId by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFieldFilter by remember { mutableStateOf("الكل") }

    // Form states for new paper submission
    var newPaperTitle by remember { mutableStateOf("") }
    var newPaperField by remember { mutableStateOf("تكنولوجيا التعليم") }
    var newPaperAbstract by remember { mutableStateOf("") }
    var newPaperDraft by remember { mutableStateOf("") }
    var newPaperAuthorAlias by remember { mutableStateOf("باحث مجهول #${(1000..9999).random()}") }
    var showPaperSubmittedSnackbar by remember { mutableStateOf(false) }

    // Form state for new feedback modal/section
    var showAddFeedbackDialog by remember { mutableStateOf(false) }

    val fieldsList = listOf("الكل", "تكنولوجيا التعليم", "العلوم المالية والبيانات", "مناهج وتدريس", "ذكاء اصطناعي", "إدارة الأعمال")

    val selectedPaper = allPapers.find { it.id == selectedPaperId } ?: allPapers.firstOrNull()

    val filteredPapers = remember(allPapers, searchQuery, selectedFieldFilter) {
        allPapers.filter { paper ->
            val matchesQuery = searchQuery.isBlank() || paper.title.contains(searchQuery, ignoreCase = true) || paper.abstractText.contains(searchQuery, ignoreCase = true)
            val matchesField = selectedFieldFilter == "الكل" || paper.academicField == selectedFieldFilter
            matchesQuery && matchesField
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("المراجعة الأكاديمية التبادلية (Peer Review)", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_peer_review")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                ExtendedFloatingActionButton(
                    onClick = { selectedTabIndex = 2 },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("تقديم مسودة جديدة") },
                    modifier = Modifier.testTag("fab_add_peer_paper")
                )
            }
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
                    text = { Text("تصفح المسودات") },
                    icon = { Icon(Icons.Default.FindInPage, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("تفاصيل المراجعة") },
                    icon = { Icon(Icons.Default.RateReview, contentDescription = null) }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("تقديم مسودة") },
                    icon = { Icon(Icons.Default.PostAdd, contentDescription = null) }
                )
            }

            when (selectedTabIndex) {
                0 -> {
                    // Browse Papers Tab
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "بيئة مراجعة مجهولة ومتبادلة تتيح للطلاب والباحثين تقديم مسوداتهم واستقبال ملاحظات علمية نقدية بناءة وفق معايير الوضوح والمنهجية والتسلسل المنطقي.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Search and Filter Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("بحث في عنوان أو مستخلص المسودة...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_search_peer_papers"),
                            singleLine = true
                        )

                        ScrollableTabRow(
                            selectedTabIndex = fieldsList.indexOf(selectedFieldFilter).coerceAtLeast(0),
                            edgePadding = 0.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            fieldsList.forEach { field ->
                                Tab(
                                    selected = selectedFieldFilter == field,
                                    onClick = { selectedFieldFilter = field },
                                    text = { Text(field, style = MaterialTheme.typography.bodySmall) }
                                )
                            }
                        }

                        if (filteredPapers.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "لا توجد مسودات بحثية مطابقة للبحث.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredPapers, key = { it.id }) { paper ->
                                    val paperFeedbacks = allFeedbacks.filter { it.paperId == paper.id }
                                    PeerPaperCard(
                                        paper = paper,
                                        feedbackCount = paperFeedbacks.size,
                                        avgClarity = if (paperFeedbacks.isNotEmpty()) paperFeedbacks.map { it.clarityRating }.average() else 0.0,
                                        avgMethodology = if (paperFeedbacks.isNotEmpty()) paperFeedbacks.map { it.methodologyRating }.average() else 0.0,
                                        avgArgumentation = if (paperFeedbacks.isNotEmpty()) paperFeedbacks.map { it.argumentationRating }.average() else 0.0,
                                        onSelectPaper = {
                                            selectedPaperId = paper.id
                                            selectedTabIndex = 1
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Paper Detail & Review Feedback Stream
                    if (selectedPaper == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("اختر مسودة بحثية من التبويب الأول لعرض التقييمات والملاحظات.")
                        }
                    } else {
                        val paperFeedbacks = allFeedbacks.filter { it.paperId == selectedPaper.id }
                        val paperResponses = allResponses.filter { it.paperId == selectedPaper.id }

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Paper Header Detail Card
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AssistChip(
                                                onClick = {},
                                                label = { Text(selectedPaper.academicField) },
                                                leadingIcon = { Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                            )
                                            Text(
                                                text = selectedPaper.authorAlias,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            text = selectedPaper.title,
                                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                        )

                                        HorizontalDivider()

                                        Text(
                                            text = "المستخلص الأكاديمي:",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = selectedPaper.abstractText,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        var showDraftContent by remember { mutableStateOf(false) }
                                        if (showDraftContent) {
                                            HorizontalDivider()
                                            Text(
                                                text = "نص المسودة الكاملة:",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                text = selectedPaper.fullDraftText,
                                                style = MaterialTheme.typography.bodySmall,
                                                lineHeight = 20.sp
                                            )
                                        }

                                        TextButton(onClick = { showDraftContent = !showDraftContent }) {
                                            Text(if (showDraftContent) "إخفاء نص المسودة الكاملة" else "قراءة نص المسودة الكاملة")
                                            Icon(if (showDraftContent) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null)
                                        }
                                    }
                                }
                            }

                            // Average Criterion Radar / Summary Cards
                            item {
                                Text(
                                    text = "ملخص تقييم الأقران وحصيلة الملاحظات:",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val avgClarity = if (paperFeedbacks.isNotEmpty()) paperFeedbacks.map { it.clarityRating }.average() else 0.0
                                    val avgMethodology = if (paperFeedbacks.isNotEmpty()) paperFeedbacks.map { it.methodologyRating }.average() else 0.0
                                    val avgArgumentation = if (paperFeedbacks.isNotEmpty()) paperFeedbacks.map { it.argumentationRating }.average() else 0.0

                                    CriterionSummaryCard(
                                        title = "الوضوح والأسلوب",
                                        rating = avgClarity,
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.ShortText
                                    )
                                    CriterionSummaryCard(
                                        title = "المنهجية العلمية",
                                        rating = avgMethodology,
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.Science
                                    )
                                    CriterionSummaryCard(
                                        title = "الحجج والمنطق",
                                        rating = avgArgumentation,
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Default.Psychology
                                    )
                                }
                            }

                            // Actions Bar: Add Feedback or AI Peer Review
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showAddFeedbackDialog = true },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_add_peer_feedback")
                                    ) {
                                        Icon(Icons.Default.RateReview, contentDescription = null)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("تقديم تقييم ونقد")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            viewModel.generateAiPeerReview(
                                                paperId = selectedPaper.id,
                                                paperTitle = selectedPaper.title,
                                                abstractText = selectedPaper.abstractText,
                                                draftContent = selectedPaper.fullDraftText
                                            )
                                        },
                                        enabled = aiReviewState !is UiState.Loading,
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("btn_ai_peer_review")
                                    ) {
                                        if (aiReviewState is UiState.Loading) {
                                            CircularProgressIndicator(modifier = Modifier.size(18.dp))
                                        } else {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("مراجعة الذكاء الاصطناعي")
                                        }
                                    }
                                }
                            }

                            // AI Review Result Banner if generated
                            item {
                                when (val state = aiReviewState) {
                                    is UiState.Success -> {
                                        val aiFeedback = state.data
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(16.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                                                    Text(
                                                        "تقرير التقييم الأكاديمي الذكي (AI Review)",
                                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                                        color = MaterialTheme.colorScheme.tertiary
                                                    )
                                                }
                                                Text("• الوضوح (${aiFeedback.clarityRating}/5): ${aiFeedback.clarityComment}", style = MaterialTheme.typography.bodySmall)
                                                Text("• المنهجية (${aiFeedback.methodologyRating}/5): ${aiFeedback.methodologyComment}", style = MaterialTheme.typography.bodySmall)
                                                Text("• الحجج (${aiFeedback.argumentationRating}/5): ${aiFeedback.argumentationComment}", style = MaterialTheme.typography.bodySmall)
                                                Text("• التوصيات العامة: ${aiFeedback.generalComment}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)

                                                Button(
                                                    onClick = {
                                                        viewModel.submitPeerFeedback(
                                                            paperId = selectedPaper.id,
                                                            reviewerAlias = aiFeedback.reviewerAlias,
                                                            clarityRating = aiFeedback.clarityRating,
                                                            methodologyRating = aiFeedback.methodologyRating,
                                                            argumentationRating = aiFeedback.argumentationRating,
                                                            clarityComment = aiFeedback.clarityComment,
                                                            methodologyComment = aiFeedback.methodologyComment,
                                                            argumentationComment = aiFeedback.argumentationComment,
                                                            generalComment = aiFeedback.generalComment
                                                        )
                                                        viewModel.resetAiPeerReviewState()
                                                    },
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text("حفظ هذا التقييم إلى المراجعات الأكاديمية")
                                                }
                                            }
                                        }
                                    }
                                    is UiState.Error -> {
                                        Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                    }
                                    else -> {}
                                }
                            }

                            // Feedbacks Stream Section
                            item {
                                Text(
                                    text = "سجل ملاحظات وتقييمات الأقران (${paperFeedbacks.size}):",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            if (paperFeedbacks.isEmpty()) {
                                item {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                    ) {
                                        Text(
                                            text = "لم يتم تقديم تقييمات لهذه المسودة بعد. كن أول من يراجع ويعطي تغذية راجعة بناءة!",
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(16.dp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                items(paperFeedbacks, key = { it.id }) { feedback ->
                                    val feedbackResponses = paperResponses.filter { it.feedbackId == feedback.id }
                                    PeerFeedbackCard(
                                        feedback = feedback,
                                        responses = feedbackResponses,
                                        onSendResponse = { responseText ->
                                            viewModel.submitPeerResponse(
                                                feedbackId = feedback.id,
                                                paperId = selectedPaper.id,
                                                responderAlias = "${selectedPaper.authorAlias} (مُعدّ البحث)",
                                                responseText = responseText
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Submit New Paper Draft
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                    Text(
                                        text = "يتم نشر المسودة بهوية مجهولة لحماية خصوصيتك وضمان التقييم الأكاديمي الشفاف القائم مجرداً على جودة الفكرة والمنهجية.",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }

                        item {
                            OutlinedTextField(
                                value = newPaperTitle,
                                onValueChange = { newPaperTitle = it },
                                label = { Text("عنوان المسودة البحثية *") },
                                placeholder = { Text("مثال: فاعلية نماذج الذكاء الاصطناعي في تحليل البيانات النوعية") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_new_paper_title"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newPaperField,
                                onValueChange = { newPaperField = it },
                                label = { Text("التخصص / المجال الأكاديمي *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_new_paper_field"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newPaperAuthorAlias,
                                onValueChange = { newPaperAuthorAlias = it },
                                label = { Text("اسم الباحث المستعار (Anonymous Alias) *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_new_paper_alias"),
                                singleLine = true
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newPaperAbstract,
                                onValueChange = { newPaperAbstract = it },
                                label = { Text("المستخلص الأكاديمي (Abstract) *") },
                                placeholder = { Text("لخّص مشكلة البحث، الهدف، العينة، والنتائج المتوقعة...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .testTag("input_new_paper_abstract"),
                                maxLines = 5
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = newPaperDraft,
                                onValueChange = { newPaperDraft = it },
                                label = { Text("نص المسودة الكاملة / الفصول الأولية *") },
                                placeholder = { Text("الصق نص المقدمة، المنهجية، أداة الدراسة، أو التحليلات...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .testTag("input_new_paper_draft"),
                                maxLines = 10
                            )
                        }

                        item {
                            Button(
                                onClick = {
                                    if (newPaperTitle.isNotBlank() && newPaperAbstract.isNotBlank() && newPaperDraft.isNotBlank()) {
                                        viewModel.submitPeerPaper(
                                            title = newPaperTitle,
                                            academicField = newPaperField,
                                            abstractText = newPaperAbstract,
                                            fullDraftText = newPaperDraft,
                                            authorAlias = newPaperAuthorAlias
                                        )
                                        showPaperSubmittedSnackbar = true
                                        newPaperTitle = ""
                                        newPaperAbstract = ""
                                        newPaperDraft = ""
                                        selectedTabIndex = 0
                                    }
                                },
                                enabled = newPaperTitle.isNotBlank() && newPaperAbstract.isNotBlank() && newPaperDraft.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("btn_submit_peer_paper")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("نشر المسودة المجهولة للمراجعة الأكاديمية")
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog to submit peer feedback based on criteria
    if (showAddFeedbackDialog && selectedPaper != null) {
        AddFeedbackModalDialog(
            paperTitle = selectedPaper.title,
            onDismiss = { showAddFeedbackDialog = false },
            onSubmit = { reviewerAlias, clarity, method, arg, clarityComm, methodComm, argComm, generalComm ->
                viewModel.submitPeerFeedback(
                    paperId = selectedPaper.id,
                    reviewerAlias = reviewerAlias,
                    clarityRating = clarity,
                    methodologyRating = method,
                    argumentationRating = arg,
                    clarityComment = clarityComm,
                    methodologyComment = methodComm,
                    argumentationComment = argComm,
                    generalComment = generalComm
                )
                showAddFeedbackDialog = false
            }
        )
    }
}

@Composable
fun PeerPaperCard(
    paper: PeerPaperEntity,
    feedbackCount: Int,
    avgClarity: Double,
    avgMethodology: Double,
    avgArgumentation: Double,
    onSelectPaper: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectPaper() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = paper.academicField,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = paper.authorAlias,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = paper.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Text(
                text = paper.abstractText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("الوضوح: ${String.format("%.1f", avgClarity)}★", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text("المنهجية: ${String.format("%.1f", avgMethodology)}★", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                    Text("الحجج: ${String.format("%.1f", avgArgumentation)}★", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
                }

                AssistChip(
                    onClick = onSelectPaper,
                    label = { Text("$feedbackCount مراجعات") },
                    leadingIcon = { Icon(Icons.Default.Comment, contentDescription = null, modifier = Modifier.size(16.dp)) }
                )
            }
        }
    }
}

@Composable
fun CriterionSummaryCard(
    title: String,
    rating: Double,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Text(title, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 1)
            Text(
                text = if (rating > 0) "${String.format("%.1f", rating)} / 5" else "لم يُقيم",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PeerFeedbackCard(
    feedback: PeerFeedbackEntity,
    responses: List<PeerResponseEntity>,
    onSendResponse: (String) -> Unit
) {
    var showReplyInput by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = feedback.reviewerAlias,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Text(
                    text = df.format(Date(feedback.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Ratings Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RatingBadge("الوضوح والأسلوب", feedback.clarityRating)
                RatingBadge("المنهجية العلمية", feedback.methodologyRating)
                RatingBadge("الحجج والمنطق", feedback.argumentationRating)
            }

            HorizontalDivider()

            // Detailed Comments per Criterion
            if (feedback.clarityComment.isNotBlank()) {
                Text("• الوضوح: ${feedback.clarityComment}", style = MaterialTheme.typography.bodySmall)
            }
            if (feedback.methodologyComment.isNotBlank()) {
                Text("• المنهجية: ${feedback.methodologyComment}", style = MaterialTheme.typography.bodySmall)
            }
            if (feedback.argumentationComment.isNotBlank()) {
                Text("• الحجج والتسلسل: ${feedback.argumentationComment}", style = MaterialTheme.typography.bodySmall)
            }
            if (feedback.generalComment.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "التوصية العامة: ${feedback.generalComment}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            // Author Responses Sub-Stream
            if (responses.isNotEmpty()) {
                HorizontalDivider()
                Text("ردود الباحث الكاتب:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

                responses.forEach { response ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(response.responderAlias, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Text(response.responseText, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Respond as Author Action
            if (!showReplyInput) {
                TextButton(onClick = { showReplyInput = true }) {
                    Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة رد ككاتب/مُعدّ البحث")
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("اكتب ردك أو إشادتك بالملاحظة للتواصل الأكاديمي...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onSendResponse(replyText)
                                    replyText = ""
                                    showReplyInput = false
                                }
                            }
                        ) {
                            Text("إرسال الرد")
                        }
                        TextButton(onClick = { showReplyInput = false }) {
                            Text("إلغاء")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RatingBadge(label: String, rating: Int) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text("$label: ", style = MaterialTheme.typography.labelSmall)
            Text("$rating★", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeedbackModalDialog(
    paperTitle: String,
    onDismiss: () -> Unit,
    onSubmit: (reviewerAlias: String, clarity: Int, method: Int, arg: Int, clarityComm: String, methodComm: String, argComm: String, generalComm: String) -> Unit
) {
    var reviewerAlias by remember { mutableStateOf("مراجع مجهول #${(100..999).random()}") }
    var clarityRating by remember { mutableFloatStateOf(4f) }
    var methodologyRating by remember { mutableFloatStateOf(4f) }
    var argumentationRating by remember { mutableFloatStateOf(4f) }

    var clarityComment by remember { mutableStateOf("") }
    var methodologyComment by remember { mutableStateOf("") }
    var argumentationComment by remember { mutableStateOf("") }
    var generalComment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تقديم تقييم وملاحظات نقدية بناءة", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text("المسودة: $paperTitle", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                item {
                    OutlinedTextField(
                        value = reviewerAlias,
                        onValueChange = { reviewerAlias = it },
                        label = { Text("اسم المراجع المستعار") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 1. Clarity Criterion
                item {
                    Text("1. الوضوح والأسلوب الأكاديمي (${clarityRating.toInt()}/5):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Slider(
                        value = clarityRating,
                        onValueChange = { clarityRating = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    OutlinedTextField(
                        value = clarityComment,
                        onValueChange = { clarityComment = it },
                        placeholder = { Text("ملاحظات حول وضوح الصياغة وسلسة الأفكار...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 2. Methodology Criterion
                item {
                    Text("2. المنهجية العلمية والأدوات (${methodologyRating.toInt()}/5):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Slider(
                        value = methodologyRating,
                        onValueChange = { methodologyRating = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    OutlinedTextField(
                        value = methodologyComment,
                        onValueChange = { methodologyComment = it },
                        placeholder = { Text("ملاحظات حول اختيار التصميم، العينة، والأدوات الإحصائية...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 3. Argumentation Criterion
                item {
                    Text("3. الحجج والتسلسل المنطقي (${argumentationRating.toInt()}/5):", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Slider(
                        value = argumentationRating,
                        onValueChange = { argumentationRating = it },
                        valueRange = 1f..5f,
                        steps = 3
                    )
                    OutlinedTextField(
                        value = argumentationComment,
                        onValueChange = { argumentationComment = it },
                        placeholder = { Text("ملاحظات حول التسلسل، دعم الادعاءات بالدليل، وتفسير النتائج...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // General Recommendations
                item {
                    Text("4. التوصية العامة والتحسينات الموصى بها:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = generalComment,
                        onValueChange = { generalComment = it },
                        placeholder = { Text("نصائح ختامية مشجعة لتطوير البحث...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmit(
                        reviewerAlias,
                        clarityRating.toInt(),
                        methodologyRating.toInt(),
                        argumentationRating.toInt(),
                        clarityComment,
                        methodologyComment,
                        argumentationComment,
                        generalComment
                    )
                }
            ) {
                Text("إرسال التقييم")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
