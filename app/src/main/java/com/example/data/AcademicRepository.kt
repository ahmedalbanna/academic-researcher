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

    // Peer Review Papers
    val allPeerPapers: Flow<List<PeerPaperEntity>> = academicDao.getAllPeerPapers()
    val allPeerFeedbacks: Flow<List<PeerFeedbackEntity>> = academicDao.getAllPeerFeedbacks()
    val allPeerResponses: Flow<List<PeerResponseEntity>> = academicDao.getAllPeerResponses()

    fun getFeedbacksForPaper(paperId: Long): Flow<List<PeerFeedbackEntity>> = academicDao.getFeedbacksForPaper(paperId)
    fun getResponsesForPaper(paperId: Long): Flow<List<PeerResponseEntity>> = academicDao.getResponsesForPaper(paperId)

    suspend fun insertPeerPaper(paper: PeerPaperEntity): Long {
        return academicDao.insertPeerPaper(paper)
    }

    suspend fun deletePeerPaper(paper: PeerPaperEntity) {
        academicDao.deletePeerPaper(paper)
    }

    suspend fun insertPeerFeedback(feedback: PeerFeedbackEntity): Long {
        return academicDao.insertPeerFeedback(feedback)
    }

    suspend fun insertPeerResponse(response: PeerResponseEntity): Long {
        return academicDao.insertPeerResponse(response)
    }
}
