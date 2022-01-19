package com.example.jbvideochat.di

import android.content.Context
import com.example.jbvideochat.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.agora.rtm.RtmChannelListener
import io.agora.rtm.RtmClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AgoraModule {

    @Singleton
    @Provides
    fun provideRtmClientListener() : RtmClientListnerImpl {
        return RtmClientListnerImpl()
    }

    @Singleton
    @Provides
    fun provideRtmClient(@ApplicationContext context: Context, rtmClientListener: RtmClientListnerImpl) : RtmClient {
        return RtmClient.createInstance(context, Constants.APP_ID_TOKEN, rtmClientListener)
    }

    @Singleton
    @Provides
    fun provideRtmChannelListener() : RtmChannelListenerImpl {
        return RtmChannelListenerImpl()
    }



}