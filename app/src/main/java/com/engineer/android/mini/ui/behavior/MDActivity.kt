package com.engineer.android.mini.ui.behavior

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import android.widget.TextView
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
            }
        }

        val markwon = Markwon.builder(this).usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(textView.textSize, buildConfig)).build()

        markwon.setMarkdown(
            textView,
            "11111 **Hello there!** 1111111, $$\\text{A long division \\longdiv{12345}{13}$$"
        );
        textView.setPadding(10.dp)
        val p = FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        p.topMargin = 48.dp
        setContentView(textView, p)
    }
}