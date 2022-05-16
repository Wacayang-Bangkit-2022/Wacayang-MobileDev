package com.c22_pc383.wacayang

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.c22_pc383.wacayang.adapter.GridSpacingItemDecoration
import com.c22_pc383.wacayang.databinding.FragmentFavoriteBinding
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.view_model.FavoriteViewModel
import com.c22_pc383.wacayang.factory.FavoriteViewModelFactory
import com.c22_pc383.wacayang.view_model.FavoriteViewModel.Companion.SortType

class FavoriteFragment : Fragment(), IGeneralSetup {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: FavoriteViewModel
    private var searchView: SearchView? = null
    private var searchQuery: String = ""
    private var isViewAll = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(
            this, FavoriteViewModelFactory(requireActivity().application)
        )[FavoriteViewModel::class.java]

        binding.gridRv.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
            setItemViewCacheSize(Utils.MAX_VIEW_CACHE)
        }

        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.favorite_menu, menu)

        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (menu.findItem(R.id.search)?.actionView as SearchView)
            .apply {
                queryHint = resources.getString(R.string.search_favorite)
                setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {
                            isViewAll = false
                            clearFocus()
                            search(query)
                        }
                        return true
                    }
                    override fun onQueryTextChange(newText: String?): Boolean = false
                })
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_ascend -> {
                getFavorites(SortType.ASCENDING, searchQuery)
                return true
            }
            R.id.sort_descend -> {
                getFavorites(SortType.DESCENDING, searchQuery)
                return true
            }
            R.id.sort_default -> {
                getFavorites(SortType.DEFAULT, searchQuery)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setup() {
        viewAll()
        binding.viewAllBtn.setOnClickListener { viewAll() }
    }

    private fun search(keyword: String) {
        searchQuery = keyword
        getFavorites(SortType.DEFAULT, searchQuery)
    }

    private fun getFavorites(sortType: SortType, keyword: String = "") {
        viewModel.getFavorites(FavoriteViewModel.getSortQuery(keyword, sortType)).observe(viewLifecycleOwner) {
            if (isViewAll && it.isEmpty()) {
                binding.searchIcon.isVisible = true
                binding.searchPanel.isVisible = false
            } else {
                binding.searchIcon.isVisible = false
                binding.searchPanel.isVisible = true
            }

            Utils.setupGridListView(requireContext(), binding.gridRv, it, binding.errorView, binding.gridRv)

            binding.foundText.text =
                if (keyword.isEmpty()) resources.getString(R.string.all_favorites, it.size)
                else resources.getString(R.string.search_favorite_found, it.size)

            binding.viewAllBtn.visibility = if (!isViewAll) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun viewAll() {
        isViewAll = true

        searchQuery = ""
        searchView?.apply {
            isIconified = true
            clearFocus()
        }

        getFavorites(SortType.DEFAULT)
    }
}