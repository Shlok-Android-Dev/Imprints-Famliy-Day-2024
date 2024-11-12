package com.runner.View

import com.runner.model.*

interface ZappingView : IView {

    fun OnUSerZapped(model: ZappingResponce?, errCode: Int)
    fun OnZappingUpdate(model: VerifyResponce?, errCode: Int)
}