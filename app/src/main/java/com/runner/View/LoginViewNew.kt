package com.runner.View

import com.runner.model.LoginModel
import com.runner.model.LoginModelNew
import com.runner.model.VerifyPinModel

interface LoginViewNew : IView {
    fun onLoginComplete(model: LoginModelNew?, errCode: Int)
}