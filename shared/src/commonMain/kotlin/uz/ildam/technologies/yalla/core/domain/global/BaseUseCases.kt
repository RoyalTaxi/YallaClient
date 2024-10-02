package uz.ildam.technologies.yalla.core.domain.global

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

abstract class UseCase<R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(): Result<R> =
        withContext(coroutineDispatcher) {
            runCatching { execute() }
        }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): R
}

abstract class UseCaseWithParams<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameter: P): Result<R> =
        withContext(coroutineDispatcher) {
            runCatching { execute(parameter) }
        }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameter: P): R
}

abstract class UseCaseWithTwoParams<in P, in R, T>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameter1: P, parameter2: R): Result<T> =
        withContext(coroutineDispatcher) {
            runCatching { execute(parameter1, parameter2) }
        }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameter1: P, parameter2: R): T
}

abstract class UseCaseWithThreeParams<in P, R, T, S>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameter1: P, parameter2: R, parameter3: T): Result<S> =
        withContext(coroutineDispatcher) {
            runCatching { execute(parameter1, parameter2, parameter3) }
        }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameter1: P, parameter2: R, parameter3: T): S
}

abstract class UseCaseWithFourParams<in P, R, T, S, I>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(
        parameter1: P,
        parameter2: R,
        parameter3: T,
        parameter4: S
    ): Result<I> =
        withContext(coroutineDispatcher) {
            runCatching { execute(parameter1, parameter2, parameter3, parameter4) }
        }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(
        parameter1: P,
        parameter2: R,
        parameter3: T,
        parameter4: S
    ): I
}

abstract class FlowUseCase<R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(): Flow<Result<R>> =
        execute()
            .map { Result.success(it) }
            .catch { Result.failure<R>(it) }
            .flowOn(coroutineDispatcher)

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): Flow<R>
}

abstract class FlowUseCaseWithParams<in P, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameter: P): Flow<Result<R>> =
        execute(parameter)
            .map { Result.success(it) }
            .catch { Result.failure<R>(it) }
            .flowOn(coroutineDispatcher)

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameter: P): Flow<R>
}

abstract class FlowUseCaseWithTwoParams<in P, T, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(parameter1: P, parameter2: T): Flow<Result<R>> =
        execute(parameter1, parameter2)
            .map { Result.success(it) }
            .catch { Result.failure<R>(it) }
            .flowOn(coroutineDispatcher)

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(parameter1: P, parameter2: T): Flow<R>
}


abstract class FlowUseCaseWithThreeParams<in P, T, S, R>(private val coroutineDispatcher: CoroutineDispatcher) {
    suspend operator fun invoke(
        parameter1: P,
        parameter2: T,
        parameter3: S,
    ): Flow<Result<R>> =
        execute(parameter1, parameter2, parameter3)
            .map { Result.success(it) }
            .catch { Result.failure<R>(it) }
            .flowOn(coroutineDispatcher)

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(
        parameter1: P,
        parameter2: T,
        parameter3: S,
    ): Flow<R>
}

