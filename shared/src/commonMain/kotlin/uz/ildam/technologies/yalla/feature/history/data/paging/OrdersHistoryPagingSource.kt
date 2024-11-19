package uz.ildam.technologies.yalla.feature.history.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import uz.ildam.technologies.yalla.core.domain.error.Result
import uz.ildam.technologies.yalla.feature.history.data.mapper.OrderHistoryMapper
import uz.ildam.technologies.yalla.feature.history.data.service.OrdersHistoryService
import uz.ildam.technologies.yalla.feature.history.domain.model.OrderHistoryModel

class OrdersHistoryPagingSource(
    private val service: OrdersHistoryService
) : PagingSource<Int, OrderHistoryModel>() {

    override fun getRefreshKey(state: PagingState<Int, OrderHistoryModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderHistoryModel> {
        val currentPage = params.key ?: 1
        return try {
            when (val response = service.getOrders(currentPage, 20)) {
                is Result.Error -> LoadResult.Error(Exception(response.error.name))
                is Result.Success -> {
                    val data = response.data.result?.list?.map(OrderHistoryMapper.mapper).orEmpty()
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