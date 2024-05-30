package com.engineer.compose.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.engineer.compose.ui.ui.theme.MiniAppTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val sampleImages = listOf(
                        "https://via.placeholder.com/600/92c952",
                        "https://img.zcool.cn/community/010a6b5922a03eb5b3086ed4b5cd3a.gif",
                        "https://via.placeholder.com/600/24f355",
                        "https://via.placeholder.com/600/d32776",
                        "https://via.placeholder.com/600/f66b97"
                    )
                    ViewPagerExample(sampleImages, modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ViewPagerExample(imageUrls: List<String>, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState()

    Column {
        HorizontalPager(
            count = imageUrls.size, state = pagerState, modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) { page ->
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                val painter = rememberAsyncImagePainter(model = imageUrls[page])
                Image(
                    painter = painter,
                    contentDescription = "Image $page",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Optional: Add some pager indicators or tabs here
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
        )
        PageIndicator(
            currentPage = pagerState.currentPage, pageCount = pagerState.pageCount
        )
    }
}

@Composable
fun PageIndicator(
    currentPage: Int,
    pageCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${currentPage + 1} / $pageCount",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = modifier
        )
    }

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HorizontalPagerIndicator(
    pagerState: com.google.accompanist.pager.PagerState,
    modifier: Modifier = Modifier,
) {
    val indicatorColor = Color.Gray
    Row(
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = modifier
    ) {
        for (i in 0 until pagerState.pageCount) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(if (i == pagerState.currentPage) 12.dp else 8.dp)
                    .background(color = indicatorColor, shape = CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewPagerPreview() {
//    ViewPagerExample()
}