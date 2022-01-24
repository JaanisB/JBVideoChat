package com.example.jbvideochat.di

import android.content.Context
import com.example.jbvideochat.di.videoImplClasses.IRtcEngineEventHandlerImpl
import com.example.jbvideochat.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.agora.rtc.RtcEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AgoraVideoModule {

    @Singleton
    @Provides
    fun provideRtcEngineEventHandler(): IRtcEngineEventHandlerImpl {
        return IRtcEngineEventHandlerImpl()
    }

    @Provides
    @Singleton
    fun provideRtcEngine(
        rtcEngineEventHandlerImpl: IRtcEngineEventHandlerImpl,
        @ApplicationContext context: Context
    ): RtcEngine {
        return RtcEngine.create(context, Constants.APP_ID, rtcEngineEventHandlerImpl)
    }


}