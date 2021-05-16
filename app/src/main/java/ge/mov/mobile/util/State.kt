package ge.mov.mobile.util

data class State(
    val status: Status,
    val message: String?,
    val data: Any?
) {
    enum class Status {
        LOADING,
        SUCCESS,
        FAILURE,
        EMPTY
    }

    companion object {
        fun empty() = State(Status.EMPTY, null, null)
        fun loading() = State(Status.LOADING, null, null)
        fun success() = State(Status.SUCCESS, null, null)
        fun <T> success(data: T?) = State(Status.SUCCESS, null, data)
        fun failure() = State(Status.FAILURE, null, null)
        fun failure(message: String?) = State(Status.FAILURE, message, null)
    }
}