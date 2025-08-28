package io.devexpert.splitbill.domain.usecases

import io.devexpert.splitbill.data.ScanCounterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetScansRemainingUseCase @Inject constructor(private val scanCounterRepository: ScanCounterRepository) {
    
    operator fun invoke(): Flow<Int> {
        return scanCounterRepository.scansRemaining
    }
}