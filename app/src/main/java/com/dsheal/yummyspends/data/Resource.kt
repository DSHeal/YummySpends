package com.dsheal.yummyspends.data

sealed class Resource <T> {
    abstract val data: List<T> // TODO подумать, как не заворачивать все в List. Возможно сделать nullable.
    abstract fun copyData(newData: List<T>): Resource<T>

    data class Success<T>(
        override val data: List<T>
    ) : Resource<T>() {
        override fun copyData(newData: List<T>) = copy(newData)
    }

    data class Loading<T>(
        override val data: List<T> = emptyList()
    ) : Resource<T>() {
        override fun copyData(newData: List<T>) = copy(newData)
    }

    data class Error<T>(
        val error: Throwable,
        override val data: List<T> = emptyList(),
        val message: String? = null
    ) : Resource<T>() {
        override fun copyData(newData: List<T>): Error<T> {
            return copy(error = error, data = newData, message = message)
        }
    }
}
