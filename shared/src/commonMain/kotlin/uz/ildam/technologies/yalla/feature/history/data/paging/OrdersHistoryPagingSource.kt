package uz.ildam.technologies.yalla.feature.history.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import uz.ildam.technologies.yalla.core.domain.error.Either
import uz.ildam.technologies.yalla.feature.history.data.mapper.OrdersHistoryMapper
import uz.ildam.technologies.yalla.feature.history.data.service.OrdersHistoryApiService
import uz.ildam.technologies.yalla.feature.history.domain.model.OrdersHistoryModel

class OrdersHistoryPagingSource(
    private val service: OrdersHistoryApiService
) : PagingSource<Int, OrdersHistoryModel>() {

    override fun getRefreshKey(state: PagingState<Int, OrdersHistoryModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrdersHistoryModel> {
        val currentPage = params.key ?: 1
        return try {
            when (val response = service.getOrders(currentPage, 20)) {
                is Either.Error -> LoadResult.Error(Exception(response.error.name))
                is Either.Success -> {
                    val data = response.data.result?.list?.map(OrdersHistoryMapper.mapper).orEmpty()
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