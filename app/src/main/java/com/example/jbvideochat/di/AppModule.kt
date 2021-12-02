package com.example.jbvideochat.di

import android.content.Context
import com.example.jbvideochat.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppIdString () : String = "29a794ea6ffd4eff913b98c00fdb9546"


    @Provides
    @Singleton
    fun providemRtcEventHandler() : IRtcEngineEventHandler {
        return object:  IRtcEngineEventHandler() {
            override fun onUserJoined(uid: Int, elapsed: Int) {
                super.onUserJoined(uid, elapsed)
            }
        }
    }

    @Provides
    // To get application context use annotation @ApplicationContext
    fun provideAgoraRtcEngine(@ApplicationContext context: Context, appId: String, iRtcEngineEventHandler: IRtcEngineEventHandler) : RtcEngine {
        return RtcEngine.create(context, Constants.APP_ID, iRtcEngineEventHandler )
    }


}