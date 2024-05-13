package com.engineer.android.mini.ui.behavior

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.toSpanned
import androidx.core.view.setPadding
import com.engineer.android.mini.ext.dp
import com.engineer.android.mini.ui.BaseActivity
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin

class MDActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val textView = TextView(this)

        val buildConfig = object : JLatexMathPlugin.BuilderConfigure {
            override fun configureBuilder(builder: JLatexMathPlugin.Builder) {
                builder.inlinesEnabled(true)
//                builder.blocksLegacy(true)
            }
        }

        val markwon = Markwon.builder(this).usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(textView.textSize, buildConfig)).build()

//        markwon.setMarkdown(
//            textView,
//            "11111 **Hello there!** 1111111, $(a^2+b)$, $$(3^2+4^2=5^2)$$"
////                    "$$\\text{A long division \\longdiv{12345}{13}$$"
//        );

        val spannedString = SpannableStringBuilder()


        val spanned = markwon.toMarkdown(" $$(a^2+b)$$ ")
//        val spanned = markwon.toMarkdown("**Hello there!**");
        spannedString.setSpan(spanned, 0, spannedString.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannedString


        textView.setPadding(10.dp)
        val ll = LinearLayout(this)
        ll.orientation = LinearLayout.VERTICAL
        val p = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        p.topMargin = 48.dp
        window.decorView.setBackgroundColor(Color.WHITE)

        ll.addView(textView)

        val tt = TextView(this)
        markwon.setMarkdown(tt, " $$(a^2+b)$$ ")
        tt.setPadding(10.dp)
        ll.addView(tt)

        val tt1 = TextView(this)


        val kk = tt.text

        markwon.setParsedMarkdown(tt1, kk.toSpanned())
//        tt1.text = tt.text
        tt1.setPadding(10.dp)
        tt1.setTextColor(Color.RED)
        ll.addView(tt1)

        setContentView(ll, p)

        textView.setOnClickListener {
            val spanned = markwon.toMarkdown("**Hello World**,Android")
            Toast.makeText(this, spanned, Toast.LENGTH_SHORT).show()
        }
    }
}