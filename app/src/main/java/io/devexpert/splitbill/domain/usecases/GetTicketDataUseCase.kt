package io.devexpert.splitbill.domain.usecases

import io.devexpert.splitbill.data.TicketData
import io.devexpert.splitbill.data.TicketRepository
import javax.inject.Inject

class GetTicketDataUseCase @Inject constructor(private val ticketRepository: TicketRepository) {
    
    operator fun invoke(): TicketData? {
        return ticketRepository.getTicketData()
    }
}