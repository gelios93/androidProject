package com.example.doyo.services

import android.content.Context
import com.example.doyo.SERVER_IP
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.nio.charset.StandardCharsets


object HttpService {

    private val client = HttpClient(Android) {
        expectSuccess = false
    }

    fun signIn(context: Context, email: String, password: String) : JSONObject? {
        val sharedPref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        val respBody: JSONObject
        val token = sharedPref.getString("token", "пися")
        println(token)

        if(token != "пися" || (email != " " && password != " ")) {
            return runBlocking {
                val htmlContent : HttpResponse = client.post("$SERVER_IP/api/auth/signin") {
                    if(email != " " && password != " ") {
                        body = FormDataContent( // создаем параметры, которые будут переданы в form
                            Parameters.build {
                                append("email", email)
                                append("password", password)
                            }
                        )
                        header(HttpHeaders.Authorization, "Bearer null")
                    }
                    else
                        header(HttpHeaders.Authorization, token)

                }
                val code = htmlContent.status.value
                if (code !in 500..599){
                    respBody = JSONObject(String(htmlContent.readBytes(), StandardCharsets.US_ASCII))
                    if (code !in 200..299){
                        println(code)
                        respBody.put("code", htmlContent.status.value)
                    }
                    else {
                        sharedPref.edit().putString("token", "Bearer ${respBody.get("accessToken")}").apply()
                    }
                }
                else {
                    respBody = JSONObject()
                    respBody.put("message", "Server is not active")
                    respBody.put("code", code)
                }
                respBody
            }
        }
        else {
            respBody = JSONObject()
            return null
        }

    }

    fun signUp(context: Context, username: String, email: String, password: String) : JSONObject? {
        var respBody: JSONObject?

        return runBlocking {
            val htmlContent : HttpResponse = client.post("$SERVER_IP/api/auth/signup") {
                body = FormDataContent( // создаем параметры, которые будут переданы в form
                    Parameters.build {
                        append("username", username)
                        append("email", email)
                        append("password", password)
                    }
                )
            }
            val code = htmlContent.status.value
            if (code != 200){
                respBody = JSONObject(String(htmlContent.readBytes(), StandardCharsets.US_ASCII))
                respBody?.put("code", htmlContent.status.value)
            }
            else {
                respBody = signIn(context, email, password)
            }
            respBody
        }
    }

    fun editData() {

    }

}