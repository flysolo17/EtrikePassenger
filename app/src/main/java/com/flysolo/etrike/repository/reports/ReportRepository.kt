package com.flysolo.etrike.repository.reports

import com.flysolo.etrike.models.reports.Reports


interface ReportRepository {
    suspend fun submitReport(
        reports: Reports
    ) : Result<String>


}