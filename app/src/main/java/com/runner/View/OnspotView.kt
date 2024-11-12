package com.runner.View

import com.runner.model.OnspotModel

interface OnspotView : IView {
    fun OnSavedUSer(model: OnspotModel?, errCode: Int)
}