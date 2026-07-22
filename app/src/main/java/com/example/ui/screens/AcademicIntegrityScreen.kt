package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AcademicViewModel

data class IntegrityCheckItem(
    val title: String,
    val description: String,
    var isChecked: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicIntegrityScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    val checklistState = remember {
        mutableStateListOf(
            IntegrityCheckItem(
                title = "الالتزام التام بالتوثيق والأمانة العلمية",
                description = "تم إسناد كافة الأفكار والمعلومات والاقتباسات لأصحابها الأصليين مع مراعاة نمط التوثيق المعتمد (APA, IEEE, إلخ)."
            ),
            IntegrityCheckItem(
                title = "استخدام الذكاء الاصطناعي كأداة مساعدة وليس ككاتب كلي",
                description = "تم التزام دور الباحث في التفكير المستقل، وإعداد البحث بنفسه مع استعانة مكملة بالأدوات للتحليل والتحسين."
            ),
            IntegrityCheckItem(
                title = "سلامة وصحة البيانات والتحليل الإحصائي",
                description = "تم جمع البيانات من عينة حقيقية وتمت معالجتها إحصائياً دون أي تزييف أو تعديل متحيز للنتائج."
            ),
            IntegrityCheckItem(
                title = "تجنب الانتحال العلمي إلكترونياً (Plagiarism-Free)",
                description = "تم فحص النص للتأكد من عدم تطابق الفقرات مباشرة وحفظ حقوق الملكية الفكرية."
            ),
            IntegrityCheckItem(
                title = "الموافقات الأخلاقية للدراسات الميدانية والتجريبية",
                description = "تم الحصول على موافقة المشاركين وحفظ خصوصية بياناتهم في الأبحاث الاجتماعية والطبية والميدانية."
            )
        )
    }

    val checkedCount = checklistState.count { it.isChecked }
    val totalCount = checklistState.size
    val isFullyCompliant = checkedCount == totalCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مستشار النزاهة والضوابط الأكاديمية", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_integrity")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notice Banner
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                            Text(
                                text = "تنبيه هائم حول النزاهة العلمية ⚠️",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Text(
                            text = "تلتزم هذه المنصة بقواعد النزاهة الأكاديمية الصارمة المعمول بها في الجامعات والمؤسسات البحثية. يُمنع منعاً باتاً الاعتماد على أدوات الذكاء الاصطناعي لكتابة الأبحاث الكاملة نيابة عن الباحث.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            // Compliance Progress Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isFullyCompliant) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
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
                            Text(
                                text = "مؤشر الجاهزية والنزاهة العلمية",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Badge(
                                containerColor = if (isFullyCompliant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ) {
                                Text(
                                    text = "$checkedCount / $totalCount متطلب",
                                    modifier = Modifier.padding(4.dp),
                                    color = Color.White
                                )
                            }
                        }

                        LinearProgressIndicator(
                            progress = { checkedCount.toFloat() / totalCount.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                        )

                        if (isFullyCompliant) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Text(
                                    text = "بحثك مستوفٍ لمعايير النزاهة والأخلاقيات البحثية المطلوبة!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Checklist Items
            item {
                Text(
                    text = "قائمة التحقق المسبق قبل تسليم البحث/الأطروحة:",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            itemsIndexed(checklistState) { index, item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = item.isChecked,
                            onCheckedChange = { checked ->
                                checklistState[index] = item.copy(isChecked = checked)
                            },
                            modifier = Modifier.testTag("chk_integrity_$index")
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Rules & Recommendations
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "إرشادات الاستخدام المسموح به للذكاء الاصطناعي 📘",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        HorizontalDivider()
                        Text("• المسموح: العصف الذهني واقتراح المحاور، تلخيص واستخراج أفكار المقالات، مراجعة الصياغة والتصحيح اللغوي، المساعدة في حساب المؤشرات الإحصائية وتفسيرها.", style = MaterialTheme.typography.bodySmall, lineHeight = 20.sp)
                        Text("• الممنوع: كتابة كامل أطروحة أو بحث ونسبته لنفسك، اختلاق وتلفيق أرقام أو استبانات أو مراجع وهمية، عدم نسبة الاقتباسات لأصحابها الأصليين.", style = MaterialTheme.typography.bodySmall, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}
