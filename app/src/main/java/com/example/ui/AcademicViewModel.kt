package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AcademicRepository
import com.example.data.local.*
import com.example.data.remote.GeminiClient
import com.example.util.AcademicUtils
import com.example.util.StatisticsResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class AcademicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AcademicRepository

    val allProposals: StateFlow<List<ResearchProposalEntity>>
    val allSummaries: StateFlow<List<LiteratureSummaryEntity>>
    val allCitations: StateFlow<List<CitationEntity>>
    val allDataAnalyses: StateFlow<List<DataAnalysisEntity>>

    init {
        val dao = AcademicDatabase.getDatabase(application).academicDao()
        repository = AcademicRepository(dao)

        allProposals = repository.allProposals.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allSummaries = repository.allSummaries.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allCitations = repository.allCitations.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allDataAnalyses = repository.allDataAnalysis.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
    }

    // 1. Research Proposal State
    private val _proposalUiState = MutableStateFlow<UiState<ResearchProposalEntity>>(UiState.Idle)
    val proposalUiState: StateFlow<UiState<ResearchProposalEntity>> = _proposalUiState

    fun generateResearchProposal(
        title: String,
        fieldOfStudy: String,
        degreeLevel: String,
        problemStatementInput: String,
        methodologyType: String
    ) {
        viewModelScope.launch {
            _proposalUiState.value = UiState.Loading

            val prompt = """
                بصفتك خبيراً أكاديمياً في إعداد الخطط البحثية للمرحلة الجامعية والدراسات العليا، صمم خطة بحث شاملة للموضوع التالي:
                - عنوان البحث: $title
                - التخصص العلمي: $fieldOfStudy
                - المرحلة الدراسية: $degreeLevel
                - خلفية الإشكالية أو الفكرة: $problemStatementInput
                - المنهج المفضل: $methodologyType

                قم بصياغة مخرجات واضحة باللغة العربية تحتوي على:
                1. صياغة دقيقة وصارمة لإشكالية البحث (Problem Statement).
                2. أسئلة البحث الرئيسية والفرعية والفرضيات.
                3. تفصيل المنهجية والأدوات والأسباب.
                4. محاور وأبواب الدراسية التفصيلية (الأبواب/الفصول والمباحث).
            """.trimIndent()

            val result = GeminiClient.generateContent(prompt)
            result.onSuccess { responseText ->
                val proposal = ResearchProposalEntity(
                    title = title,
                    fieldOfStudy = fieldOfStudy,
                    degreeLevel = degreeLevel,
                    problemStatement = problemStatementInput,
                    researchQuestions = responseText,
                    methodology = methodologyType,
                    mainAxes = responseText
                )
                _proposalUiState.value = UiState.Success(proposal)
            }.onFailure { err ->
                _proposalUiState.value = UiState.Error(err.localizedMessage ?: "حدث خطأ أثناء صياغة خطة البحث.")
            }
        }
    }

    fun saveProposal(proposal: ResearchProposalEntity) {
        viewModelScope.launch {
            repository.insertProposal(proposal)
        }
    }

    fun deleteProposal(proposal: ResearchProposalEntity) {
        viewModelScope.launch {
            repository.deleteProposal(proposal)
        }
    }

    fun resetProposalState() {
        _proposalUiState.value = UiState.Idle
    }

    // 2. Literature Summary State
    private val _summaryUiState = MutableStateFlow<UiState<LiteratureSummaryEntity>>(UiState.Idle)
    val summaryUiState: StateFlow<UiState<LiteratureSummaryEntity>> = _summaryUiState

    fun summarizeAndAnalyzeLiterature(
        paperTitle: String,
        authorsAndYear: String,
        paperText: String
    ) {
        viewModelScope.launch {
            _summaryUiState.value = UiState.Loading

            val prompt = """
                قم بتلخيص وتحليل نقدي أكاديمي دقيق للورقة العلمية/المقال التالي:
                - عنوان الدراسة: $paperTitle
                - الباحثون والسنة: $authorsAndYear
                - النص الاصلي / المستخلص:
                $paperText

                قم باستخراج الهيكل النقدي التالي باللغة العربية الأكاديمية:
                1. [النتائج والأهداف الأساسية]: ملخص النتائج المحورية برؤية علمية دقيقة.
                2. [المنهجية وعينة الدراسة]: تقييم المنهج المستخدم وأدوات جمع البيانات.
                3. [التحليل النقدي والفجوة البحثية]: نقاط القوة، نقاط الضعف، الفجوة البحثية (Research Gap)، وتوصيات لبحوث مستقبيلة لتطوير التفكير المستقل للباحث.
            """.trimIndent()

            val result = GeminiClient.generateContent(prompt)
            result.onSuccess { text ->
                val summary = LiteratureSummaryEntity(
                    paperTitle = paperTitle,
                    authorsAndYear = authorsAndYear,
                    originalTextOrAbstract = paperText,
                    summaryFindings = text,
                    methodologyUsed = "تم استخلاصها نقدياً",
                    researchGaps = text
                )
                _summaryUiState.value = UiState.Success(summary)
            }.onFailure { err ->
                _summaryUiState.value = UiState.Error(err.localizedMessage ?: "حدث خطأ أثناء تلخيص الدراسة.")
            }
        }
    }

    fun saveSummary(summary: LiteratureSummaryEntity) {
        viewModelScope.launch {
            repository.insertSummary(summary)
        }
    }

    fun deleteSummary(summary: LiteratureSummaryEntity) {
        viewModelScope.launch {
            repository.deleteSummary(summary)
        }
    }

    fun resetSummaryState() {
        _summaryUiState.value = UiState.Idle
    }

    // 3. Academic Writing Polish State
    private val _polishUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val polishUiState: StateFlow<UiState<String>> = _polishUiState

    fun polishAcademicWriting(draftText: String, polishType: String) {
        viewModelScope.launch {
            _polishUiState.value = UiState.Loading

            val prompt = """
                قم بمراجعة وصياغة النص الأكاديمي التالي وفق أعلى معايير الكتابة الأكاديمية والمجلات المحكمة:
                - نوع التحسين المطلوب: $polishType
                - النص الأصلي:
                "$draftText"

                قم بتقديم النص بعد التحسين مع شرح مختصر ومحدد بالتغييرات الأكاديمية والمصطلحات التي تم توحيدها.
            """.trimIndent()

            val result = GeminiClient.generateContent(prompt)
            result.onSuccess { text ->
                _polishUiState.value = UiState.Success(text)
            }.onFailure { err ->
                _polishUiState.value = UiState.Error(err.localizedMessage ?: "حدث خطأ أثناء تحسين الصياغة.")
            }
        }
    }

    fun resetPolishState() {
        _polishUiState.value = UiState.Idle
    }

    // 4. Data Analysis State
    private val _dataAnalysisUiState = MutableStateFlow<UiState<Pair<StatisticsResult, String>>>(UiState.Idle)
    val dataAnalysisUiState: StateFlow<UiState<Pair<StatisticsResult, String>>> = _dataAnalysisUiState

    fun analyzeDataset(title: String, rawDataInput: String) {
        viewModelScope.launch {
            _dataAnalysisUiState.value = UiState.Loading

            val stats = AcademicUtils.parseAndCalculateStats(rawDataInput)
            if (stats == null) {
                _dataAnalysisUiState.value = UiState.Error("يرجى إدخال قيم عددية صحيحة مفصولة بفاصلة (مثال: 10, 15, 20, 25)")
                return@launch
            }

            val prompt = """
                قم بتقديم تفسير إحصائي وأكاديمي دقيق وموجز للنتائج الإحصائية التالية لتجربة/دراسة بعنوان "$title":
                - عدد العينة (N): ${stats.count}
                - الوسط الحسابي (Mean): ${String.format("%.2f", stats.mean)}
                - الوسيط (Median): ${String.format("%.2f", stats.median)}
                - الانحراف المعياري (Std Dev): ${String.format("%.2f", stats.stdDev)}
                - النطاق (Min - Max): ${stats.min} إلى ${stats.max}

                المطلوب:
                1. تفسير معنى هذه المؤشرات بأسلوب علمي مناسب لفصل النتائج والمناقشة.
                2. توجيه حول كيفية عرضها في الجداول الإحصائية للأبحاث الجامعية.
            """.trimIndent()

            val result = GeminiClient.generateContent(prompt)
            val interpretation = result.getOrDefault(
                "أظهرت النتائج أن الوسط الحسابي بلغ ${String.format("%.2f", stats.mean)} بانحراف معيار قدره ${String.format("%.2f", stats.stdDev)}."
            )

            _dataAnalysisUiState.value = UiState.Success(Pair(stats, interpretation))
        }
    }

    fun saveDataAnalysis(title: String, rawInput: String, stats: StatisticsResult, interpretation: String) {
        viewModelScope.launch {
            val entity = DataAnalysisEntity(
                title = title,
                rawDataValues = rawInput,
                meanValue = stats.mean,
                medianValue = stats.median,
                stdDevValue = stats.stdDev,
                sampleCount = stats.count,
                minValue = stats.min,
                maxValue = stats.max,
                statisticalSummaryText = interpretation
            )
            repository.insertDataAnalysis(entity)
        }
    }

    fun deleteDataAnalysis(entity: DataAnalysisEntity) {
        viewModelScope.launch {
            repository.deleteDataAnalysis(entity)
        }
    }

    fun resetDataAnalysisState() {
        _dataAnalysisUiState.value = UiState.Idle
    }

    // 5. Reference & Citation State
    private val _suggestedReferencesUiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val suggestedReferencesUiState: StateFlow<UiState<String>> = _suggestedReferencesUiState

    fun suggestAcademicReferences(topic: String, field: String) {
        viewModelScope.launch {
            _suggestedReferencesUiState.value = UiState.Loading

            val prompt = """
                اقترح قائمة من 5 مراجع ومصادر موثوقة وحديثة (كتب محكمة، مقالات مجلات علمية) في مجال التخصص التالي:
                - موضوع البحث: $topic
                - التخصص: $field

                المطلوب باللغة العربية:
                لكل مرجع: اسم المؤلف، عنوان المرجع، السنة، اسم المجلة/الناشر، أهمية المرجع لموضوع البحث، والكلمات المفتاحية الموصى بها عند البحث بالدورية أو المطبوعات.
            """.trimIndent()

            val result = GeminiClient.generateContent(prompt)
            result.onSuccess { text ->
                _suggestedReferencesUiState.value = UiState.Success(text)
            }.onFailure { err ->
                _suggestedReferencesUiState.value = UiState.Error(err.localizedMessage ?: "حدث خطأ أثناء اقتراح المراجع.")
            }
        }
    }

    fun saveCitation(
        sourceType: String,
        authors: String,
        title: String,
        year: String,
        publisherOrJournal: String,
        doiOrUrl: String
    ) {
        viewModelScope.launch {
            val apa = AcademicUtils.formatApa(authors, title, year, publisherOrJournal, doiOrUrl)
            val ieee = AcademicUtils.formatIeee(authors, title, year, publisherOrJournal, doiOrUrl)
            val mla = AcademicUtils.formatMla(authors, title, year, publisherOrJournal, doiOrUrl)

            val citation = CitationEntity(
                sourceType = sourceType,
                authors = authors,
                title = title,
                year = year,
                publisherOrJournal = publisherOrJournal,
                doiOrUrl = doiOrUrl,
                formattedApa = apa,
                formattedIeee = ieee,
                formattedMla = mla
            )
            repository.insertCitation(citation)
        }
    }

    fun deleteCitation(citation: CitationEntity) {
        viewModelScope.launch {
            repository.deleteCitation(citation)
        }
    }

    fun resetReferenceSuggestionState() {
        _suggestedReferencesUiState.value = UiState.Idle
    }
}
