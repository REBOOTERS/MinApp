用户问的是 Jetpack Compose 中的动画用法，并要求类比传统的属性动画进行描述。我需要对照传统的 View
系统（XML/Java/Kotlin）中的动画来说明 Compose 中的对应方式。

传统动画主要有：

1. View Animation (补间动画) - translate, alpha, scale, rotate
2. Property Animation (属性动画) - ObjectAnimator, ValueAnimator
3. LayoutTransition - 布局过渡动画
4. Transition API - 场景转换动画

Compose 中对应的：

1. animateXxxAsState - 单值动画（类似 ValueAnimator/ObjectAnimator）
2. AnimatedVisibility - 显示/隐藏动画
3. AnimatedContent - 内容切换动画
4. Crossfade - 淡入淡出
5. Transition/MutableTransitionState - 组合动画
6. rememberInfiniteTransition - 无限循环动画
7. Animatable/AnimationSpec - 底层动画控制

我应该按照传统动画的分类来组织 Compose 的动画用法，这样更容易理解。

## Jetpack Compose 动画用法（对比传统属性动画）

### 1. 单属性动画

#### 传统方式

```kotlin
// ValueAnimator
val animator = ValueAnimator.ofFloat(0f, 1f)
animator.duration = 300
animator.addUpdateListener { animation ->
    view.alpha = animation.animatedValue as Float
}
animator.start()

// ObjectAnimator
ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
    duration = 300
    start()
}
```

#### Compose 对应

```kotlin
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
```

**支持的动画类型：**

- `animateFloatAsState()` - Float 属性
- `animateDpAsState()` - Dp 属性
- `animateSizeAsState()` - Size 属性
- `animateOffsetAsState()` - Offset 属性
- `animateIntAsState()` - Int 属性
- `animateColorAsState()` - Color 属性

---

### 2. 组合属性动画

#### 传统方式

```kotlin
// 使用 AnimatorSet 组合多个动画
val scaleX = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f)
val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)

AnimatorSet().apply {
    playTogether(scaleX, scaleY, alpha)  // 同时执行
    duration = 300
    start()
}
```

#### Compose 对应

```kotlin
@Composable
fun CombinedAnimationExample() {
    var isExpanded by remember { mutableStateOf(false) }

    // 多个 animateXxxAsState 可以组合使用
    val scale by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0.5f,
        animationSpec = tween(300)
    )
    val alpha by animateFloatAsState(
        targetValue = if (isExpanded) 1f else 0f,
        animationSpec = tween(300)
    )
    val elevation by animateDpAsState(
        targetValue = if (isExpanded) 8.dp else 2.dp,
        animationSpec = tween(300)
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
}
```

---

### 3. 显示/隐藏动画

#### 传统方式

```kotlin
// ViewPropertyAnimator
view.animate()
    .alpha(1f)
    .translationY(0f)
    .setDuration(300)
    .start()

// View.VISIBLE/VISIBLE/GONE 需要手动处理动画
```

#### Compose 对应

```kotlin
@Composable
fun AnimatedVisibilityExample() {
    var isVisible by remember { mutableStateOf(false) }

    // AnimatedVisibility 自动处理进入/退出动画
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically() + fadeIn(),  // 进入动画
        exit = slideOutVertically() + fadeOut()   // 退出动画
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
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
```

**常用进入/退出动画：**

| 传统方式                | Compose 对应                                         |
|---------------------|----------------------------------------------------|
| `alpha(0f → 1f)`    | `fadeIn()` / `fadeOut()`                           |
| `translationX/Y`    | `slideInHorizontally()` / `slideOutHorizontally()` |
| `scaleX/Y(0f → 1f)` | `scaleIn()` / `scaleOut()`                         |
| `expand/collapse`   | `expandIn()` / `shrinkOut()`                       |

---

### 4. 内容切换动画

#### 传统方式

```kotlin
// 需要手动处理 View 切换动画
val animator1 = ObjectAnimator.ofFloat(view1, "alpha", 1f, 0f)
val animator2 = ObjectAnimator.ofFloat(view2, "alpha", 0f, 1f)

AnimatorSet().apply {
    playSequentially(animator1, animator2)
    start()
}
```

#### Compose 对应

