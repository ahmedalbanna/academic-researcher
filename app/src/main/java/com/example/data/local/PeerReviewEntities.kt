package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "peer_papers")
data class PeerPaperEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val academicField: String,
    val abstractText: String,
    val fullDraftText: String,
    val authorAlias: String,
    val status: String = "قيد المراجعة",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "peer_feedbacks")
data class PeerFeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val paperId: Long,
    val reviewerAlias: String,
    val clarityRating: Int,
    val methodologyRating: Int,
    val argumentationRating: Int,
    val clarityComment: String,
    val methodologyComment: String,
    val argumentationComment: String,
    val generalComment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "peer_responses")
data class PeerResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val feedbackId: Long,
    val paperId: Long,
    val responderAlias: String,
    val responseText: String,
    val createdAt: Long = System.currentTimeMillis()
)
