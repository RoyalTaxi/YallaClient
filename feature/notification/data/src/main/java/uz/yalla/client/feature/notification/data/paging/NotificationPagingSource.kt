package uz.yalla.client.feature.notification.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import uz.yalla.client.core.domain.error.Either
import uz.yalla.client.feature.domain.model.NotificationModel
import uz.yalla.client.feature.notification.data.mapper.NotificationsMapper
import uz.yalla.client.service.notification.service.NotificationsApiService

class NotificationPagingSource(
    private val service: NotificationsApiService
) : PagingSource<Int, NotificationModel>(){

    override fun getRefreshKey(state: PagingState<Int, NotificationModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationModel> {
        val currentPage = params.key ?: 1
        return try {
            when (val response = service.getNotifications(currentPage, params.loadSize)) {
                is Either.Error -> {
                    LoadResult.Error(Exception(response.error))
                }
                is Either.Success -> {
                    val data = response.data.result?.list?.map ( NotificationsMapper.mapper ).orEmpty()
                    val nextKey = if (data.isEmpty()) null else currentPage + 1

                    LoadResult.Page(
                        data = data,
                        prevKey = if (currentPage == 1) null else currentPage - 1,
                        nextKey = nextKey
                    )
                }
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}