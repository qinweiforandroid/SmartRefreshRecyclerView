package com.qw.recyclerview.loadmore

/**
 * Result of a load-more request.
 */
enum class LoadMoreState {
    SUCCESS,
    HIDDEN,
    NO_MORE,
    ERROR;

    fun toState(): State {
        return when (this) {
            SUCCESS -> State.IDLE
            NO_MORE -> State.NO_MORE
            ERROR -> State.ERROR
            else -> State.HIDDEN
        }
    }
}
