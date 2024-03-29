package org.moztw.bot.telegram.space

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import java.util.*

internal class Keyboard {
    val showButton: InlineKeyboardMarkup
        get() = getKeyboard(arrayOf(arrayOf("📋 點此顯示工寮關門檢查列表")), arrayOf(arrayOf("list:0")))

    fun getCheckList(data: Int) =
            getKeyboard(arrayOf(
                    arrayOf((if (data and 1 > 0) "✅" else "🖼") + " 白板彙整拍照", (if (data and 2 > 0) "✅" else "🎥") + " 投影機關機"),
                    arrayOf((if (data and 4 > 0) "✅" else "🔮") + " 兩台冷氣關機", (if (data and 8 > 0) "✅" else "🔌") + " 關延長線開關"),
                    arrayOf((if (data and 16 > 0) "✅" else "🔓") + " 鎖上窗戶", (if (data and 64 > 0) "✅" else "🔌") + " 移除飲水機電源"),
                    arrayOf((if (data and 32 > 0) "✅" else "💡") + " 關閉電燈", (if (data and 128 > 0) "✅" else "🚪") + " 檢查門是否關妥"),
                    arrayOf("❌ 點此關閉工寮關門檢查列表")
            ), arrayOf(
                    arrayOf("list:" + Integer.toHexString(data xor 1), "list:" + Integer.toHexString(data xor 2)),
                    arrayOf("list:" + Integer.toHexString(data xor 4), "list:" + Integer.toHexString(data xor 8)),
                    arrayOf("list:" + Integer.toHexString(data xor 16), "list:" + Integer.toHexString(data xor 64)),
                    arrayOf("list:" + Integer.toHexString(data xor 32), "list:" + Integer.toHexString(data xor 128)),
                    arrayOf("hide")
            ))

    private fun getKeyboard(text: Array<Array<String>>, data: Array<Array<String>>): InlineKeyboardMarkup {
        val keyboard = InlineKeyboardMarkup()
        val buttons = ArrayList<List<InlineKeyboardButton>>()
        for (i in text.indices) {
            val buttonRow = ArrayList<InlineKeyboardButton>()
            for (j in text[i].indices) {
                val button = InlineKeyboardButton(text[i][j])
                button.callbackData = data[i][j]
                buttonRow.add(button)
            }
            buttons.add(buttonRow)
        }
        keyboard.keyboard = buttons
        return keyboard
    }
}
