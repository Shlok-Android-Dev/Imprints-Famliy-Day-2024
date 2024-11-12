package com.runner.View

import com.runner.model.OnspotModel

interface EditValueView:IView {
    fun OnEditUser(model: OnspotModel?, errCode: Int)
}