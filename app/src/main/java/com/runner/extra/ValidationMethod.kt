package com.runner.extra

import java.util.regex.Matcher
import java.util.regex.Pattern


object ValidationMethod {
    var m: Matcher? = null
    var emailExpression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,20}$"
    var emailPattern = Pattern.compile(emailExpression, Pattern.CASE_INSENSITIVE)
    var passwordExpression = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,20})"
    var passwordPattern = Pattern.compile(passwordExpression)
    fun emailValidation(s: String?): Boolean {
        return if (s == null) {
            false
        } else {
            m = emailPattern.matcher(s)
            m!!.matches()
        }
    }

    fun passwordValidation(s: String?): Boolean {
        return if (s == null) {
            false
        } else {
            m = passwordPattern.matcher(s)
            m!!.matches()
        }
    }

    fun emailValidation2(s: String?): Boolean {
        m = emailPattern.matcher(s)
        return m!!.matches()
    }
}