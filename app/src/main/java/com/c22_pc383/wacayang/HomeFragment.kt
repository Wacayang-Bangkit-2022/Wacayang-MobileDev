package com.c22_pc383.wacayang

import android.Manifest
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.c22_pc383.wacayang.adapter.GridSpacingItemDecoration
import com.c22_pc383.wacayang.adapter.ListWayangAdapter
import com.c22_pc383.wacayang.data.Wayang
import com.c22_pc383.wacayang.databinding.FragmentHomeBinding
import com.c22_pc383.wacayang.factory.FavoriteViewModelFactory
import com.c22_pc383.wacayang.factory.WayangViewModelFactory
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.c22_pc383.wacayang.helper.Utils
import com.c22_pc383.wacayang.network.ApiConfig
import com.c22_pc383.wacayang.network.ApiService
import com.c22_pc383.wacayang.repository.WayangRepository
import com.c22_pc383.wacayang.view_model.FavoriteViewModel
import com.c22_pc383.wacayang.view_model.WayangViewModel


class HomeFragment : Fragment(), IGeneralSetup {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: WayangViewModel

    private val launchCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) openCamera()
        else {
            Toast.makeText(
                requireContext(),
                getString(R.string.permission_denied),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModel = ViewModelProvider(
            this, WayangViewModelFactory(WayangRepository.getRepository())
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
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                startActivity(Intent(requireContext(), SearchActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun setup() {
        binding.viewAllBtn.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    SearchActivity::class.java
                ).apply { putExtra(SearchActivity.VIEW_ALL_EXTRA, true) })
        }

        binding.refreshBtn.setOnClickListener { refresh() }
        binding.scanCard.setOnClickListener { checkCameraPermission() }
    }

    override fun observerCall() {
        viewModel.apply {
            listWayang.observe(viewLifecycleOwner) { list ->
                Utils.setupGridListView(requireContext(), binding.gridRv, list.slice(0..5), binding.errorView, binding.gridRv)
            }

            isGetWayangError.observe(viewLifecycleOwner){
                binding.progressBar.isVisible = false

                if (it) {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.network_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun refresh() {
        viewModel.getWayangs(0)
        binding.errorView.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            launchCameraPermission.launch(Manifest.permission.CAMERA)
        } else openCamera()
    }

    private fun openCamera() {
        startActivity(Intent(requireContext(), CameraActivity::class.java))
    }
}