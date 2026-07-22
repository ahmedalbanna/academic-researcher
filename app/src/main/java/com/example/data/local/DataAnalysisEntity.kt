package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data_analysis")
data class DataAnalysisEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val rawDataValues: String, // Comma separated values or descriptive text
    val meanValue: Double,
    val medianValue: Double,
    val stdDevValue: Double,
    val sampleCount: Int,
    val minValue: Double,
    val maxValue: Double,
    val statisticalSummaryText: String,
    val createdAt: Long = System.currentTimeMillis()
)
