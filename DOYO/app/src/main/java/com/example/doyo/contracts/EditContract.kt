package com.example.doyo.contracts

import android.content.Context
import android.content.Intent
import android.app.Activity.RESULT_OK
import androidx.activity.result.contract.ActivityResultContract
import com.example.doyo.activities.EditActivity

class EditContract: ActivityResultContract<EditContract.Input, EditContract.Output>() {

    data class Input(val mode: String)
    data class Output(val result: Boolean)

    override fun createIntent(context: Context, input: Input?): Intent {
        val intent = Intent(context, EditActivity::class.java)
        return if (input != null) {
            intent.putExtra("mode", input.mode)
        } else
            intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Output {
        val result = resultCode == RESULT_OK
        return Output(result)
    }
}