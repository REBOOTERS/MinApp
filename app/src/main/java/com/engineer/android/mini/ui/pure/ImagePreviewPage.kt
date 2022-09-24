package com.engineer.android.mini.ui.pure

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.compose.R
import com.engineer.compose.ui.ui.theme.MiniAppTheme

class ImagePreviewPage : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent.getStringExtra("uri")
        setContent {
            MiniAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    MyText(Uri.parse(uri))
                }
            }
        }
    }
}

@Composable
fun MyText(uri: Uri) {
    val inputStream = LocalContext.current.contentResolver.openInputStream(uri)
    Image(
        bitmap = BitmapFactory.decodeStream(inputStream).asImageBitmap(),
        contentDescription = "null",
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MiniAppTheme {
        MyText(Uri.parse("content://media/external/images/media/64372"))
    }
}