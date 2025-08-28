package io.devexpert.splitbill.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScanCounterRepository @Inject constructor(private val scanCounterDataSource: ScanCounterDataSource) {
    
    val scansRemaining: Flow<Int> = scanCounterDataSource.scansRemaining
    
    suspend fun initializeOrResetIfNeeded() {
        scanCounterDataSource.initializeOrResetIfNeeded()
    }
    
    suspend fun decrementScan() {
        scanCounterDataSource.decrementScan()
    }
}