package com.example.globalmonitor.di

import android.content.Context
import com.example.globalmonitor.data.remote.MusicDatabase
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped


@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @Provides
    @ServiceScoped
    fun provideAudioAttributes() = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()


    @Provides
    @ServiceScoped
    fun provideExoplayer(
        @ApplicationContext context: Context,
        audioAttributes: AudioAttributes
    ) = SimpleExoPlayer.Builder(context)
    .apply {
        setAudioAttributes(audioAttributes, true)
        setHandleAudioBecomingNoisy(true)
    }.build()


    @Provides
    @ServiceScoped
    fun provideDataSourceFactory(
        @ApplicationContext context: Context,
    ) = DefaultDataSourceFactory(context, Util.getUserAgent(context, "Spotify App"))


    @Provides
    @ServiceScoped
    fun provideMusicDatabase() = MusicDatabase()
}











