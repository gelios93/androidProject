package com.example.doyo.contracts

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.result.contract.ActivityResultContract
import com.example.doyo.activities.EditActivity

class EditContract: ActivityResultContract<EditContract.Input, EditContract.Output>() {

    data class Input(val bitmap: Bitmap?)
    data class Output(val result: Boolean, val bitmap: Bitmap?)

    override fun createIntent(context: Context, input: Input?): Intent {
        val intent = Intent(context, EditActivity::class.java)
        return if (input?.bitmap == null)
            intent
        else
            intent.putExtra("BitmapImage", input.bitmap)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Output {
        val result = resultCode == RESULT_OK
        val bitmap = intent?.getParcelableExtra<Bitmap>("BitmapImage")
        return Output(result, bitmap)
    }
}