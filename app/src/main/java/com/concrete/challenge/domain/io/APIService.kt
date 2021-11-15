package com.concrete.challenge.domain.io

import com.concrete.challenge.data.PullRequestEntity
import com.concrete.challenge.domain.io.response.RepositoriesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface APIService {

    @GET("search/repositories?q=language:Java&sort=stars")
    suspend fun getRepositories(
        @Query("page") page: Int
    ): RepositoriesResponse

    @GET("repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Path("owner") username: String,
        @Path("repo") repositoryName: String
    ): List<PullRequestEntity>

}