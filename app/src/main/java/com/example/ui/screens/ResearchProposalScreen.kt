package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ResearchProposalEntity
import com.example.ui.AcademicViewModel
import com.example.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResearchProposalScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var fieldOfStudy by remember { mutableStateOf("") }
    var degreeLevel by remember { mutableStateOf("ماجستير") }
    var problemInput by remember { mutableStateOf("") }
    var methodologyType by remember { mutableStateOf("وصفي تحليلي") }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: new proposal, 1: saved proposals

    val proposalState by viewModel.proposalUiState.collectAsState()
    val savedProposals by viewModel.allProposals.collectAsState()

    val degreeOptions = listOf("جامعي (بكالوريوس)", "ماجستير", "دكتوراه", "بحث علمي حر")
    val methodOptions = listOf("وصفي تحليلي", "تجريبي", "مقارن", "تاريخي استردادي", "ميداني مسحي")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعداد خطة البحث والمحاور", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_proposal")) {
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
                    text = { Text("صياغة خطة جديدة") },
                    icon = { Icon(Icons.Default.Add, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("الخطط المحفوظة (${savedProposals.size})") },
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
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "يساعدك الذكاء الاصطناعي على هيكلة وصياغة إشكالية البحث ومحاوره الأساسية بما يحقق النزاهة والتفكير المستقل.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("عنوان البحث الإبتدائي *") },
                            placeholder = { Text("مثال: أثر الذكاء الاصطناعي على إدارة المشاريع الأكاديمية") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_proposal_title"),
                            singleLine = true
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = fieldOfStudy,
                            onValueChange = { fieldOfStudy = it },
                            label = { Text("التخصص العلمي *") },
                            placeholder = { Text("مثال: إدارة الأعمال، علوم الحاسب، القانون، الهندسة") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_proposal_field"),
                            singleLine = true
                        )
                    }

                    item {
                        Text("المرحلة الدراسية:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            degreeOptions.forEach { option ->
                                FilterChip(
                                    selected = degreeLevel == option,
                                    onClick = { degreeLevel = option },
                                    label = { Text(option, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    item {
                        Text("المنهجية المعتمدة:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            methodOptions.take(3).forEach { option ->
                                FilterChip(
                                    selected = methodologyType == option,
                                    onClick = { methodologyType = option },
                                    label = { Text(option, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = problemInput,
                            onValueChange = { problemInput = it },
                            label = { Text("إشكالية أو خلفية البحث المبدئية *") },
                            placeholder = { Text("اكتب ملخصاً سريحاً عن الظاهرة التي تريد دراستها والهدف المرجو...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("input_proposal_problem"),
                            maxLines = 5
                        )
                    }

                    item {
                        Button(
                            onClick = {
                                if (title.isNotBlank() && fieldOfStudy.isNotBlank()) {
                                    viewModel.generateResearchProposal(
                                        title = title,
                                        fieldOfStudy = fieldOfStudy,
                                        degreeLevel = degreeLevel,
                                        problemStatementInput = problemInput,
                                        methodologyType = methodologyType
                                    )
                                }
                            },
                            enabled = title.isNotBlank() && fieldOfStudy.isNotBlank() && proposalState !is UiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("btn_generate_proposal")
                        ) {
                            if (proposalState is UiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("جاري إعداد خطة البحث...")
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("توليد خطة البحث والمحاور")
                            }
                        }
                    }

                    // Display Generated Result
                    item {
                        when (val state = proposalState) {
                            is UiState.Error -> {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = state.message,
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                            is UiState.Success -> {
                                val generatedProposal = state.data
                                var isSaved by remember { mutableStateOf(false) }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
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
                                                text = "مخرجات خطة البحث الموصى بها",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            Button(
                                                onClick = {
                                                    viewModel.saveProposal(generatedProposal)
                                                    isSaved = true
                                                },
                                                enabled = !isSaved,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.secondary
                                                )
                                            ) {
                                                Icon(
                                                    if (isSaved) Icons.Default.Check else Icons.Default.Bookmark,
                                                    contentDescription = null
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isSaved) "تم الحفظ" else "حفظ الخطة")
                                            }
                                        }

                                        HorizontalDivider()

                                        Text(
                                            text = generatedProposal.mainAxes,
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
                // Saved Proposals Tab
                if (savedProposals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "لا توجد خطط بحث محددة محفوظة حتى الآن.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(savedProposals, key = { it.id }) { item ->
                            SavedProposalCard(
                                proposal = item,
                                onDelete = { viewModel.deleteProposal(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedProposalCard(
    proposal: ResearchProposalEntity,
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
                Text(
                    text = proposal.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text(proposal.fieldOfStudy) })
                AssistChip(onClick = {}, label = { Text(proposal.degreeLevel) })
            }

            if (expanded) {
                HorizontalDivider()
                Text(
                    text = proposal.mainAxes,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 20.sp
                )
            }

            TextButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (expanded) "طي الخطة" else "عرض الخطة كاملة")
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }
        }
    }
}
