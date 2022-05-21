package com.c22_pc383.wacayang

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.c22_pc383.wacayang.adapter.GridSpacingItemDecoration
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.databinding.ActivitySearchBinding
import com.c22_pc383.wacayang.factory.WayangViewModelFactory
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.WayangViewModel

class SearchActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: WayangViewModel

    private var searchView: SearchView? = null
    private var isViewAll = false
    private var listItem = ArrayList<Wayang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            title = resources.getString(R.string.search)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        viewModel = ViewModelProvider(
            this, WayangViewModelFactory(WayangRepository.getDefaultRepository())
        )[WayangViewModel::class.java]

        binding.gridRv.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
            setItemViewCacheSize(Utils.MAX_VIEW_CACHE)
        }

        if (intent.getBooleanExtra(VIEW_ALL_EXTRA, false)) viewAll()

        val initKeyword = intent.getStringExtra(INITIAL_SEARCH_KEYWORD)
        if (initKeyword != null && initKeyword.isNotEmpty()) search(initKeyword)

        setup()
        observerCall()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (menu.findItem(R.id.search)?.actionView as SearchView)
            .apply {
                queryHint = resources.getString(R.string.search)
                setSearchableInfo(searchManager.getSearchableInfo(componentName))
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            clearFocus()
                            search(query)
                        }
                        return true
                    }
                    override fun onQueryTextChange(newText: String?): Boolean = false
                })
            }

        return true
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
        binding.viewAllBtn.setOnClickListener { viewAll() }
    }

    override fun observerCall() {
        viewModel.apply {
            foundWayang.observe(this@SearchActivity) { list ->
                listItem.clear()
                listItem.addAll(list)
            }
            isFindWayangError.observe(this@SearchActivity){
                onEndLoading()
                if (it) {
                    Utils.toastNetworkError(this@SearchActivity)
                }
            }
        }
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.viewAllBtn.visibility = if (!isEnabled) View.VISIBLE else View.INVISIBLE
        binding.foundText.text =
            if (isEnabled) resources.getString(R.string.all_item)
            else resources.getString(R.string.search_found, listItem.size)
    }

    private fun search(keyword: String) {
        isViewAll = false
        viewModel.findWayang(AppPreference(this).getToken(), keyword)

        onLoading()
    }

    private fun viewAll() {
        isViewAll = true
        searchView?.apply {
            isIconified = true
            clearFocus()
        }
        viewModel.findWayang(AppPreference(this).getToken(), "", true)

        onLoading()
    }

    private fun onLoading() {
        binding.searchIcon.isVisible = false
        binding.searchPanel.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun onEndLoading() {
        binding.searchPanel.isVisible = true
        binding.progressBar.isVisible = false
        enableControl(isViewAll)
        Utils.setupGridListView(this@SearchActivity, binding.gridRv, listItem, binding.errorView, binding.gridRv)
    }

    companion object {
        const val VIEW_ALL_EXTRA = "view_all_extra"
        const val INITIAL_SEARCH_KEYWORD = "initial_search_keyword"
    }
}