```kotlin
@Composable
fun ContentSwitchAnimationExample() {
    var tabIndex by remember { mutableStateOf(0) }

    // AnimatedContent 自动处理内容切换动画
    AnimatedContent(
        targetState = tabIndex,
        transitionSpec = {
            // 定义内容切换动画
            slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -width } + fadeOut()
        }
    ) { index ->
        when (index) {
            0 -> Card { Text("页面 1", Modifier.padding(16.dp)) }
            1 -> Card { Text("页面 2", Modifier.padding(16.dp)) }
            2 -> Card { Text("页面 3", Modifier.padding(16.dp)) }
        }
    }

    Row {
        listOf("Tab1", "Tab2", "Tab3").forEachIndexed { index, text ->
            Button(
                onClick = { tabIndex = index },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(text)
            }
        }
    }
}
```

---

### 5. 淡入淡出动画

#### 传统方式

```kotlin
// 使用 Crossfade 效果需要手动实现
```

#### Compose 对应

```kotlin
@Composable
fun CrossfadeExample() {
    var selected by remember { mutableStateOf(0) }

    // Crossfade 专门用于淡入淡出切换
    Crossfade(
        targetState = selected,
        animationSpec = tween(durationMillis = 300),
        label = "crossfade"
    ) { index ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
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
}
```

---

### 6. 无限循环动画

#### 传统方式

```kotlin
// 使用 ValueAnimator.INFINITE
val animator = ValueAnimator.ofFloat(0f, 1f)
animator.repeatCount = ValueAnimator.INFINITE
animator.repeatMode = ValueAnimator.RESTART
animator.start()
```

#### Compose 对应

```kotlin
@Composable
fun InfiniteAnimationExample() {
    // rememberInfiniteTransition 创建无限动画
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse  // 类似 ValueAnimator.REVERSE
        ),
        label = "alpha"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart  // 类似 ValueAnimator.RESTART
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(100.dp)
            .alpha(alpha)
            .rotate(rotation)
            .background(MaterialTheme.colorScheme.primary)
    )
}
```

---

### 7. 动画插值器

#### 传统方式

```kotlin
// AccelerateDecelerateInterpolator
val animator = ObjectAnimator.ofFloat(view, "translationX", 0f, 100f)
animator.interpolator = AccelerateDecelerateInterpolator()
animator.start()

// AccelerateInterpolator
animator.interpolator = AccelerateInterpolator()

// DecelerateInterpolator
animator.interpolator = DecelerateInterpolator()
```

#### Compose 对应

```kotlin
@Composable
fun EasingExample() {
    var isAnimated by remember { mutableStateOf(false) }

    val offset by animateDpAsState(
        targetValue = if (isAnimated) 100.dp else 0.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing  // 对应 AccelerateDecelerateInterpolator
            // 其他选项：
            // LinearEasing - 线性
            // FastOutLinearInEasing - 对应 AccelerateInterpolator
            // LinearOutSlowInEasing - 对应 DecelerateInterpolator
        ),
        label = "offset"
    )

    Box(
        modifier = Modifier
            .offset(x = offset)
            .size(50.dp)
            .background(MaterialTheme.colorScheme.primary)
    )
}
```

---

### 8. 布局过渡动画

#### 传统方式

```kotlin
// 使用 LayoutTransition
val layoutTransition = LayoutTransition()
layoutTransition.setAnimator(
    LayoutTransition.APPEARING,
    ObjectAnimator.ofFloat(null, "alpha", 0f, 1f)
)
container.setLayoutTransition(layoutTransition)
```

#### Compose 对应

```kotlin
@Composable
fun LayoutTransitionExample() {
    var items by remember { mutableStateOf(listOf(1, 2, 3)) }

    // 使用 AnimatedContent 实现布局过渡
    AnimatedContent(
        targetState = items,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        }
    ) { currentItems ->
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

    Button(onClick = {
        items = items + (items.size + 1)
    }) {
        Text("添加项")
    }
}
```

---

## 总结对比表

| 传统动画                 | Compose 对应                   | 用途      |
|----------------------|------------------------------|---------|
| ValueAnimator        | animateXxxAsState            | 单值属性动画  |
| ObjectAnimator       | animateXxxAsState + Modifier | 对象属性动画  |
| AnimatorSet          | 多个 animateXxxAsState 组合      | 多属性组合动画 |
| ViewPropertyAnimator | animateXxxAsState            | 链式调用动画  |
| LayoutTransition     | AnimatedContent              | 布局过渡动画  |
| 手动淡入淡出               | Crossfade                    | 内容切换动画  |
| View.VISIBLE 动画      | AnimatedVisibility           | 显示/隐藏动画 |
| INFINITE repeat      | rememberInfiniteTransition   | 无限循环动画  |
| Interpolator         | Easing                       | 动画插值曲线  |

ReportID: 4093edac-bcf5-43c5-8770-130861155d83
ConversationID: 6c7a6282-f63b-402b-95ab-10594c60bdb3