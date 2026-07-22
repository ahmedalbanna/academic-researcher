package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AcademicRepository
import com.example.data.local.*
import com.example.data.model.QuizQuestion
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
    val allPeerPapers: StateFlow<List<PeerPaperEntity>>
    val allPeerFeedbacks: StateFlow<List<PeerFeedbackEntity>>
    val allPeerResponses: StateFlow<List<PeerResponseEntity>>

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
        allPeerPapers = repository.allPeerPapers.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allPeerFeedbacks = repository.allPeerFeedbacks.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allPeerResponses = repository.allPeerResponses.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

        viewModelScope.launch {
            repository.allPeerPapers.firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    seedDefaultPeerData()
                }
            }
        }
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

    // 6. Interactive Learning & Quiz Generator State
    private val _aiQuizUiState = MutableStateFlow<UiState<List<QuizQuestion>>>(UiState.Idle)
    val aiQuizUiState: StateFlow<UiState<List<QuizQuestion>>> = _aiQuizUiState

    private val _userQuizScores = MutableStateFlow<Map<String, Int>>(emptyMap())
    val userQuizScores: StateFlow<Map<String, Int>> = _userQuizScores

    fun recordQuizScore(moduleId: String, scorePercentage: Int) {
        val current = _userQuizScores.value.toMutableMap()
        current[moduleId] = scorePercentage
        _userQuizScores.value = current
    }

    fun generateQuizFromPaper(paperTitle: String, paperContent: String) {
        viewModelScope.launch {
            _aiQuizUiState.value = UiState.Loading

            val prompt = """
                أنت أستاذ تقييم أكاديمي وتفكير نقدي في الدراسات العليا. بناءً على الورقة البحثية التالية:
                - العنوان: $paperTitle
                - المحتوى / المستخلص:
                $paperContent

                أنشئ 3 أسئلة تفاعلية تقيس التفكير النقدي، تقييم المنهجية، والنتائج لهذه الورقة.
                صغ الإجابة بالشكل التالي لكل سؤال:

                سؤال: [نص السؤال هنا]
                خيارات:
                أ) [الخيار الأول]
                ب) [الخيار الثاني]
                ج) [الخيار الثالث]
                د) [الخيار الرابع]
                إجابة: [رقم الخيار الصحيح 0 أو 1 أو 2 أو 3]
                شرح: [تفسير علمي سريع لسبب صحة الخيار]
                ---
            """.trimIndent()

            val result = GeminiClient.generateContent(prompt)
            result.onSuccess { rawText ->
                val questions = parsePaperQuiz(rawText, paperTitle)
                _aiQuizUiState.value = UiState.Success(questions)
            }.onFailure { err ->
                _aiQuizUiState.value = UiState.Error(err.localizedMessage ?: "حدث خطأ أثناء توليد اختبار الورقة البحثية.")
            }
        }
    }

    private fun parsePaperQuiz(text: String, title: String): List<QuizQuestion> {
        val questions = mutableListOf<QuizQuestion>()
        try {
            val blocks = text.split("---")
            var idCounter = 1
            for (block in blocks) {
                if (block.isBlank()) continue
                val lines = block.lines().map { it.trim() }.filter { it.isNotBlank() }
                
                var questionText = ""
                val options = mutableListOf<String>()
                var correctIdx = 0
                var explanation = "تفسير بناءً على التحليل النقدي للورقة البحثية."

                for (line in lines) {
                    when {
                        line.startsWith("سؤال:") || line.startsWith("سؤال") -> questionText = line.substringAfter(":").trim()
                        line.startsWith("أ)") || line.startsWith("1)") || line.startsWith("أ.") -> options.add(line.substring(2).trim())
                        line.startsWith("ب)") || line.startsWith("2)") || line.startsWith("ب.") -> options.add(line.substring(2).trim())
                        line.startsWith("ج)") || line.startsWith("3)") || line.startsWith("ج.") -> options.add(line.substring(2).trim())
                        line.startsWith("د)") || line.startsWith("4)") || line.startsWith("د.") -> options.add(line.substring(2).trim())
                        line.startsWith("إجابة:") || line.startsWith("الاجابة:") -> {
                            val numStr = line.substringAfter(":").trim().lowercase()
                            correctIdx = when {
                                numStr.contains("0") || numStr.contains("أ") -> 0
                                numStr.contains("1") || numStr.contains("ب") -> 1
                                numStr.contains("2") || numStr.contains("ج") -> 2
                                numStr.contains("3") || numStr.contains("د") -> 3
                                else -> 0
                            }
                        }
                        line.startsWith("شرح:") -> explanation = line.substringAfter(":").trim()
                    }
                }

                if (questionText.isNotBlank()) {
                    val finalOptions = if (options.size >= 4) options.take(4) else listOf(
                        "تقييم دقيق للمتغيرات والمنهجية",
                        "محدودية عينة الدراسة والتعميم",
                        "الالتزام بالتحليل الإحصائي المحايد",
                        "ربط النتائج بالتوصيات المباشرة"
                    )
                    questions.add(
                        QuizQuestion(
                            id = idCounter++,
                            questionText = questionText,
                            options = finalOptions,
                            correctAnswerIndex = correctIdx,
                            explanation = explanation
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (questions.isEmpty()) {
            // Fallback default questions
            return listOf(
                QuizQuestion(
                    id = 1,
                    questionText = "ما هي المساهمة الأساسية والفجوة البحثية التي تناولتها الدراسة: ($title)؟",
                    options = listOf(
                        "استكشاف وتحليل متغيرات الموضوع بناءً على المستخلص المقدم",
                        "تكرار النماذج السابقة بدون إبعاد جديدة",
                        "عدم وجود منهجية واضحة للبحث",
                        "الاعتماد الكامل على الفرضيات الاستطلاعية"
                    ),
                    correctAnswerIndex = 0,
                    explanation = "تعبر المساهمة العلمية عن القيمة المضافة والمعرفة الجوهرية للورقة الأكاديمية."
                ),
                QuizQuestion(
                    id = 2,
                    questionText = "كيف تقيم أسلوب معالجة البيانات والنتائج الموضحة بالورقة؟",
                    options = listOf(
                        "تحليل موضوعي يربط الفرضيات بالأرقام أو الأدلة النوعية",
                        "تحيز كامل للنتائج المتوقعة",
                        "إغفال مناقشة الأدبيات المرجعية",
                        "عدم وضوح عينة البحث"
                    ),
                    correctAnswerIndex = 0,
                    explanation = "الربط المنطقي بين الأرقام أو الأدلة والتساؤلات هو معيار الرصانة والأمانة العلمية."
                )
            )
        }
        return questions
    }

    fun resetAiQuizState() {
        _aiQuizUiState.value = UiState.Idle
    }

    // 7. Peer Review System Methods
    private val _aiPeerReviewUiState = MutableStateFlow<UiState<PeerFeedbackEntity>>(UiState.Idle)
    val aiPeerReviewUiState: StateFlow<UiState<PeerFeedbackEntity>> = _aiPeerReviewUiState

    fun submitPeerPaper(
        title: String,
        academicField: String,
        abstractText: String,
        fullDraftText: String,
        authorAlias: String
    ) {
        viewModelScope.launch {
            val paper = PeerPaperEntity(
                title = title.trim(),
                academicField = academicField.trim().ifEmpty { "دراسات عامة" },
                abstractText = abstractText.trim(),
                fullDraftText = fullDraftText.trim(),
                authorAlias = authorAlias.ifEmpty { "باحث مجهول #${(1000..9999).random()}" }
            )
            repository.insertPeerPaper(paper)
        }
    }

    fun submitPeerFeedback(
        paperId: Long,
        reviewerAlias: String,
        clarityRating: Int,
        methodologyRating: Int,
        argumentationRating: Int,
        clarityComment: String,
        methodologyComment: String,
        argumentationComment: String,
        generalComment: String
    ) {
        viewModelScope.launch {
            val feedback = PeerFeedbackEntity(
                paperId = paperId,
                reviewerAlias = reviewerAlias.ifEmpty { "مراجع مجهول #${(100..999).random()}" },
                clarityRating = clarityRating.coerceIn(1, 5),
                methodologyRating = methodologyRating.coerceIn(1, 5),
                argumentationRating = argumentationRating.coerceIn(1, 5),
                clarityComment = clarityComment.trim(),
                methodologyComment = methodologyComment.trim(),
                argumentationComment = argumentationComment.trim(),
                generalComment = generalComment.trim()
            )
            repository.insertPeerFeedback(feedback)
        }
    }

    fun submitPeerResponse(
        feedbackId: Long,
        paperId: Long,
        responderAlias: String,
        responseText: String
    ) {
        viewModelScope.launch {
            val response = PeerResponseEntity(
                feedbackId = feedbackId,
                paperId = paperId,
                responderAlias = responderAlias.ifEmpty { "مُعدّ البحث" },
                responseText = responseText.trim()
            )
            repository.insertPeerResponse(response)
        }
    }

    fun generateAiPeerReview(paperId: Long, paperTitle: String, abstractText: String, draftContent: String) {
        viewModelScope.launch {
            _aiPeerReviewUiState.value = UiState.Loading

            val prompt = """
                أنت محكّم أكاديمي ومراجع أقران خبير للرسائل والأبحاث العلمية.
                قم بتقييم المسودة البحثية التالية بناءً على 3 محاور رئيسية مع تقديم تعليقات بناة وتوجيهات تطويرية:
                - العنوان: $paperTitle
                - المستخلص: $abstractText
                - المسودة: $draftContent

                يرجى تقديم التقييم بوضوح وفقاً للصيغة التالية بالضبط:
                وضوح_درجة: [رقم من 1 إلى 5]
                وضوح_تعليق: [تعليق حول السلاسة، التنسيق، واللغة الأكاديمية]
                منهجية_درجة: [رقم من 1 إلى 5]
                منهجية_تعليق: [تعليق حول التصميم البحثي واختيار الأدوات والعينة]
                حجج_درجة: [رقم من 1 إلى 5]
                حجج_تعليق: [تعليق حول التسلسل المنطقي، أدلة الإثبات والربط بالمراجع]
                عام_تعليق: [توصية عامة ختامية تشجع الباحث وتلخص أبرز القوة والتحسينات]
            """.trimIndent()

            val result = GeminiClient.generateContent(prompt)
            result.onSuccess { rawText ->
                val feedback = parseAiPeerReviewText(paperId, rawText)
                _aiPeerReviewUiState.value = UiState.Success(feedback)
            }.onFailure { err ->
                _aiPeerReviewUiState.value = UiState.Error(err.localizedMessage ?: "حدث خطأ أثناء إعداد التقييم الأكاديمي.")
            }
        }
    }

    private fun parseAiPeerReviewText(paperId: Long, text: String): PeerFeedbackEntity {
        var clarityRating = 4
        var methodologyRating = 4
        var argumentationRating = 4
        var clarityComment = "الصياغة الأكاديمية واضحة ومباشرة."
        var methodologyComment = "المنهجية المتبعة مناسبة لتساؤلات البحث."
        var argumentationComment = "التسلسل المنطقي للحجج مدعوم بالأدلة."
        var generalComment = "بحث متميز يمتلك مقومات إضافة قيمة علمية حقيقية."

        val lines = text.lines().map { it.trim() }
        for (line in lines) {
            when {
                line.startsWith("وضوح_درجة:") -> clarityRating = line.substringAfter(":").trim().toIntOrNull()?.coerceIn(1, 5) ?: 4
                line.startsWith("وضوح_تعليق:") -> clarityComment = line.substringAfter(":").trim()
                line.startsWith("منهجية_درجة:") -> methodologyRating = line.substringAfter(":").trim().toIntOrNull()?.coerceIn(1, 5) ?: 4
                line.startsWith("منهجية_تعليق:") -> methodologyComment = line.substringAfter(":").trim()
                line.startsWith("حجج_درجة:") -> argumentationRating = line.substringAfter(":").trim().toIntOrNull()?.coerceIn(1, 5) ?: 4
                line.startsWith("حجج_تعليق:") -> argumentationComment = line.substringAfter(":").trim()
                line.startsWith("عام_تعليق:") -> generalComment = line.substringAfter(":").trim()
            }
        }

        return PeerFeedbackEntity(
            paperId = paperId,
            reviewerAlias = "مساعد التقييم الأكاديمي الذكي (AI Peer Reviewer)",
            clarityRating = clarityRating,
            methodologyRating = methodologyRating,
            argumentationRating = argumentationRating,
            clarityComment = clarityComment,
            methodologyComment = methodologyComment,
            argumentationComment = argumentationComment,
            generalComment = generalComment
        )
    }

    fun resetAiPeerReviewState() {
        _aiPeerReviewUiState.value = UiState.Idle
    }

    private suspend fun seedDefaultPeerData() {
        val paper1Id = repository.insertPeerPaper(
            PeerPaperEntity(
                title = "فاعلية بيئات التعلم التكيفية القائمة على الذكاء الاصطناعي في تنمية التفكير التحليلي",
                academicField = "تكنولوجيا التعليم",
                abstractText = "تهدف الدراسة إلى تقييم أثر استخدام منصة تعلم ذكية تقوم بتحديد نمط القارئ وتوفير محتوى مخصص لطلاب الدراسات العليا. شملت العينة 45 طالباً وطالبة.",
                fullDraftText = "1. المقدمة:\nتزايد الاهتمام بالتعليم الرقمي المخصص...\n2. المنهجية:\nتم استخدام المنهج شبه التجريبي ذو المجموعتين الضابطة والتجريبية، وطبّق اختبار التفكير التحليلي القبلي والبعدي...\n3. النتائج والمناقشة:\nأظهرت نتائج اختبار t وجود فروق ذات دلالة إحصائية عند مستوى (0.05) لصالح المجموعة التجريبية...",
                authorAlias = "باحث مجهول #3021",
                status = "قيد المراجعة"
            )
        )

        val paper2Id = repository.insertPeerPaper(
            PeerPaperEntity(
                title = "تقييم أداء الخوارزميات المتقدمة في التنبؤ بالاتجاهات المسبقة في الأسواق المالية",
                academicField = "العلوم المالية والبيانات",
                abstractText = "تسعى هذه الورقة لاستكشاف نماذج الشبكات العصبية العميقة وتطبيقها على البيانات التاريخية لمؤشرات سوق الأسهم بهدف تحسين دقة التنبؤ وتقليل نسبة المخاطرة.",
                fullDraftText = "مقدمة الدراسة وهيكلها:\nتم استعراض نماذج LSTM و Transformer وقدرتها على معالجة السلاسل الزمنية المعقدة...\nالتحليل القياسي والإحصائي:\nاستخدمنا معايير RMSE و MAE لتقييم أداء النماذج على بيانات امتدت لـ 5 سنوات...",
                authorAlias = "باحث مجهول #7190",
                status = "مراجعة مكتملة"
            )
        )

        // Seed feedback for Paper 1
        val feedback1Id = repository.insertPeerFeedback(
            PeerFeedbackEntity(
                paperId = paper1Id,
                reviewerAlias = "مراجع أكاديمي #104",
                clarityRating = 5,
                methodologyRating = 4,
                argumentationRating = 4,
                clarityComment = "المقدمة والمسودة مكتوبة بلغة أكاديمية رصينة للغاية، والأفكار متتابعة بسلاسة.",
                methodologyComment = "التصميم شبه التجريبي ممتاز، ولكن يُفضل توضيح كيفية التحقق من صدق وثبات أداة المقياس بشكل أكثر تفصيلاً.",
                argumentationComment = "ربط النتائج بفرضيات البحث منطقي ومدعوم بأدبيات قوية.",
                generalComment = "دراسة واعدة جداً. التعديلات المطلوبة طفيفة وتتركز في ملحق صدق الأداة."
            )
        )

        // Seed author response for Feedback 1
        repository.insertPeerResponse(
            PeerResponseEntity(
                feedbackId = feedback1Id,
                paperId = paper1Id,
                responderAlias = "باحث مجهول #3021 (مُعدّ البحث)",
                responseText = "شكراً جزيلاً للمراجع الفاضل على هذا النقد البنّاء. تم إضافة جزء خاص بـ (معامل ألفا كرونباخ) والصدق الظاهري للأداة في الملحق المعدّل."
            )
        )

        // Seed feedback for Paper 2
        repository.insertPeerFeedback(
            PeerFeedbackEntity(
                paperId = paper2Id,
                reviewerAlias = "مراجع أكاديمي #512",
                clarityRating = 4,
                methodologyRating = 5,
                argumentationRating = 5,
                clarityComment = "أسلوب الطرح ممتاز، المصطلحات الدقيقة تم شرحها في متن البحث.",
                methodologyComment = "استخدام معايير RMSE و MAE يُعطي رصانة عالية للمقارنة الإحصائية بين النماذج.",
                argumentationComment = "المناقشة العلمية للنتائج مقتنعة وتربط بين النظريات المالية والخوارزميات بشكل دقيق.",
                generalComment = "بحث ذو جودة عالية جاهز للنشر بعد مراجعة التنسيق الشكلي فقط."
            )
        )
    }
}
