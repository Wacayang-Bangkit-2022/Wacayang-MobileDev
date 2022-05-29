package com.c22_pc383.wacayang

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.c22_pc383.wacayang.adapter.ImageSliderAdapter
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.databinding.ActivityDetailsBinding
import com.c22_pc383.wacayang.factory.WayangViewModelFactory
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.WayangViewModel
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YoutubeFragmentX

class DetailsActivity : AppCompatActivity(), IGeneralSetup, YouTubePlayer.OnInitializedListener {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: WayangViewModel

    private var mYouTubePlayer: YouTubePlayer? = null
    private var isFullscreen = false
    private lateinit var mWayang: Wayang
    private var wayangId = -1

    private val launchCommentActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CommentActivity.COMMENT_UPDATE_RESULT_CODE) {
            onLoading()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = resources.getString(R.string.details_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        setResult(DETAILS_RESULT_CODE)

        viewModel = ViewModelProvider(
            this, WayangViewModelFactory(WayangRepository.getDefaultRepository())
        )[WayangViewModel::class.java]

        wayangId = intent.getIntExtra(WAYANG_ID_EXTRA, -1)
        if (wayangId == -1) onBackPressed()

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

    override fun onResume() {
        super.onResume()
        mYouTubePlayer?.pause()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onPause() {
        if (isFullscreen) mYouTubePlayer?.setFullscreen(!isFullscreen)
        super.onPause()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        mYouTubePlayer?.pause()
    }

    override fun setup() {
        onLoading()

        binding.openComment.setOnClickListener { openComment() }
        binding.viewAllBtn.setOnClickListener { openComment() }
        binding.recentCommentPanel.setOnClickListener { openComment() }

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
        viewModel.apply {
            detailWayang.observe(this@DetailsActivity) {
                mWayang = it
                onSuccess()
            }

            isGetDetailWayangError.observe(this@DetailsActivity) {
                binding.progressBar.isVisible = false
                if (it) { onError() }
            }

            isAddFavError.observe(this@DetailsActivity) {
                binding.favoriteBtn.isEnabled = true
                if (it) Utils.toastNetworkError(this@DetailsActivity)
                else {
                    mWayang.isFavorite = 1
                    setupFavoriteButton(mWayang.isFavorite == 1)
                }
            }

            isDelFavError.observe(this@DetailsActivity) {
                binding.favoriteBtn.isEnabled = true
                if (it) Utils.toastNetworkError(this@DetailsActivity)
                else {
                    mWayang.isFavorite = 0
                    setupFavoriteButton(mWayang.isFavorite == 1)
                }
            }
        }
    }

    private fun onLoading() {
        viewModel.getWayangDetail(AppPreference(this).getToken(), wayangId.toString())

        binding.progressBar.isVisible = true
        binding.mainView.isVisible = false
        binding.errorView.isVisible = false
    }

    private fun onError() {
        Utils.toastNetworkError(this)
        binding.mainView.isVisible = false
        binding.errorView.isVisible = true
    }

    private fun onSuccess() {
        val key = applicationContext.packageManager
            .getApplicationInfo(
                applicationContext.packageName,
                PackageManager.GET_META_DATA
            ).metaData["api_key"].toString()

        (supportFragmentManager.findFragmentById(R.id.youtube_fragment) as YoutubeFragmentX)
            .initialize(key, this)


        binding.imageSlider.adapter = ImageSliderAdapter(this, Utils.splitImageUrls(mWayang.image))
        binding.imageTabLayout.setupWithViewPager(binding.imageSlider)

        binding.itemTitle.text = mWayang.name
        binding.itemDesc.text = mWayang.description
        val courtesy = "<b>${resources.getString(R.string.courtesy)}:</b> ${mWayang.video}"
        Utils.setHtmlText(binding.videoSourceInfo, courtesy)

        binding.commentHeader.text = resources.getString(R.string.comment_plural, mWayang.totalComments)
        val hasComment = mWayang.totalComments > 0
        if (hasComment) {
            binding.recentComment.text = mWayang.recentComment
            Glide.with(this)
                .load(mWayang.commenterPhoto)
                .placeholder(Utils.getCircularProgressDrawable(this))
                .circleCrop()
                .into(binding.commenterPhoto)
        }
        binding.recentCommentPanel.isVisible = hasComment
        binding.viewAllBtn.isVisible = hasComment
        binding.openComment.isVisible = !hasComment

        setupFavoriteButton(mWayang.isFavorite == 1)
        binding.favoriteBtn.setOnClickListener { toggleFavorite() }

        binding.mainView.isVisible = true
        binding.errorView.isVisible = false
    }

    private fun setupFavoriteButton(isFavorite: Boolean) {
        binding.favoriteBtn.setImageResource(
            if (isFavorite) R.drawable.ic_baseline_favorite_24
            else R.drawable.ic_baseline_favorite_border_24
        )
    }

    private fun toggleFavorite() {
        if (Utils.isCurrentUserAnonymous()) {
            Toast.makeText(this, resources.getString(R.string.require_login_prompt), Toast.LENGTH_SHORT).show()
            return
        }

        binding.favoriteBtn.isEnabled = false
        if (mWayang.isFavorite == 1) viewModel.delFavorite(AppPreference(this).getToken(), mWayang.id)
        else viewModel.addFavorite(AppPreference(this).getToken(), mWayang.id)
    }

    private fun openComment() {
        launchCommentActivity.launch(Intent(this, CommentActivity::class.java).apply {
            putExtra(WAYANG_ID_EXTRA, wayangId)
        })
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
        const val DETAILS_RESULT_CODE = 180
    }
}