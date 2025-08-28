package io.devexpert.splitbill.domain.usecases

import io.devexpert.splitbill.data.ScanCounterRepository
import javax.inject.Inject

class DecrementScanCounterUseCase @Inject constructor(private val scanCounterRepository: ScanCounterRepository) {
    
    suspend operator fun invoke() {
        scanCounterRepository.decrementScan()
    }
}