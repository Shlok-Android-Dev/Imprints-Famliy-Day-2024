package com.runner.View

import com.runner.model.ResponceVerifier

interface plentyview : IView {
    fun onPlentyAppyied(model: ResponceVerifier?, errCode: Int)
}