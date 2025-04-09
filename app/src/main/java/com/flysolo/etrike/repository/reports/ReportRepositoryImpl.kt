package com.flysolo.etrike.repository.reports

import com.flysolo.etrike.models.reports.Reports
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


const val REPORTS_COLLECTION = "reports"
class ReportRepositoryImpl(
    private val firestore: FirebaseFirestore
): ReportRepository {
    override suspend fun submitReport(reports: Reports): Result<String> {
        return try {
            firestore.collection(REPORTS_COLLECTION)
                .document(reports.id!!)
                .set(reports)
                .await()
            Result.success("Successfully Submitted")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }
}