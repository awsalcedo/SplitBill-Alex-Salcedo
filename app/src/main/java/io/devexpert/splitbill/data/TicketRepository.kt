package io.devexpert.splitbill.data

import javax.inject.Inject

class TicketRepository @Inject constructor(private val ticketDataSource: TicketDataSource) {

    private var _ticketData: TicketData? = null

    suspend fun processTicket(imageBytes: ByteArray): TicketData {
        val result = ticketDataSource.processTicket(imageBytes)
        _ticketData = result
        return result
    }
    
    fun getTicketData(): TicketData? {
        return _ticketData
    }
}