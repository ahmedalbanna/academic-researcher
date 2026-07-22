package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "citations")
data class CitationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sourceType: String, // "كتاب", "مجلة علمية", "موقع إلكتروني", "أطروحة"
    val authors: String,
    val title: String,
    val year: String,
    val publisherOrJournal: String,
    val doiOrUrl: String,
    val formattedApa: String,
    val formattedIeee: String,
    val formattedMla: String,
    val createdAt: Long = System.currentTimeMillis()
)
