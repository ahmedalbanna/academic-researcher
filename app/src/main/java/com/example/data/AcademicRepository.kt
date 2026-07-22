package com.example.data

import com.example.data.local.*
import kotlinx.coroutines.flow.Flow

class AcademicRepository(private val academicDao: AcademicDao) {

    // Research Proposals
    val allProposals: Flow<List<ResearchProposalEntity>> = academicDao.getAllProposals()

    suspend fun insertProposal(proposal: ResearchProposalEntity) {
        academicDao.insertProposal(proposal)
    }

    suspend fun deleteProposal(proposal: ResearchProposalEntity) {
        academicDao.deleteProposal(proposal)
    }

    // Literature Summaries
    val allSummaries: Flow<List<LiteratureSummaryEntity>> = academicDao.getAllSummaries()

    suspend fun insertSummary(summary: LiteratureSummaryEntity) {
        academicDao.insertSummary(summary)
    }

    suspend fun deleteSummary(summary: LiteratureSummaryEntity) {
        academicDao.deleteSummary(summary)
    }

    // Citations
    val allCitations: Flow<List<CitationEntity>> = academicDao.getAllCitations()

    suspend fun insertCitation(citation: CitationEntity) {
        academicDao.insertCitation(citation)
    }

    suspend fun deleteCitation(citation: CitationEntity) {
        academicDao.deleteCitation(citation)
    }

    // Data Analysis
    val allDataAnalysis: Flow<List<DataAnalysisEntity>> = academicDao.getAllDataAnalysis()

    suspend fun insertDataAnalysis(analysis: DataAnalysisEntity) {
        academicDao.insertDataAnalysis(analysis)
    }

    suspend fun deleteDataAnalysis(analysis: DataAnalysisEntity) {
        academicDao.deleteDataAnalysis(analysis)
    }
}
