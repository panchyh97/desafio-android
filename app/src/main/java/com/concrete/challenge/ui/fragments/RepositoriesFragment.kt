package com.concrete.challenge.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.concrete.challenge.R
import com.concrete.challenge.ui.adapters.RepositoryAdapter
import com.concrete.challenge.domain.io.response.RepositoriesResponse
import com.concrete.challenge.presentation.model.RepositoryItem
import com.concrete.challenge.presentation.toRepositoryItem
import com.concrete.challenge.presentation.viewmodel.RepositoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REPOSITORIES_LIST_CONTENT = 1

class RepositoriesFragment : Fragment() {

    private val adapter by lazy { RepositoryAdapter(manager = RepositoryManager()) }

    private val repositoryViewModel: RepositoryViewModel by viewModel()

    private lateinit var rvRepository: RecyclerView
    private lateinit var vfRepository: ViewFlipper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_repositories, container, false)
        rvRepository = view.findViewById(R.id.rvRepository)
        vfRepository = view.findViewById(R.id.viewFlipper)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRepository.layoutManager = LinearLayoutManager(requireContext())

        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            (rvRepository.layoutManager as LinearLayoutManager).orientation)
        rvRepository.addItemDecoration(dividerItemDecoration)

        initView()
    }

    private fun initView() {
        initRecyclerView()
        initObservers()
        loadInfo()
    }

    private fun initRecyclerView() {
        rvRepository.adapter = adapter
    }

    private fun initObservers() {
        repositoryViewModel.repositoriesResponse.observe(viewLifecycleOwner, ::addRepositories)
    }

    private fun loadInfo() {
        repositoryViewModel.getRepositories()
    }

    private fun addRepositories(repositoriesResponse: RepositoriesResponse?) {
        if (repositoriesResponse != null) {
            val item = repositoriesResponse.repositoriesEntityList.map {
                    repository -> repository.toRepositoryItem()
            }

            vfRepository.displayedChild = REPOSITORIES_LIST_CONTENT
            adapter.setItems(item)
        }
    }

    inner class RepositoryManager : RepositoryAdapter.AdapterManager {
        override fun onRepositoryClicked(repositoryClicked: RepositoryItem) {

            val bundle = Bundle()

            bundle.putString("username", repositoryClicked.repositoryOwner.username)
            bundle.putString("url", repositoryClicked.pullRequestsUrl)

            parentFragmentManager.setFragmentResult("key", bundle)

            findNavController().navigate(
                R.id.action_repositoriesFragment_to_pullRequestFragment
            )
        }
    }
}
