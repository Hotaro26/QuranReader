package com.hotaro.quranreader.di

import com.hotaro.quranreader.data.remote.QuranApiService
import com.hotaro.quranreader.data.remote.ExternalApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(QuranApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideQuranApiService(retrofit: Retrofit): QuranApiService {
        return retrofit.create(QuranApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideExternalApiService(retrofit: Retrofit): ExternalApiService {
        return retrofit.create(ExternalApiService::class.java)
    }
}
