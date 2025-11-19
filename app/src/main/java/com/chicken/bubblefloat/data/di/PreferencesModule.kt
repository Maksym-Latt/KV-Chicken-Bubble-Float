package com.chicken.bubblefloat.data.di

import android.content.Context
import android.content.SharedPreferences
import com.chicken.bubblefloat.data.progress.ProgressRepository
import com.chicken.bubblefloat.data.progress.ProgressRepositoryImpl
import com.chicken.bubblefloat.data.settings.SettingsRepository
import com.chicken.bubblefloat.data.settings.SettingsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsDataModule {

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        impl: ProgressRepositoryImpl
    ): ProgressRepository
}