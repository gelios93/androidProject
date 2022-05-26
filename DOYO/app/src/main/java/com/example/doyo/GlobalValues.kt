package com.example.doyo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.util.regex.Pattern

const val SERVER_IP = "http://34.147.15.234"
val INPUT_PATTERN: Pattern = Pattern.compile("^[A-Za-z0-9]+\$")


data class ValidationResult(val result: Boolean, val error: String? = null)

fun toBase64(bitmap: Bitmap): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun toBitmap (encoded: String): Bitmap {
    val imageBytes = Base64.decode(encoded, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun isValidEmail (email: String): ValidationResult {
    if (email.isEmpty())
        return ValidationResult(false, "This field must be filled")
    val result = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    return if (result) ValidationResult(true)
    else ValidationResult(false, "Input does not match email format")
}

fun isValidName (username: String): ValidationResult {
    if (username.isEmpty())
        return ValidationResult(false, "This field must be filled")
    if (username.length < 4)
        return ValidationResult(false, "Username must contain at least 4 characters")
    if (username.length >= 25)
        return ValidationResult(false, "Username must contain less than 25 characters")
    val result = INPUT_PATTERN.matcher(username).matches()
    return if (result) ValidationResult(true)
    else ValidationResult(false, "Only latin characters and digits are allowed")
}

fun isValidPassword (password: String): ValidationResult {
    if (password.isEmpty())
        return ValidationResult(false, "This field must be filled")
    if (password.length < 8)
        return ValidationResult(false, "Password must contain at least 8 characters")
    if (password.length >= 35)
        return ValidationResult(false, "Password must contain less than 35 characters")
    val result = INPUT_PATTERN.matcher(password).matches()
    return if (result) ValidationResult(true)
    else ValidationResult(false, "Only latin characters and digits are allowed")
}
