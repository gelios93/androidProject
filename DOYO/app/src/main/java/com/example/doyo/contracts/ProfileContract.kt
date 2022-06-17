package com.example.doyo.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.example.doyo.activities.UserActivity
import com.example.doyo.models.User
import com.google.gson.Gson

class ProfileContract: ActivityResultContract<ProfileContract.Input, ProfileContract.Output>() {

    data class Input(val user: User)
    data class Output(val result: Boolean)

    override fun createIntent(context: Context, input: Input?): Intent {
        val intent = Intent(context, UserActivity::class.java)
        val gson = Gson()
        val json: String = gson.toJson(input?.user)
        intent.putExtra("user", json)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Output {
        val result = resultCode == Activity.RESULT_OK
        return Output(result)
    }
}