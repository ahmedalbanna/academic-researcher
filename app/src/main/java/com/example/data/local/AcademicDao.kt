package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademicDao {

    // Research Proposals
    @Query("SELECT * FROM research_proposals ORDER BY updatedAt DESC")
    fun getAllProposals(): Flow<List<ResearchProposalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProposal(proposal: ResearchProposalEntity): Long

    @Delete
    suspend fun deleteProposal(proposal: ResearchProposalEntity)

    // Literature Summaries
    @Query("SELECT * FROM literature_summaries ORDER BY createdAt DESC")
    fun getAllSummaries(): Flow<List<LiteratureSummaryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: LiteratureSummaryEntity): Long

    @Delete
    suspend fun deleteSummary(summary: LiteratureSummaryEntity)

    // Citations
    @Query("SELECT * FROM citations ORDER BY createdAt DESC")
    fun getAllCitations(): Flow<List<CitationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCitation(citation: CitationEntity): Long

    @Delete
    suspend fun deleteCitation(citation: CitationEntity)

    // Data Analysis
    @Query("SELECT * FROM data_analysis ORDER BY createdAt DESC")
    fun getAllDataAnalysis(): Flow<List<DataAnalysisEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDataAnalysis(analysis: DataAnalysisEntity): Long

    @Delete
    suspend fun deleteDataAnalysis(analysis: DataAnalysisEntity)

    // Peer Papers
    @Query("SELECT * FROM peer_papers ORDER BY createdAt DESC")
    fun getAllPeerPapers(): Flow<List<PeerPaperEntity>>

    @Query("SELECT * FROM peer_papers WHERE id = :paperId")
    suspend fun getPeerPaperById(paperId: Long): PeerPaperEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeerPaper(paper: PeerPaperEntity): Long

    @Delete
    suspend fun deletePeerPaper(paper: PeerPaperEntity)

    // Peer Feedbacks
    @Query("SELECT * FROM peer_feedbacks WHERE paperId = :paperId ORDER BY createdAt DESC")
    fun getFeedbacksForPaper(paperId: Long): Flow<List<PeerFeedbackEntity>>

    @Query("SELECT * FROM peer_feedbacks ORDER BY createdAt DESC")
    fun getAllPeerFeedbacks(): Flow<List<PeerFeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeerFeedback(feedback: PeerFeedbackEntity): Long

    // Peer Responses
    @Query("SELECT * FROM peer_responses WHERE paperId = :paperId ORDER BY createdAt ASC")
    fun getResponsesForPaper(paperId: Long): Flow<List<PeerResponseEntity>>

    @Query("SELECT * FROM peer_responses ORDER BY createdAt ASC")
    fun getAllPeerResponses(): Flow<List<PeerResponseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeerResponse(response: PeerResponseEntity): Long
}
