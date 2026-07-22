package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.*
import com.example.ui.AcademicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AcademicViewModel,
    onNavigateToTool: (String) -> Unit
) {
    val proposals by viewModel.allProposals.collectAsState()
    val summaries by viewModel.allSummaries.collectAsState()
    val citations by viewModel.allCitations.collectAsState()
    val dataAnalyses by viewModel.allDataAnalyses.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Academic Icon",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column {
                            Text(
                                text = "الباحث الأكاديمي",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "المرحلة الجامعية والدراسات العليا",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Hero Banner Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_academic_hero_1784693466792),
                            contentDescription = "Academic Research Hero",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(16.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "الجيل الذكي للبحث العلمي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "منصة تطوير البحث والتحليل النقدي",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = Color.White
                            )
                            Text(
                                text = "مساعدك الأكاديمي الشامل في الصياغة والتحليل والنزاهة العلمية",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }

            // Overview Stats Counters
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "خطط البحث",
                        count = proposals.size.toString(),
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "التلخيصات",
                        count = summaries.size.toString(),
                        icon = Icons.Default.AutoAwesome,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "المراجع",
                        count = citations.size.toString(),
                        icon = Icons.Default.Bookmark,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "التحليلات",
                        count = dataAnalyses.size.toString(),
                        icon = Icons.Default.BarChart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Core Academic Tools Grid / Section
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "أدوات البحث والتحليل الأكاديمي",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    AcademicToolCard(
                        title = "نظام المراجعة الأكاديمية التبادلية (Peer Review)",
                        subtitle = "تقديم مسودات الأبحاث بهوية مجهولة وتبادل الملاحظات النقدية والرد عليها وفق المعايير العلمية",
                        icon = Icons.Default.RateReview,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = { onNavigateToTool("peer_review") },
                        testTag = "btn_tool_peer_review"
                    )

                    AcademicToolCard(
                        title = "الوحدات التعليمية والاختبارات التفاعلية",
                        subtitle = "شروح مرئية، تمارين تطبيقة، واختبارات تفاعلية مستخرجة من أوراق البحث بالذكاء الاصطناعي",
                        icon = Icons.Default.School,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToTool("learning") },
                        testTag = "btn_tool_learning"
                    )

                    AcademicToolCard(
                        title = "إعداد خطة البحث والمحاور الرئيسية",
                        subtitle = "صياغة إشكالية البحث، الأسئلة، الفرضيات، والمنهجية العلمية",
                        icon = Icons.Default.AccountTree,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToTool("proposal") },
                        testTag = "btn_tool_proposal"
                    )

                    AcademicToolCard(
                        title = "تلخيص الأبحاث والتحليل النقدي",
                        subtitle = "استخراج النتائج، الفجوة البحثية، ونقد الدراسات السابقة",
                        icon = Icons.Default.Analytics,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        onClick = { onNavigateToTool("summary") },
                        testTag = "btn_tool_summary"
                    )

                    AcademicToolCard(
                        title = "مراجعة الصياغة والأسلوب الأكاديمي",
                        subtitle = "ترقية لغة البحث، توحيد المصطلحات، وتحسين الصياغة والأكاديمية",
                        icon = Icons.Default.EditNote,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        onClick = { onNavigateToTool("writing") },
                        testTag = "btn_tool_writing"
                    )

                    AcademicToolCard(
                        title = "تحليل البيانات والمساعدة الإحصائية",
                        subtitle = "حساب المؤشرات الإحصائية، رسم المخططات البيانية، وتفسير النتائج",
                        icon = Icons.Default.Equalizer,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToTool("data") },
                        testTag = "btn_tool_data"
                    )

                    AcademicToolCard(
                        title = "تنظيم المراجع والاقتباسات (APA/IEEE/MLA)",
                        subtitle = "تنسيق الاقتباسات التلقائي واقتراح مصادر ومراجع حديثة",
                        icon = Icons.Default.MenuBook,
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        iconTint = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToTool("citation") },
                        testTag = "btn_tool_citation"
                    )

                    AcademicToolCard(
                        title = "مستشار النزاهة الأكاديمية والضوابط",
                        subtitle = "قائمة التحقق الأخلاقية وضوابط استخدام الذكاء الاصطناعي كمساعد",
                        icon = Icons.Default.VerifiedUser,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        onClick = { onNavigateToTool("integrity") },
                        testTag = "btn_tool_integrity"
                    )
                }
            }

            // Saved Items Quick Preview
            if (proposals.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "آخر خطط البحوث المحفوظة",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(proposals.take(5)) { item ->
                                Card(
                                    modifier = Modifier
                                        .width(220.dp)
                                        .clickable { onNavigateToTool("proposal") },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = item.title,
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "التخصص: ${item.fieldOfStudy}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        SuggestionChip(
                                            onClick = {},
                                            label = { Text(item.degreeLevel, fontSize = 10.sp) }
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
fun StatCard(
    title: String,
    count: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = count,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun AcademicToolCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    iconTint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(containerColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(26.dp)
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronLeft,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
