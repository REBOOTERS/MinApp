package com.engineer.compose.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.engineer.compose.ui.ui.theme.MiniAppTheme

class PlaygroundPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Body(modifier = Modifier.padding(innerPadding))
                }
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
fun Body(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AlphaAnimationExample()
        Spacer(modifier = Modifier.height(16.dp))
        CombinedAnimationExample()
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibilityExample()
        Spacer(modifier = Modifier.height(16.dp))
        ContentSwitchAnimationExample()
        Spacer(modifier = Modifier.height(16.dp))
        CrossfadeExample()
        Spacer(modifier = Modifier.height(16.dp))
        InfiniteAnimationExample()
        Spacer(modifier = Modifier.height(16.dp))
        EasingExample()
        Spacer(modifier = Modifier.height(16.dp))
        LayoutTransitionExample()
    }
}

@Preview
@Composable
fun AlphaAnimationExample() {
    var isVisible by remember { mutableStateOf(false) }

    // animateXxxAsState 类似 ValueAnimator/ObjectAnimator
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 300),  // 类似 duration
        label = "alpha"  // 调试标签
    )

    Box(modifier = Modifier.alpha(alpha)) {  // 类似 view.alpha
        Text("淡入淡出效果")
    }

    Button(onClick = { isVisible = !isVisible }) {
        Text("切换显示")
    }
}

@Composable
fun CombinedAnimationExample() {
    var isExpanded by remember { mutableStateOf(false) }

    // 多个 animateXxxAsState 可以组合使用
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.5f, animationSpec = tween(300)
    )
    val alpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f, animationSpec = tween(300)
    )
    val elevation by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 2.dp, animationSpec = tween(300)
    )

    Card(
        modifier = Modifier
            .scale(scale)  // 类似 scaleX/scaleY
            .alpha(alpha)  // 类似 alpha
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(elevation)
    ) {
        Text("组合动画效果", Modifier.padding(16.dp))
    }

    Button(onClick = { isExpanded = !isExpanded }) {
        Text("切换显示2")
    }
}

@Composable
fun AnimatedVisibilityExample() {
    var isVisible by remember { mutableStateOf(false) }

    // AnimatedVisibility 自动处理进入/退出动画
    AnimatedVisibility(
        visible = isVisible, enter = slideInVertically() + fadeIn(),  // 进入动画
        exit = slideOutVertically() + fadeOut()   // 退出动画
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("动画显示的内容", Modifier.padding(16.dp))
        }
    }

    Button(onClick = { isVisible = !isVisible }) {
        Text("切换显示")
    }
}

@Composable
fun ContentSwitchAnimationExample() {
    var tabIndex by remember { mutableStateOf(0) }

    // AnimatedContent 自动处理内容切换动画
    AnimatedContent(
        targetState = tabIndex, transitionSpec = {
            // 定义内容切换动画
            slideInHorizontally { width -> width } + fadeIn() togetherWith slideOutHorizontally { width -> -width } + fadeOut()
        }) { index ->
        when (index) {
            0 -> Card { Text("页面 1", Modifier.padding(16.dp)) }
            1 -> Card { Text("页面 2", Modifier.padding(16.dp)) }
            2 -> Card { Text("页面 3", Modifier.padding(16.dp)) }
        }
    }

    Row {
        listOf("Tab1", "Tab2", "Tab3").forEachIndexed { index, text ->
            Button(
                onClick = { tabIndex = index }, modifier = Modifier.padding(4.dp)
            ) {
                Text(text)
            }
        }
    }
}

@Composable
fun CrossfadeExample() {
    var selected by remember { mutableStateOf(0) }

    // Crossfade 专门用于淡入淡出切换
    Crossfade(
        targetState = selected, animationSpec = tween(durationMillis = 300), label = "crossfade"
    ) { index ->
        Card(
            modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
                containerColor = when (index) {
                    0 -> Color.Red
                    1 -> Color.Green
                    else -> Color.Blue
                }
            )
        ) {
            Text("颜色 $index", Modifier.padding(16.dp))
        }
    }

    Button(onClick = {
        selected++; if (selected > 2) selected = 0
    }) {
        Text("切换颜色")
    }
}

@Composable
fun InfiniteAnimationExample() {
    // rememberInfiniteTransition 创建无限动画
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(1000), repeatMode = RepeatMode.Reverse  // 类似 ValueAnimator.REVERSE
        ), label = "alpha"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f, animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart  // 类似 ValueAnimator.RESTART
        ), label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .alpha(alpha)
            .rotate(rotation)
            .background(MaterialTheme.colorScheme.primary)
    )
}

@Composable
fun EasingExample() {
    var isAnimated by remember { mutableStateOf(false) }

    val offset by animateDpAsState(
        targetValue = if (isAnimated) 100.dp else 0.dp, animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing  // 对应 AccelerateDecelerateInterpolator
            // 其他选项：
            // LinearEasing - 线性
            // FastOutLinearInEasing - 对应 AccelerateInterpolator
            // LinearOutSlowInEasing - 对应 DecelerateInterpolator
        ), label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = offset)
            .size(50.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
    Button(
        onClick = { isAnimated = !isAnimated }, modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("开始动画")
    }
}

@Composable
fun LayoutTransitionExample() {
    var items by remember { mutableStateOf(listOf(1, 2, 3)) }

    // 使用 AnimatedContent 实现布局过渡
    AnimatedContent(
        targetState = items, transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        }) { currentItems ->
        Column {
            currentItems.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Text("Item $item", Modifier.padding(16.dp))
                }
            }
        }
    }
    Row() {
        Button(onClick = {
            items = items + (items.size + 1)
        }) {
            Text("添加项")
        }
        Button(onClick = {
            items = if (items.isNotEmpty()) items.dropLast(1) else items
        }) {
            Text("移除项")
        }
    }

}