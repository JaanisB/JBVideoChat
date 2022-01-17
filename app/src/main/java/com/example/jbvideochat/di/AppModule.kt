package com.example.jbvideochat.di

import android.content.Context
import com.example.jbvideochat.util.Constants
import com.example.jbvideochat.util.RtmClientListnerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtm.RtmClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRtmClientListner() : RtmClientListnerImpl {
        return RtmClientListnerImpl()
    }

    @Singleton
    @Provides
    fun provideRtmClient(@ApplicationContext context: Context, rtmClientListener: RtmClientListnerImpl) : RtmClient {
        return RtmClient.createInstance(context, Constants.APP_ID_TOKEN, rtmClientListener)
    }



}