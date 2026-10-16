
package com.sabbir.keyboard

import android.inputmethodservice.InputMethodService
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout

class SabbirKeyboardService : InputMethodService() {

    private var currentLanguage = 0

    private val languages = arrayOf("বাংলা", "العربية", "اردو", "English")

    override fun onCreateInputView(): View {
        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setBackgroundColor(Color.rgb(5, 48, 37))
        root.setPadding(5, 5, 5, 5)

        val toolbar = LinearLayout(this)
        toolbar.gravity = Gravity.CENTER

        addButton(toolbar, "🕋", 1f) {
            switchLanguage()
            updateKeyboard()
        }
        addButton(toolbar, "📋", 1f) {
            currentInputConnection?.let { connection ->
                val text = connection.getExtractedText(
                    android.view.inputmethod.ExtractedTextRequest(), 0
                )?.text
                if (!text.isNullOrEmpty()) {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE)
                            as android.content.ClipboardManager
                    clipboard.setPrimaryClip(
                        android.content.ClipData.newPlainText("Text", text)
                    )
                }
            }
        }
        addButton(toolbar, "⚙️", 1f) {
            switchLanguage()
            updateKeyboard()
        }

        root.addView(toolbar)
        root.tag = "keyboard_root"
        buildKeys(root)
        return root
    }

    private fun addButton(
        parent: LinearLayout,
        label: String,
        weight: Float,
        action: () -> Unit
    ) {
        val button = Button(this)
        button.text = label
        button.setTextColor(Color.rgb(255, 220, 140))
        button.setBackgroundColor(Color.rgb(8, 76, 57))
        button.setOnClickListener { action() }
        parent.addView(
            button,
            LinearLayout.LayoutParams(0, 52, weight)
        )
    }

    private fun switchLanguage() {
        currentLanguage = (currentLanguage + 1) % languages.size
    }

    private fun updateKeyboard() {
        val root = currentInputView as? LinearLayout ?: return
        root.removeAllViews()
        onCreateInputView().also {
            setInputView(it)
        }
    }

    private fun buildKeys(root: LinearLayout) {
        val rows = when (currentLanguage) {
            0 -> listOf(
                "অ আ ই ঈ উ ঊ এ ঐ ও ঔ",
                "ক খ গ ঘ ঙ চ ছ জ ঝ ঞ",
                "ট ঠ ড ঢ ণ ত থ দ ধ ন",
                "প ফ ব ভ ম য র ল শ ষ স হ",
                "া ি ী ু ূ ৃ ে ৈ ো ৌ ্ ং ঃ ঁ"
            )
            1 -> listOf(
                "ض ص ث ق ف غ ع ه خ ح ج",
                "ش س ي ب ل ا ت ن م ك ط",
                "ئ ء ؤ ر لا ى ة و ز ظ",
                "َ ِ ُ ّ ْ ً ٍ ٌ"
            )
            2 -> listOf(
                "ق و ع ر ت ے ء ی ہ پ",
                "ا س د ف گ ح ج ک ل",
                "ٹ ڈ ڑ ں ھ چ ژ ش",
                "ب ن م ز خ غ"
            )
            else -> listOf(
                "q w e r t y u i o p",
                "a s d f g h j k l",
                "z x c v b n m"
            )
        }

        for (row in rows) {
            val line = LinearLayout(this)
            line.gravity = Gravity.CENTER
            for (letter in row.split(" ")) {
                addButton(line, letter, 1f) {
                    currentInputConnection?.commitText(letter, 1)
                }
            }
            root.addView(
                line,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    48
                )
            )
        }

        val bottom = LinearLayout(this)
        addButton(bottom, "🌐", 1f) {
            switchLanguage()
            updateKeyboard()
        }
        addButton(bottom, "⌫", 1f) {
            currentInputConnection?.deleteSurroundingText(1, 0)
        }
        addButton(bottom, "English/বাংলা", 2f) {
            switchLanguage()
            updateKeyboard()
        }
        addButton(bottom, "Space", 2f) {
            currentInputConnection?.commitText(" ", 1)
        }
        addButton(bottom, "↵", 1f) {
            currentInputConnection?.commitText("\n", 1)
        }
        root.addView(bottom)
    }
}
