package io.devexpert.splitbill.ui.screens.home

import android.graphics.Bitmap
import io.devexpert.splitbill.domain.usecases.DecrementScanCounterUseCase
import io.devexpert.splitbill.domain.usecases.GetScansRemainingUseCase
import io.devexpert.splitbill.domain.usecases.InitializeScanCounterUseCase
import io.devexpert.splitbill.domain.usecases.ProcessTicketUseCase
import io.devexpert.splitbill.ui.ImageConverter
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


/**
 * Regla de JUnit4 para usar un TestDispatcher como Dispatchers.Main en tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE, sdk = [33])
class HomeViewModelTest {

    @get:Rule
    val mainRule = MainDispatcherRule()

    @Test
    fun `init collects scans and updates scansLeft`() = runTest {
        // Arrange
        val scansFlow = MutableStateFlow(0)
        val processTicketUseCase = mockk<ProcessTicketUseCase>(relaxed = true)
        val initializeUseCase = mockk<InitializeScanCounterUseCase>()
        val decrementUseCase = mockk<DecrementScanCounterUseCase>()
        val getScansUseCase = mockk<GetScansRemainingUseCase>()

        coJustRun { initializeUseCase.invoke() }
        every { getScansUseCase.invoke() } returns scansFlow

        // Act
        val vm = HomeViewModel(
            processTicketUseCase = processTicketUseCase,
            initializeScanCounterUseCase = initializeUseCase,
            getScansRemainingUseCase = getScansUseCase,
            decrementScanCounterUseCase = decrementUseCase
        )
        // Emite nuevo valor
        scansFlow.value = 5
        advanceUntilIdle()

        // Assert
        assertEquals(5, vm.uiState.value.scansLeft)
        coVerify(exactly = 1) { initializeUseCase.invoke() }
        coVerify { getScansUseCase.invoke() }
    }

    @Test
    fun `processTicket success sets flags and calls use cases`() = runTest {
        // Arrange
        val scansFlow = MutableStateFlow(3)
        val processTicketUseCase = mockk<ProcessTicketUseCase>()
        val initializeUseCase = mockk<InitializeScanCounterUseCase>()
        val decrementUseCase = mockk<DecrementScanCounterUseCase>()
        val getScansUseCase = mockk<GetScansRemainingUseCase>()

        coJustRun { initializeUseCase.invoke() }
        every { getScansUseCase.invoke() } returns scansFlow
        coJustRun { processTicketUseCase.invoke(any()) }
        coJustRun { decrementUseCase.invoke() }

        mockkObject(ImageConverter)
        every { ImageConverter.toResizedByteArray(any()) } returns byteArrayOf(1, 2, 3)

        val vm = HomeViewModel(
            processTicketUseCase = processTicketUseCase,
            initializeScanCounterUseCase = initializeUseCase,
            getScansRemainingUseCase = getScansUseCase,
            decrementScanCounterUseCase = decrementUseCase
        )

        val fakeBitmap = mockk<Bitmap>(relaxed = true)

        // Act
        vm.processTicket(fakeBitmap)
        advanceUntilIdle()

        // Assert estado
        val state = vm.uiState.value
        assertEquals(false, state.isProcessing)
        assertEquals(true, state.ticketProcessed)
        assertNull(state.errorMessage)

        // Assert llamadas
        coVerify(exactly = 1) { ImageConverter.toResizedByteArray(fakeBitmap) }
        coVerify(exactly = 1) { processTicketUseCase.invoke(byteArrayOf(1, 2, 3)) }
        coVerify(exactly = 1) { decrementUseCase.invoke() }
    }

    @Test
    fun `processTicket failure sets errorMessage and not ticketProcessed`() = runTest {
        // Arrange
        val scansFlow = MutableStateFlow(1)
        val processTicketUseCase = mockk<ProcessTicketUseCase>()
        val initializeUseCase = mockk<InitializeScanCounterUseCase>()
        val decrementUseCase = mockk<DecrementScanCounterUseCase>()
        val getScansUseCase = mockk<GetScansRemainingUseCase>()

        coJustRun { initializeUseCase.invoke() }
        every { getScansUseCase.invoke() } returns scansFlow

        mockkObject(ImageConverter)
        every { ImageConverter.toResizedByteArray(any()) } returns byteArrayOf(9)
        coEvery { processTicketUseCase.invoke(any()) } throws RuntimeException("boom")

        val vm = HomeViewModel(
            processTicketUseCase = processTicketUseCase,
            initializeScanCounterUseCase = initializeUseCase,
            getScansRemainingUseCase = getScansUseCase,
            decrementScanCounterUseCase = decrementUseCase
        )

        val fakeBitmap = mockk<Bitmap>(relaxed = true)

        // Act
        vm.processTicket(fakeBitmap)
        advanceUntilIdle()

        // Assert estado
        val state = vm.uiState.value
        assertEquals(false, state.isProcessing)
        assertEquals(false, state.ticketProcessed)
        assertEquals("boom", state.errorMessage)

        // Assert llamadas
        coVerify(exactly = 1) { ImageConverter.toResizedByteArray(fakeBitmap) }
        coVerify(exactly = 1) { processTicketUseCase.invoke(byteArrayOf(9)) }
        // 👇 Corrección: en vez de "wasNot Called"
        coVerify(exactly = 0) { decrementUseCase.invoke() }
    }

    @Test
    fun `resetTicketProcessed sets flag to false`() = runTest {
        val scansFlow = MutableStateFlow(0)
        val processTicketUseCase = mockk<ProcessTicketUseCase>(relaxed = true)
        val initializeUseCase = mockk<InitializeScanCounterUseCase>()
        val decrementUseCase = mockk<DecrementScanCounterUseCase>(relaxed = true)
        val getScansUseCase = mockk<GetScansRemainingUseCase>()

        coJustRun { initializeUseCase.invoke() }
        every { getScansUseCase.invoke() } returns scansFlow

        val vm = HomeViewModel(
            processTicketUseCase = processTicketUseCase,
            initializeScanCounterUseCase = initializeUseCase,
            getScansRemainingUseCase = getScansUseCase,
            decrementScanCounterUseCase = decrementUseCase
        )

        // Simula que ya quedó en true
        vm.resetTicketProcessed() // primero asegúrate false
        val pre = vm.uiState.value.ticketProcessed
        assertEquals(false, pre)

        // Fuerza a true y luego resetea
        vm.apply {
            // Accesamos el método expuesto para cambiar el flag (vía flujo real)
            // simulando un éxito
            resetTicketProcessed() // ya false
        }
        // Marquemos true simulando un éxito real:
        mockkObject(ImageConverter)
        every { ImageConverter.toResizedByteArray(any()) } returns byteArrayOf(1)
        coJustRun { processTicketUseCase.invoke(any()) }

        val fakeBitmap = mockk<Bitmap>(relaxed = true)
        vm.processTicket(fakeBitmap)
        advanceUntilIdle()
        assertEquals(true, vm.uiState.value.ticketProcessed)

        vm.resetTicketProcessed()
        assertEquals(false, vm.uiState.value.ticketProcessed)
    }
}