package com.c22_pc383.wacayang

import android.content.Intent
import android.net.Uri
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
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.util.Util
import org.apache.commons.lang3.StringEscapeUtils

class DetailsActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityDetailsBinding
    private lateinit var viewModel: WayangViewModel

    private var videoPlayer: ExoPlayer? = null
    private var mWayang: Wayang? = null
    private var wayangId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = resources.getString(R.string.details_title)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) initVideoPlayer()
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (Util.SDK_INT < 24 && videoPlayer == null) initVideoPlayer()
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 24) releaseVideoPlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) releaseVideoPlayer()
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
                    mWayang?.isFavorite = 1
                    setupFavoriteButton(mWayang?.isFavorite == 1)
                }
            }

            isDelFavError.observe(this@DetailsActivity) {
                binding.favoriteBtn.isEnabled = true
                if (it) Utils.toastNetworkError(this@DetailsActivity)
                else {
                    mWayang?.isFavorite = 0
                    setupFavoriteButton(mWayang?.isFavorite == 1)
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
        cueVideoToPlayer()

        binding.youtubeBtn.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mWayang?.video)))
            } catch (e: Exception) {
                Toast.makeText(this@DetailsActivity, resources.getString(R.string.youtube_error_prompt), Toast.LENGTH_SHORT).show()
            }
        }

        binding.imageSlider.adapter = ImageSliderAdapter(this, Utils.splitImageUrls(mWayang?.image!!))
        binding.imageTabLayout.setupWithViewPager(binding.imageSlider)

        binding.itemTitle.text = mWayang?.name
        binding.itemDesc.text = mWayang?.description
        val courtesy = "<b>${resources.getString(R.string.courtesy)}:</b> ${mWayang?.video}"
        Utils.setHtmlText(binding.videoSourceInfo, courtesy)

        binding.commentHeader.text = resources.getString(R.string.comment_plural, mWayang?.totalComments)
        val hasComment = mWayang?.totalComments!! > 0
        if (hasComment) {
            binding.recentComment.text = StringEscapeUtils.unescapeJava(mWayang?.recentComment)
            Glide.with(this)
                .load(mWayang?.commenterPhoto)
                .placeholder(Utils.getCircularProgressDrawable(this))
                .circleCrop()
                .into(binding.commenterPhoto)
        }
        binding.recentCommentPanel.isVisible = hasComment
        binding.viewAllBtn.isVisible = hasComment
        binding.openComment.isVisible = !hasComment

        setupFavoriteButton(mWayang?.isFavorite == 1)
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
        if (mWayang?.isFavorite == 1) viewModel.delFavorite(AppPreference(this).getToken(), mWayang?.id!!)
        else viewModel.addFavorite(AppPreference(this).getToken(), mWayang?.id!!)
    }

    private val launchCommentActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CommentActivity.COMMENT_UPDATE_RESULT_CODE) {
            onLoading()
        }
    }

    private fun openComment() {
        launchCommentActivity.launch(Intent(this, CommentActivity::class.java).apply {
            putExtra(WAYANG_ID_EXTRA, wayangId)
        })
    }

    private fun initVideoPlayer() {
        videoPlayer = ExoPlayer.Builder(this).build()
        binding.videoPlayer.player = videoPlayer
        binding.videoProgressBar.isVisible = true

        cueVideoToPlayer()
    }

    private fun cueVideoToPlayer() {
        if (mWayang == null || videoPlayer == null) return

        val link = "https://raw.githubusercontent.com/Wacayang-Bangkit-2022/Wacayang-Documentation/main/assets/videos/${mWayang!!.name.lowercase()}_video.mp4"
        videoPlayer?.setMediaItem(MediaItem.fromUri(link))
        videoPlayer?.prepare()
        binding.videoProgressBar.isVisible = false
    }

    private fun releaseVideoPlayer() {
        videoPlayer?.release()
        videoPlayer = null
    }

    companion object {
        const val WAYANG_ID_EXTRA = "WAYANG_ID_EXTRA"
        const val DETAILS_RESULT_CODE = 180
    }
}