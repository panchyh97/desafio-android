package com.concrete.challenge.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ViewFlipper
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.concrete.challenge.R
import com.concrete.challenge.data.PullRequestEntity
import com.concrete.challenge.databinding.FragmentPullRequestBinding
import com.concrete.challenge.presentation.viewmodel.PullRequestViewModel
import com.concrete.challenge.ui.adapters.PullRequestAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

const val PULL_REQUEST_NUMBER = "{/number}"

class PullRequestFragment : Fragment() {

    private val adapter by lazy { PullRequestAdapter(manager = PullRequestManager()) }
    private val pullRequestViewModel: PullRequestViewModel by viewModel()

    private lateinit var binding: FragmentPullRequestBinding

    private val rvPullRequest by lazy { binding.rvPullRequest }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPullRequestBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvPullRequest.layoutManager = LinearLayoutManager(requireContext())

        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            (rvPullRequest.layoutManager as LinearLayoutManager).orientation)
        rvPullRequest.addItemDecoration(dividerItemDecoration)

        initView()
    }

    private fun initView() {
        initRecyclerView()
        initObservables()
        loadInfo()
    }

    private fun initRecyclerView() {
        rvPullRequest.adapter = adapter
    }

    private fun initObservables() {
        pullRequestViewModel.pullRequestsList.observe(viewLifecycleOwner, ::addPullRequests)
    }

    private fun loadInfo() {
        parentFragmentManager.setFragmentResultListener("key", this,
            { _, result ->
                val urlPullRequests = result.getString("url")

                val splitUrlPullRequests = urlPullRequests?.split(
                    "https://api.github.com/repos/", PULL_REQUEST_NUMBER, "/")

                val owner = splitUrlPullRequests?.get(1)
                val repo = splitUrlPullRequests?.get(2)

                    owner?.let { it1 -> repo?.let { pullRequestViewModel.getPullRequests(it1, it) } }

            }
        )
    }

    private fun addPullRequests(pullRequestsList: List<PullRequestEntity>) {

        val viewFlipper = view?.findViewById<ViewFlipper>(R.id.vfPullRequest)

        pullRequestsList.let {
            viewFlipper?.showNext()
            adapter.addItems(pullRequestsList)
        }

    }

    inner class PullRequestManager : PullRequestAdapter.AdapterManager {
        override fun onPullRequestClicked(pullRequestClicked: PullRequestEntity) {

            val uri: Uri = Uri.parse(pullRequestClicked.pullRequestUrl)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)

        }
    }

}