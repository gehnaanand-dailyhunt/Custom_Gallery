package com.example.mediagallery.videoPlayer

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.exo_player.*

class VideoPlayerViewModel  (application: Application) : AndroidViewModel(application) {
    val context=application.baseContext

    var playbackPosition: Long = 0
    var currentWindow: Int = 0
    var playWhenReady = true
    lateinit var player: SimpleExoPlayer
    var componentListener: ComponentListener? = null
    fun initializePlayer(uri: String) {
        if (uri.isEmpty()) {
            return
        }

        if (!::player.isInitialized) {
            // a factory to create an AdaptiveVideoTrackSelection
            val adaptiveTrackSelectionFactory = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
            player = ExoPlayerFactory.newSimpleInstance(
                context,
                DefaultRenderersFactory(getApplication()),
                DefaultTrackSelector(adaptiveTrackSelectionFactory),
                DefaultLoadControl()
            )

            player.addListener(componentListener)
            player.addAnalyticsListener(componentListener)
            player.addAnalyticsListener(componentListener)
        }



        player.playWhenReady = playWhenReady
        player.seekTo(currentWindow, playbackPosition)


        val firstSource = getMediaSource(uri)
        player.prepare(firstSource, true, false)
    }

    fun playerset()
    { player.setPlayWhenReady(true);
        player.getPlaybackState();

        player.seekTo(currentWindow, playbackPosition)
    }

    fun getMediaSource(uri : String):MediaSource{
        val dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(getApplication(),"Media Gallery"));
        return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource((Uri.parse(uri)))
    }

    fun isPlayerInitialized():Boolean
    {
        return !::player.isInitialized
    }

    fun releasePlayer() {
        playbackPosition = player.currentPosition
        currentWindow = player.currentWindowIndex
        playWhenReady = player.playWhenReady
        player.setPlayWhenReady(false)
        player.getPlaybackState()
    }
}