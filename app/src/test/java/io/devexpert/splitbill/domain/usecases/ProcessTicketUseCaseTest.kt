package io.devexpert.splitbill.domain.usecases

import io.devexpert.splitbill.data.MockTicketDataSource
import io.devexpert.splitbill.data.TicketData
import io.devexpert.splitbill.data.TicketDataSource
import io.devexpert.splitbill.data.TicketRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

private class FakeTicketDataSource : TicketDataSource {
    private val json = Json { ignoreUnknownKeys = true }
    private val MOCK_JSON = """
        {
           "items":[
              {"count":7,"name":"PAN","price_per_unit":1.90},
              {"count":3,"name":"COCA COLA ZERO","price_per_unit":3.75},
              {"count":3,"name":"CAÑA","price_per_unit":4.20},
              {"count":4,"name":"AGUA SOLAN 1/2","price_per_unit":2.70},
              {"count":1,"name":"CROQUETAS DE LACON Y HUEV","price_per_unit":12.00},
              {"count":1,"name":"BUÑUELOS DE BACALAO","price_per_unit":12.50},
              {"count":1,"name":"ALCACHOFAS A LA PLANCHA","price_per_unit":16.00},
              {"count":1,"name":"PATATA RELLENA DE RABO","price_per_unit":16.00},
              {"count":1,"name":"JARRETE DE CERDO ASADO","price_per_unit":20.00},
              {"count":2,"name":"ARROZ NEGRO","price_per_unit":21.00},
              {"count":1,"name":"VERDURAS GUISADAS","price_per_unit":16.00},
              {"count":1,"name":"PIMIENTOS RELLENOS","price_per_unit":14.00},
              {"count":1,"name":"ARROZ SECO CON VERDURAS Y","price_per_unit":18.00},
              {"count":1,"name":"MERLUZA MEUNIER","price_per_unit":22.00},
              {"count":1,"name":"TARTA DE LIMON DE LA MARU","price_per_unit":7.00},
              {"count":1,"name":"TARTA DE QUESO DE CANADIO","price_per_unit":7.00},
              {"count":1,"name":"EL FLAN DE ALEX","price_per_unit":7.00},
              {"count":2,"name":"INFUSION","price_per_unit":3.50},
              {"count":1,"name":"CAFE CON LECHE","price_per_unit":2.75},
              {"count":1,"name":"CAFE SOLO","price_per_unit":2.50},
              {"count":1,"name":"CAFE CORTADO","price_per_unit":2.50}
           ],
           "total":272.20
        }
    """.trimIndent()

    override suspend fun processTicket(imageBytes: ByteArray): TicketData {
        return json.decodeFromString(MOCK_JSON)
    }
}

class ProcessTicketUseCaseTest {

    private lateinit var repository: TicketRepository
    private lateinit var useCase: ProcessTicketUseCase

    @Before
    fun setUp() {
        repository = TicketRepository(FakeTicketDataSource())
        useCase = ProcessTicketUseCase(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `invoke processes ticket with mock data successfully`() = runTest {
        // Arrange
        val dummyImage = ByteArray(16)

        // Act
        val result = useCase(dummyImage)

        // Assert (según el JSON del mock)
        assertTrue(result.items.isNotEmpty())
        assertEquals(21, result.items.size)
        assertEquals(272.20, result.total, 0.001)
    }
}