package com.example.evprogect

import android.app.Activity
import android.content.Intent
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.EditText

class CommonUtils {
    companion object{
        fun isEmailValid(email : String): Boolean {
            return Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }
        fun viewPassword(textPassword : EditText){
            var passwordIsValue = textPassword.transformationMethod == PasswordTransformationMethod.getInstance()
            if(passwordIsValue){
                textPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else{
                textPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            textPassword.setSelection(textPassword.text.length)
        }
        fun validPassword(password: String): Boolean {
            val lengthRegex = Regex(".{8,}")
            val uppercaseRegex = Regex("[A-Z]")
            val lowercaseRegex = Regex("[a-z]")
            val digitRegex = Regex("[0-9]")
            val specialCharRegex = Regex("[!@#\$%^&*()-+=]")

            val hasRequiredLength = lengthRegex.matches(password)
            val hasUppercase = uppercaseRegex.containsMatchIn(password)
            val hasLowercase = lowercaseRegex.containsMatchIn(password)
            val hasDigit = digitRegex.containsMatchIn(password)
            val hasSpecialChar = specialCharRegex.containsMatchIn(password)

            return hasRequiredLength && hasUppercase && hasLowercase && hasDigit && hasSpecialChar
        }
    }
}