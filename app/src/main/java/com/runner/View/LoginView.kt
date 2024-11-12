package com.runner.View

import com.runner.model.LoginModel
import com.runner.model.VerifyPinModel

interface LoginView : IView {
    fun onLoginComplete(model: LoginModel?, errCode: Int)
    fun OnVerifryPin(model: VerifyPinModel?, errCode: Int)
}