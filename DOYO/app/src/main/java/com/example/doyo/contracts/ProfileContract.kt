package com.example.doyo.contracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.example.doyo.activities.UserActivity

class ProfileContract: ActivityResultContract<ProfileContract.Input, ProfileContract.Output>() {

    data class Input(val username: String)
    data class Output(val result: Boolean)

    override fun createIntent(context: Context, input: Input?): Intent {
        val intent = Intent(context, UserActivity::class.java)
        intent.putExtra("username", input?.username)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Output {
        val result = resultCode == Activity.RESULT_OK
        return Output(result)
    }
}