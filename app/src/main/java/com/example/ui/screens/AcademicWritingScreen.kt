package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.ui.AcademicViewModel
import com.example.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademicWritingScreen(
    viewModel: AcademicViewModel,
    onBack: () -> Unit
) {
    var draftText by remember { mutableStateOf("") }
    var selectedPolishMode by remember { mutableStateOf("صياغة أكاديمية رصينة وتوحيد المصطلحات") }

    val context = LocalContext.current
    val polishState by viewModel.polishUiState.collectAsState()

    val polishModes = listOf(
        "صياغة أكاديمية رصينة وتوحيد المصطلحات",
        "تحسين الربط المنهجي بين الفقرات",
        "التدقيق اللغوي والإملائي الدقيق",
        "تعزيز الحياد والأسلوب الأكاديمي المباشر"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("مراجعة وتحسين الصياغة الأكاديمية", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_writing")) {
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
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.EditNote, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Text(
                            text = "ساعد في ترقية نص مسودتك إلى أسلوب أكاديمي محكم مع المحافظة التامة على أفكارك الأصلية المعبرة دون انتحال.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            item {
                Text("اختر نمط التحسين المطلوب:", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    polishModes.forEach { mode ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedPolishMode == mode,
                                onClick = { selectedPolishMode = mode }
                            )
                            Text(mode, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = draftText,
                    onValueChange = { draftText = it },
                    label = { Text("النص المبدئي / فقرة البحث *") },
                    placeholder = { Text("الصق الفقرة التي ترغب في صياغتها أكاديمياً وتنسيقها هنا...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .testTag("input_writing_draft"),
                    maxLines = 8
                )
            }

            item {
                Button(
                    onClick = {
                        if (draftText.isNotBlank()) {
                            viewModel.polishAcademicWriting(draftText, selectedPolishMode)
                        }
                    },
                    enabled = draftText.isNotBlank() && polishState !is UiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_polish_writing")
                ) {
                    if (polishState is UiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("جاري مراجعة وتحسين النص...")
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تحسين الصياغة والأكاديمية")
                    }
                }
            }

            item {
                when (val state = polishState) {
                    is UiState.Error -> {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(state.message, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
                        }
                    }
                    is UiState.Success -> {
                        val resultText = state.data

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
                                        text = "النص بعد المراجعة الأكاديمية",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.tertiary
                                        )
                                    )
                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Academic Polish", resultText)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "تم نسخ النص المحسّن!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "نسخ النص")
                                    }
                                }

                                HorizontalDivider()

                                Text(
                                    text = resultText,
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
    }
}
