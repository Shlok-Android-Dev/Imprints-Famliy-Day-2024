package com.runner.View

import com.runner.model.UserListModel

interface ListActivityView : IView {
    fun onlistLoad(model: UserListModel?, errCode: Int)
}