package com.qw.recyclerview.loadmore

/**
 * Result of a load-more request.
 */
enum class LoadMoreResult(
    val state: State,
    val success: Boolean,
    val noMoreData: Boolean
) {
    SUCCESS(
        state = State.IDLE,
        success = true,
        noMoreData = false
    ),
    NO_MORE(
        state = State.NO_MORE,
        success = true,
        noMoreData = true
    ),
    ERROR(
        state = State.ERROR,
        success = false,
        noMoreData = false
    );

    companion object {
        fun from(success: Boolean, noMoreData: Boolean): LoadMoreResult {
            return when {
                !success -> ERROR
                noMoreData -> NO_MORE
                else -> SUCCESS
            }
        }
    }
}
