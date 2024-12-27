package uz.ildam.technologies.yalla.core.domain.error


sealed interface Either<out D, out E : RootError> {
    data class Success<out D, out E : RootError>(val data: D) : Either<D, E>
    data class Error<out D, out E : RootError>(val error: E) : Either<D, E>
}