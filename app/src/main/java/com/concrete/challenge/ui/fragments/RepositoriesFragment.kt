package com.concrete.challenge.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.concrete.challenge.R
import com.concrete.challenge.data.model.api.Response
import com.concrete.challenge.domain.errors.HttpError
import com.concrete.challenge.ui.adapters.RepositoryAdapter
import com.concrete.challenge.domain.io.response.RepositoriesResponse
import com.concrete.challenge.presentation.model.RepositoryItem
import com.concrete.challenge.presentation.toRepositoryItem
import com.concrete.challenge.presentation.viewmodel.RepositoryViewModel
import com.concrete.challenge.utils.isInternalServerError
import com.concrete.challenge.utils.isNotFound
import com.concrete.challenge.utils.isTimeout
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val REPOSITORIES_LIST_CONTENT = 1
private const val ERROR_MESSAGE = 2

class RepositoriesFragment : Fragment() {

    private val adapter by lazy { RepositoryAdapter(manager = RepositoryManager()) }

    private val repositoryViewModel: RepositoryViewModel by viewModel()

    private lateinit var rvRepository: RecyclerView
    private lateinit var vfRepository: ViewFlipper
    private lateinit var txtErrorMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_repositories, container, false)
        rvRepository = view.findViewById(R.id.rvRepository)
        vfRepository = view.findViewById(R.id.viewFlipper)
        txtErrorMessage = view.findViewById(R.id.txtError)

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

    private fun addRepositories(repositoriesResponse: Response<RepositoriesResponse>?) {
        when (repositoriesResponse) {
            is Response.OnSuccess -> repositoriesResponse.data?.let { handleSuccess(it) }
            is Response.OnFailure -> handleError(repositoriesResponse.throwable)
            is Response.OnLoading -> TODO()
        }
    }

    private fun handleSuccess(data: RepositoriesResponse) {
        val item = data.repositoriesEntityList.map {
                repository -> repository.toRepositoryItem()
        }

        vfRepository.displayedChild = REPOSITORIES_LIST_CONTENT
        adapter.setItems(item)
    }

    private fun handleError(throwable: Throwable) {
        return when {
            throwable.isNotFound() -> httpErrorHandler(HttpError.NotFound)
            throwable.isInternalServerError() -> httpErrorHandler(HttpError.InternalServerError)
            throwable.isTimeout() -> httpErrorHandler(HttpError.Timeout)
            else -> httpErrorHandler(HttpError.GenericError)
        }

    }

    private fun httpErrorHandler(error: HttpError) {
        val errorMessage = when (error) {
            is HttpError.NotFound -> "Información no encontrada (HTTP 400 ERROR)"
            is HttpError.InternalServerError -> "Error en el servidor (HTTP 500 ERROR)"
            is HttpError.Timeout -> "Tiempo de espera agotado (HTTP 504 ERROR)"
            else -> "¡Ha ocurrido un error!"
        }

        vfRepository.displayedChild = ERROR_MESSAGE
        txtErrorMessage.text = errorMessage
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
