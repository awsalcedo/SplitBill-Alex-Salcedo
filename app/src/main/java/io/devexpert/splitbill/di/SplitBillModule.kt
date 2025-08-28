package io.devexpert.splitbill.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.devexpert.splitbill.BuildConfig
import io.devexpert.splitbill.data.DataStoreScanCounterDataSource
import io.devexpert.splitbill.data.MLKitTicketDataSource
import io.devexpert.splitbill.data.MockTicketDataSource
import io.devexpert.splitbill.data.ScanCounterDataSource
import io.devexpert.splitbill.data.TicketDataSource
import io.devexpert.splitbill.data.TicketRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SplitBillModule {

    @Provides
    @Singleton
    fun provideTicketDataSource(): TicketDataSource {
        return if (BuildConfig.DEBUG) {
            MockTicketDataSource()
        } else {
            MLKitTicketDataSource()
        }
    }

    @Provides
    @Singleton
    fun provideScanCounterDataSource(@ApplicationContext context: Context): ScanCounterDataSource {
        return DataStoreScanCounterDataSource(context)
    }

    @Provides
    @Singleton
    fun provideTicketRepository(dataSource: TicketDataSource): TicketRepository =
        TicketRepository(dataSource)
}