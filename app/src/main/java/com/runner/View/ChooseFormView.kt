package com.runner.View

import com.runner.model.ChooseCategoryModel

interface ChooseFormView : IView {
    fun OnFormSuccess(model: ChooseCategoryModel?, errCode: Int)
}