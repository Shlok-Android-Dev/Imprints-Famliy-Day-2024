package com.runner.presenter

import com.runner.View.IView
import retrofit2.Response

abstract class BasePresenter<I : IView?> internal constructor() {
    var view: I? = null
        private set

    fun setView(iView: I) {
        view = iView
    }

    fun handleError(response: Response<*>?): Boolean {
        if (response!!.errorBody() != null) {
            try {
                val error = response.errorBody()!!.string()
                view!!.onError(if (response != null) error else null)
            } catch (e: Exception) {
                e.printStackTrace()
                view!!.onError(null)
                return true
            }
            return true
        }
        return false
    }
}