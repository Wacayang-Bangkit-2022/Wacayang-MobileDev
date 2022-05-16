package com.google.android.youtube.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.youtube.player.internal.ab

class YoutubeFragmentX : Fragment(), YouTubePlayer.Provider {
    private val playerViewX: PlayerViewX = PlayerViewX()

    private var bundle: Bundle? = null
    private var devKey: String? = null

    private var playerView: YouTubePlayerView? = null
    private var initListener: YouTubePlayer.OnInitializedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bundle = savedInstanceState?.getBundle(KEY_STATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        playerView = YouTubePlayerView(requireContext(), null, 0, playerViewX)
        setup()
        return playerView
    }

    override fun onStart() {
        super.onStart()
        playerView?.a()
    }

    override fun onResume() {
        super.onResume()
        playerView?.b()
    }

    override fun onPause() {
        playerView?.c()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(KEY_STATE, if (playerView != null) playerView!!.e() else bundle!!)
    }

    override fun onStop() {
        playerView?.d()
        super.onStop()
    }

    override fun onDestroyView() {
        playerView?.c(requireActivity().isFinishing)
        playerView = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        playerView?.b(requireActivity().isFinishing)
        super.onDestroy()
    }

    override fun initialize(devKey: String?, initListener: YouTubePlayer.OnInitializedListener?) {
        this.devKey = ab.a(devKey, "Developer key cannot be null or empty")
        this.initListener = initListener
        setup()
    }

    private fun setup() {
        if (playerView != null && initListener != null) {
            playerView!!.a(requireActivity(), this, devKey, initListener, bundle)
            bundle = null
            initListener = null
        }
    }

    private inner class PlayerViewX : YouTubePlayerView.b {
        override fun a(
            playerView: YouTubePlayerView?,
            devKey: String?,
            initListener: YouTubePlayer.OnInitializedListener?
        ) { initialize(devKey, this@YoutubeFragmentX.initListener!!) }

        override fun a(playerView: YouTubePlayerView?) { }
    }

    companion object {
        const val KEY_STATE = "YouTubePlayerSupportFragment.KEY_PLAYER_VIEW_STATE"
    }
}