package com.c22_pc383.wacayang

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.c22_pc383.wacayang.adapter.ImageSliderAdapter
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.databinding.ActivityDetailsBinding
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.view_model.FavoriteViewModel
import com.c22_pc383.wacayang.factory.FavoriteViewModelFactory
import com.c22_pc383.wacayang.factory.WayangViewModelFactory
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.WayangViewModel
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YoutubeFragmentX

class DetailsActivity : AppCompatActivity(), IGeneralSetup, YouTubePlayer.OnInitializedListener {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var favViewModel: FavoriteViewModel
    private lateinit var wayangViewModel: WayangViewModel

    private var tIsFavorite = false
    private var mYouTubePlayer: YouTubePlayer? = null
    private var isFullscreen = false
    private lateinit var mWayang: Wayang

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = resources.getString(R.string.details_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        wayangViewModel = ViewModelProvider(
            this, WayangViewModelFactory(WayangRepository.getRepository())
        )[WayangViewModel::class.java]

        favViewModel = ViewModelProvider(
            this, FavoriteViewModelFactory(application)
        )[FavoriteViewModel::class.java]

        setup()
        observerCall()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (isFullscreen) mYouTubePlayer?.setFullscreen(!isFullscreen)
        else super.onBackPressed()
    }

    override fun onPause() {
        if (isFullscreen) mYouTubePlayer?.setFullscreen(!isFullscreen)
        super.onPause()
    }

    override fun setup() {
        onLoading()
        binding.refreshBtn.setOnClickListener { onLoading() }
        binding.showMoreBtn.let { btn ->
            btn.setOnClickListener {
                binding.itemDesc.apply {
                    if (maxLines < Int.MAX_VALUE) {
                        maxLines = Int.MAX_VALUE
                        btn.text = resources.getString(R.string.show_less)
                    } else {
                        maxLines = resources.getInteger(R.integer.show_less_max_lines)
                        btn.text = resources.getString(R.string.show_more)
                    }
                }
            }
        }
    }

    override fun observerCall() {
        wayangViewModel.apply {
            detailWayang.observe(this@DetailsActivity) {
                mWayang = it
                onSuccess()
            }

            isGetDetailWayangError.observe(this@DetailsActivity) {
                binding.progressBar.isVisible = false
                if (it) { onError() }
            }
        }
    }

    private fun onLoading() {
        val id = intent.getIntExtra(WAYANG_ID_EXTRA, -1)
        wayangViewModel.getWayangDetail(id.toString())

        binding.progressBar.isVisible = true
        binding.mainView.isVisible = false
        binding.errorView.isVisible = false
    }

    private fun onError() {
        Toast.makeText(
            this@DetailsActivity,
            resources.getString(R.string.network_error),
            Toast.LENGTH_SHORT
        ).show()

        binding.mainView.isVisible = false
        binding.errorView.isVisible = true
    }

    private fun onSuccess() {
        (supportFragmentManager.findFragmentById(R.id.youtube_fragment) as YoutubeFragmentX)
            .initialize(BuildConfig.API_KEY, this)

        binding.imageSlider.adapter = ImageSliderAdapter(this, Utils.splitImageUrls(mWayang.image))
        binding.imageTabLayout.setupWithViewPager(binding.imageSlider)

        binding.itemTitle.text = mWayang.name
        binding.itemDesc.text = mWayang.description

        setupFavoriteFeature()
        binding.favoriteBtn.setOnClickListener { toggleFavorite(mWayang) }

        binding.mainView.isVisible = true
        binding.errorView.isVisible = false
    }

    private fun setupFavoriteFeature() {
        favViewModel.isAFavorite(mWayang.id).observe(this) {
            setupFavoriteButton(it > 0)
        }
    }

    private fun setupFavoriteButton(isFavorite: Boolean) {
        tIsFavorite = isFavorite
        binding.favoriteBtn.setImageResource(
            if (isFavorite) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    private fun toggleFavorite(item: Wayang) {
        if (tIsFavorite) favViewModel.deleteFavorite(item)
        else favViewModel.insertFavorite(item)
    }

    override fun onInitializationSuccess(
        provider: YouTubePlayer.Provider?,
        youTubePlayer: YouTubePlayer, b: Boolean
    ) {
        if (mYouTubePlayer == null) {
            mYouTubePlayer = youTubePlayer.apply {
                setOnFullscreenListener { isFullscreen = it }
                cueVideo(Utils.getYoutubeVideoId(mWayang.video))
            }
        }
    }

    override fun onInitializationFailure(
        provider: YouTubePlayer.Provider?,
        youTubeInitializationResult: YouTubeInitializationResult?
    ) {
        Toast.makeText(
            this@DetailsActivity,
            getString(R.string.video_player_failure),
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val WAYANG_ID_EXTRA = "WAYANG_ID_EXTRA"
    }
}