package com.example.data.model

data class QuizQuestion(
    val id: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

data class PracticalExercise(
    val id: Int,
    val title: String,
    val scenario: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val feedbackExplanation: String
)

data class LearningModule(
    val id: String,
    val title: String,
    val subjectArea: String,
    val durationMinutes: Int,
    val videoTitle: String,
    val videoDescription: String,
    val videoDuration: String,
    val keyTakeaways: List<String>,
    val practicalExercise: PracticalExercise,
    val quizQuestions: List<QuizQuestion>
)
