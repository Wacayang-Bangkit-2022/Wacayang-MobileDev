package com.c22_pc383.wacayang

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.c22_pc383.wacayang.adapter.GridSpacingItemDecoration
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.databinding.FragmentFavoriteBinding
import com.c22_pc383.wacayang.factory.WayangViewModelFactory
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.WayangViewModel

class FavoriteFragment : Fragment(), IGeneralSetup {
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var viewModel: WayangViewModel
    private var isViewAll = true
    private var listItem = ArrayList<Wayang>()
    private var searchView: SearchView? = null

    private val launchDetailsActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == DetailsActivity.DETAILS_RESULT_CODE) {
            if (isViewAll) viewAll()
            else search(searchView?.query.toString())
        }
    }

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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (Utils.isCurrentUserAnonymous()) {
            binding.lockIcon.isVisible = true
            binding.searchIcon.isVisible = false
            binding.searchPanel.isVisible = false
            return
        }

        viewModel = ViewModelProvider(
            this, WayangViewModelFactory(WayangRepository.getDefaultRepository())
        )[WayangViewModel::class.java]

        binding.gridRv.apply {
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(GridSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.margin_small)))
            setItemViewCacheSize(Utils.MAX_VIEW_CACHE)
        }

        setup()
        observerCall()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (Utils.isCurrentUserAnonymous()) return

        inflater.inflate(R.menu.search_menu, menu)

        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = (menu.findItem(R.id.search)?.actionView as SearchView)
            .apply {
                queryHint = resources.getString(R.string.search_favorite)
                setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
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
    }

    override fun setup() {
        viewAll()
        binding.viewAllBtn.setOnClickListener { viewAll() }
    }

    override fun observerCall() {
        viewModel.apply {
            listFav.observe(viewLifecycleOwner) { list ->
                listItem.clear()
                listItem.addAll(list)
            }

            isGetFavError.observe(viewLifecycleOwner) {
                onEndLoading()
                if (it) Utils.toastNetworkError(requireContext())
            }
        }
    }

    private fun search(keyword: String) {
        if (Utils.isCurrentUserAnonymous()) return

        isViewAll = false
        viewModel.getFavWayangs(AppPreference(requireContext()).getToken(), keyword)
        onLoading()
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.viewAllBtn.visibility = if (!isEnabled) View.VISIBLE else View.INVISIBLE
        binding.foundText.text =
            if (isEnabled) resources.getString(R.string.all_favorites)
            else resources.getString(R.string.search_favorite_found, listItem.size)
    }

    private fun viewAll() {
        isViewAll = true
        searchView?.apply {
            isIconified = true
            clearFocus()
        }
        viewModel.getFavWayangs(AppPreference(requireContext()).getToken())

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
        Utils.setupGridListView(requireContext(), binding.gridRv, listItem, binding.errorView, binding.gridRv, launchDetailsActivity)
    }
}