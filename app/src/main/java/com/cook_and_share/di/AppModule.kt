package com.cook_and_share.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.cook_and_share.domain.repository.AuthRepository
import com.cook_and_share.data.repository.AuthRepositoryImpl
import com.cook_and_share.domain.repository.FirestoreRepository
import com.cook_and_share.data.repository.FirestoreRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun provideFirestoreRepository(impl: FirestoreRepositoryImpl): FirestoreRepository = impl
}