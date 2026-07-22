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
}
