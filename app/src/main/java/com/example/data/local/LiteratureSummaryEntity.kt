package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "literature_summaries")
data class LiteratureSummaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val paperTitle: String,
    val authorsAndYear: String,
    val originalTextOrAbstract: String,
    val summaryFindings: String,
    val methodologyUsed: String,
    val researchGaps: String,
    val createdAt: Long = System.currentTimeMillis()
)
