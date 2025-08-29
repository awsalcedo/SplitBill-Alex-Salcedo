package io.devexpert.splitbill.domain.usecases

import io.devexpert.splitbill.data.ScanCounterDataSource
import io.devexpert.splitbill.data.ScanCounterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Fake que implementa lógica de inicialización/reset:
 * - Si el valor actual <= 0, lo resetea a [defaultValue].
 * - Si el valor actual > 0, no hace nada.
 */
private class FakeScanCounterDataSourceWithReset(
    initial: Int = 0,
    private val defaultValue: Int = 3
) : ScanCounterDataSource {

    private val _scansRemaining = MutableStateFlow(initial)
    override val scansRemaining: Flow<Int> = _scansRemaining

    override suspend fun initializeOrResetIfNeeded() {
        if (_scansRemaining.value <= 0) {
            _scansRemaining.value = defaultValue
        }
    }

    override suspend fun decrementScan() {
        _scansRemaining.value = (_scansRemaining.value - 1).coerceAtLeast(0)
    }
}

class InitializeOrResetScanCounterUseCaseTest {

    private lateinit var repository: ScanCounterRepository
    private lateinit var ds: FakeScanCounterDataSourceWithReset

    @Before
    fun setUp() {
        ds = FakeScanCounterDataSourceWithReset(initial = 0, defaultValue = 3)
        repository = ScanCounterRepository(ds)
    }

    @Test
    fun `initializeOrResetIfNeeded sets default when current is zero or below`() = runTest {
        assertEquals(0, repository.scansRemaining.first())

        repository.initializeOrResetIfNeeded()

        val after = repository.scansRemaining.first()
        assertEquals(3, after)
    }

    @Test
    fun `initializeOrResetIfNeeded does nothing when current is positive`() = runTest {
        val dsPos = FakeScanCounterDataSourceWithReset(initial = 5, defaultValue = 3)
        val repoPos = ScanCounterRepository(dsPos)

        assertEquals(5, repoPos.scansRemaining.first())

        repoPos.initializeOrResetIfNeeded()

        val after = repoPos.scansRemaining.first()
        assertEquals(5, after) // sin cambios
    }

    @Test
    fun `after decrementing to zero initializeOrResetIfNeeded resets to default`() = runTest {
        repository.initializeOrResetIfNeeded()
        assertEquals(3, repository.scansRemaining.first())

        repository.decrementScan()
        repository.decrementScan()
        repository.decrementScan()
        assertEquals(0, repository.scansRemaining.first())

        repository.initializeOrResetIfNeeded()
        assertEquals(3, repository.scansRemaining.first())
    }
}