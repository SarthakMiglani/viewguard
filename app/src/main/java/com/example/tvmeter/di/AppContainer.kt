package com.example.tvmeter.di

import android.content.Context
import com.example.tvmeter.data.remote.ApiService
import com.example.tvmeter.data.remote.DeviceRepository
import com.example.tvmeter.data.remote.NetworkModule
import com.example.tvmeter.data.token.TokenManager
import com.example.tvmeter.viewmodels.PairingViewModel

class AppContainer(private val context: Context) {
    // Network dependencies
    private val okHttpClient by lazy { NetworkModule.provideOkHttpClient() }
    private val retrofit by lazy { NetworkModule.provideRetrofit(okHttpClient) }
    val apiService: ApiService by lazy { NetworkModule.provideApiService(retrofit) }
    // Token management
    val tokenManager: TokenManager by lazy { TokenManager(context) }
    // Repository
    val deviceRepository: DeviceRepository by lazy {
        DeviceRepository(apiService, tokenManager, context)
    }
    // ViewModels
    fun createPairingViewModel(): PairingViewModel {
        return PairingViewModel(deviceRepository)
    }
} 