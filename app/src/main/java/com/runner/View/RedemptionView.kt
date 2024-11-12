package com.runner.View

import com.runner.model.RedemptionListModel
import com.runner.model.VerifyResponce

interface RedemptionView : IView {
    fun OnSearchUser(model: RedemptionListModel?, errCode: Int)
    fun OnUpdateToServer(model: VerifyResponce?, errCode: Int)
}