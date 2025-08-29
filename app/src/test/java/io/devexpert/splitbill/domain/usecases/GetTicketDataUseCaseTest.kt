package io.devexpert.splitbill.domain.usecases

import io.devexpert.splitbill.data.TicketRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTicketDataUseCaseTest {

    private lateinit var repository: TicketRepository
    private lateinit var getTicketDataUseCase: GetTicketDataUseCase

    @Before
    fun setUp() {
        repository = TicketRepository(FakeTicketDataSource())
        getTicketDataUseCase = GetTicketDataUseCase(repository)
    }

    @Test
    fun `invoke returns null when no ticket has been processed`() {
        val result = getTicketDataUseCase()
        assertNull(result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `invoke returns last processed ticket data`() = runTest {
        val processed = repository.processTicket(ByteArray(8))

        val result = getTicketDataUseCase()

        assertTrue(result != null)
        result!!

        assertEquals(21, result.items.size)
        assertEquals(272.20, result.total, 0.001)

        val first = result.items.first()
        assertEquals("PAN", first.name)
        assertEquals(7, first.quantity)
        assertEquals(1.90, first.price, 0.001)

        assertEquals(processed.total, result.total, 0.001)
        assertEquals(processed.items.size, result.items.size)
    }
}