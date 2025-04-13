package com.quangduy.chatapp.di

import com.quangduy.chatapp.datastore.AppSetting
import com.quangduy.chatapp.datastore.AppSettingImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppSettingModule {

    @Binds
    @Singleton
    abstract fun bindAppSetting(
        appSettingImpl: AppSettingImpl
    ): AppSetting
}