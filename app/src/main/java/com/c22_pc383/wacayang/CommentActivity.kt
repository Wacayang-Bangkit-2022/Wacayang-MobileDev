package com.c22_pc383.wacayang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22_pc383.wacayang.adapter.ListCommentAdapter
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.data.Comment
import com.c22_pc383.wacayang.databinding.ActivityCommentBinding
import com.c22_pc383.wacayang.factory.WayangViewModelFactory
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.WayangViewModel

class CommentActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityCommentBinding
    private lateinit var viewModel: WayangViewModel
    private var listItem = ArrayList<Comment>()
    private var wayangId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "Comments"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        binding.commentRv.apply {
            layoutManager = LinearLayoutManager(context)
            setItemViewCacheSize(Utils.MAX_VIEW_CACHE)
        }

        viewModel = ViewModelProvider(
            this, WayangViewModelFactory(WayangRepository.getDefaultRepository())
        )[WayangViewModel::class.java]

        wayangId = intent.getIntExtra(DetailsActivity.WAYANG_ID_EXTRA, -1)
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

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setup() {
        binding.commentButton.setOnClickListener { addComment() }
        binding.refreshBtn.setOnClickListener { refreshComment() }
        refreshComment()
    }

    override fun observerCall() {
        viewModel.apply {
            isAddCommentError.observe(this@CommentActivity) {
                if (it) {
                    enableControl(true)
                    Utils.toastNetworkError(this@CommentActivity)
                }
                else {
                    binding.commentField.text.clear()
                    setResult(COMMENT_UPDATE_RESULT_CODE)
                    refreshComment()
                }
            }

            isDelCommentError.observe(this@CommentActivity) {
                if (it) {
                    enableControl(true)
                    Utils.toastNetworkError(this@CommentActivity)
                }
                else {
                    setResult(COMMENT_UPDATE_RESULT_CODE)
                    refreshComment()
                }
            }

            listComment.observe(this@CommentActivity) { list ->
                listItem.clear()
                listItem.addAll(list)
            }

            isGetCommentError.observe(this@CommentActivity) {
                enableControl(true)
                if (it) { Utils.toastNetworkError(this@CommentActivity) }
                else { setupCommentListView() }
            }
        }
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.progressBar.isVisible = !isEnabled

        if (Utils.isCurrentUserAnonymous()) {
            binding.commentField.hint = resources.getString(R.string.require_login_prompt)
            binding.commentField.isEnabled = false
            binding.commentButton.isEnabled = false
        } else {
            binding.commentField.isEnabled = isEnabled
            binding.commentButton.isEnabled = isEnabled
        }
    }

    private fun addComment() {
        val content = binding.commentField.text.toString()
        if (content.isNotEmpty()) {
            enableControl(false)
            viewModel.addComment(AppPreference(this).getToken(), wayangId, content)
        }
    }

    private fun refreshComment() {
        enableControl(false)
        binding.errorView.isVisible = false
        viewModel.getComments(AppPreference(this).getToken(), wayangId)
    }

    private fun setupCommentListView() {
        if (listItem.isEmpty()) {
            binding.errorView.isVisible = true
            binding.commentRv.isVisible = false
        } else {
            binding.errorView.isVisible = false
            binding.commentRv.isVisible = true

            binding.commentRv.adapter = ListCommentAdapter(listItem).apply {
                setOnItemClickCallback(object : ListCommentAdapter.OnItemClickCallback {
                    override fun onItemClicked(item: Comment, position: Int) {
                        promptDeleteCommentDialogue(item.commentId)
                    }
                })
            }
        }
    }

    private fun promptDeleteCommentDialogue(commentId: Int) {
        AlertDialog.Builder(this).apply {
            setTitle(getString(R.string.confirm_deletion))
            setMessage(getString(R.string.delete_comment_confirmation))
            setNegativeButton(getString(R.string.no), null)
            setPositiveButton(getString(R.string.yes)) { _, _ ->
                enableControl(false)
                viewModel.delComment(AppPreference(this@CommentActivity).getToken(), commentId)
            }
        }.show()
    }

    companion object {
        const val COMMENT_UPDATE_RESULT_CODE = 176
    }
}