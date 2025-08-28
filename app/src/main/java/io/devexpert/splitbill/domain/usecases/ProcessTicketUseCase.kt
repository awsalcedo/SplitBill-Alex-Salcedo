package io.devexpert.splitbill.domain.usecases

import io.devexpert.splitbill.data.TicketData
import io.devexpert.splitbill.data.TicketRepository
import javax.inject.Inject

class ProcessTicketUseCase @Inject constructor(private val ticketRepository: TicketRepository) {

    suspend operator fun invoke(imageBytes: ByteArray): TicketData {
        return ticketRepository.processTicket(imageBytes)
    }
}