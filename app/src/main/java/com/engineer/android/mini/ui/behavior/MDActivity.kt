package com.engineer.android.mini.ui.behavior

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.engineer.android.mini.databinding.ActivityMarkdownLayoutBinding
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.util.TypewriterHelper
import com.engineer.common.utils.AndroidFileUtils
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import io.noties.markwon.inlineparser.MarkwonInlineParserPlugin


class MDActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityMarkdownLayoutBinding
    private lateinit var markwon: Markwon

    private val buildConfig = JLatexMathPlugin.BuilderConfigure { builder ->
        builder.inlinesEnabled(true)
        //                builder.blocksLegacy(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMarkdownLayoutBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        markwon = Markwon.builder(this).usePlugin(MarkwonInlineParserPlugin.create())
            .usePlugin(JLatexMathPlugin.create(viewBinding.tv1.textSize, buildConfig)).build()


        markwon.setMarkdown(viewBinding.tv1, "$$(x^2+y)$$")

        markwon.setMarkdown(viewBinding.tv2, "$$(a^2+b)$$")

        markwon.setMarkdown(viewBinding.tv5,"有本书叫做 **《历史的天空》**，很好")

        markwon.setMarkdown(viewBinding.tv4,"有本书叫做 ** 《历史的天空》 **，很好")



        viewBinding.tv1.setOnClickListener {
            val spanned = markwon.toMarkdown("**Hello World**,Android")
            Toast.makeText(this, spanned, Toast.LENGTH_SHORT).show()

            TypewriterHelper.reset()
            TypewriterHelper.clear()
            var lastSize = 0
            TypewriterHelper.start { s, i ->

                if (i == 1 || i % 50 == 0 || i == lastSize) {
                    markwon.setMarkdown(viewBinding.tv3, s)
                    viewBinding.scrollView.scrollBy(0, 200)
                }
                lastSize = i
//                if (i % 1000 == 0) {
//                    viewBinding.scrollView.scrollBy(0, 100)
//                }
            }
            var input = AndroidFileUtils.getStringFromAssets(this, "math.md")
            input = input?.replace("$", "$$")

            input?.forEach {
                Log.i("zzzz", it.toString())
                TypewriterHelper.addMessage(it.toString(), false)
            }
        }
    }
}