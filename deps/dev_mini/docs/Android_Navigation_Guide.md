# Android Navigation 导航架构文档

## 一、项目 Navigation 结构总览

### 1.1 导航图层级关系

```
nav_graph.xml (主导航图)
├── nav_a_graph.xml (A模块嵌套图)
│   ├── A1Fragment (起始页)
│   └── nav_a2_graph (A2模块嵌套图)
│       ├── A2Fragment (起始页)
│       └── A21Fragment
└── nav_b_graph.xml (B模块嵌套图)
    └── BFragment (起始页)
```

### 1.2 导航流程图

```
                    ┌─────────────────────────────────────────┐
                    │           nav_graph (主导航图)            │
                    │         startDestination: nav_a_graph    │
                    └─────────────────────────────────────────┘
                                        │
            ┌───────────────────────────┴───────────────────────────┐
            ▼                                                       ▼
┌─────────────────────────────┐                      ┌─────────────────────────┐
│      nav_a_graph (A模块)     │◄────────────────────│     nav_b_graph (B模块)  │
│  startDestination: a1Fragment│   action_b_to_a     │ startDestination: bFrag  │
└─────────────────────────────┘                      └─────────────────────────┘
            │                                                    ▲
            │                                                    │
            ▼                                                    │
    ┌───────────────┐         action_a1_to_b                     │
    │  A1Fragment   │────────────────────────────────────────────┘
    └───────────────┘
            │
            │ action_a1_to_nav_a2_graph
            ▼
┌─────────────────────────────┐
│      nav_a2_graph (嵌套)     │
│  startDestination: a2Fragment│
└─────────────────────────────┘
            │
            ▼
    ┌───────────────┐   action_a2_to_a21   ┌───────────────┐
    │  A2Fragment   │─────────────────────►│  A21Fragment  │
    └───────────────┘◄─────────────────────└───────────────┘
                        action_a21_to_a2
```

---

## 二、各导航图详解

### 2.1 主导航图 (nav_graph.xml)

| 属性               | 值           | 说明          |
|------------------|-------------|-------------|
| id               | nav_graph   | 主导航图唯一标识    |
| startDestination | nav_a_graph | 应用启动时的默认目的地 |

**包含的嵌套导航图：**

- `nav_a_graph` - A模块导航图
- `nav_b_graph` - B模块导航图

**全局 Action：**

| Action ID                 | 目的地         | popUpTo   | 说明       |
|---------------------------|-------------|-----------|----------|
| action_global_nav_a_graph | nav_a_graph | nav_graph | 全局跳转到A模块 |
| action_global_nav_b_graph | nav_b_graph | nav_graph | 全局跳转到B模块 |

### 2.2 A模块导航图 (nav_a_graph.xml)

**Fragment 清单：**

| Fragment    | ID          | 说明       |
|-------------|-------------|----------|
| A1Fragment  | a1Fragment  | A模块起始页   |
| A2Fragment  | a2Fragment  | A2子模块起始页 |
| A21Fragment | a21Fragment | A2子模块详情页 |

**Action 清单：**

| Action ID                 | 来源  | 目的地          | popUpTo               | 说明       |
|---------------------------|-----|--------------|-----------------------|----------|
| action_a1_to_nav_a2_graph | A1  | nav_a2_graph | -                     | 进入A2子模块  |
| action_a1_to_b            | A1  | nav_b_graph  | nav_graph             | 跳转到B模块   |
| action_a2_to_a21          | A2  | a21Fragment  | a2Fragment            | 进入A21详情页 |
| action_a21_to_a2          | A21 | a2Fragment   | a2Fragment(inclusive) | 返回A2页面   |

### 2.3 B模块导航图 (nav_b_graph.xml)

**Fragment 清单：**

| Fragment  | ID        | 说明     |
|-----------|-----------|--------|
| BFragment | bFragment | B模块起始页 |

**Action 清单：**

| Action ID     | 来源 | 目的地         | popUpTo   | 说明    |
|---------------|----|-------------|-----------|-------|
| action_b_to_a | B  | nav_a_graph | nav_graph | 返回A模块 |

---

## 三、Android Navigation 核心用法

### 3.1 基础配置

#### 添加依赖 (build.gradle.kts)

```kotlin
dependencies {
    val nav_version = "2.7.7"

    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")
}
```

#### Activity 布局中添加 NavHostFragment

```xml

<androidx.fragment.app.FragmentContainerView android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment" android:layout_width="match_parent"
    android:layout_height="match_parent" app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />
```

### 3.2 导航图 XML 结构

#### 基本导航图

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto" android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <fragment android:id="@+id/homeFragment" android:name="com.example.HomeFragment"
        android:label="首页">

        <action android:id="@+id/action_home_to_detail" app:destination="@id/detailFragment" />
    </fragment>

    <fragment android:id="@+id/detailFragment" android:name="com.example.DetailFragment"
        android:label="详情" />
</navigation>
```

#### 嵌套导航图

```xml
<!-- 方式1: 内联嵌套 -->
<navigation android:id="@+id/nested_graph" app:startDestination="@id/nestedFragment">

    <fragment android:id="@+id/nestedFragment"
    ... />
</navigation>

    <!-- 方式2: 引用外部文件 -->
<include app:graph="@navigation/nested_nav_graph" />
```

### 3.3 代码中执行导航

#### 获取 NavController

```kotlin
// 在 Fragment 中
val navController = findNavController()

// 在 Activity 中
val navController = findNavController(R.id.nav_host_fragment)
```

#### 基本导航

```kotlin
// 使用 Action ID 导航
findNavController().navigate(R.id.action_home_to_detail)

// 使用目的地 ID 直接导航
findNavController().navigate(R.id.detailFragment)
```

#### 带参数导航

```kotlin
// 使用 Bundle 传参
val bundle = bundleOf(
    "userId" to 123,
    "userName" to "张三"
)
findNavController().navigate(R.id.action_home_to_detail, bundle)

// 在目标 Fragment 中接收
val userId = arguments?.getInt("userId")
val userName = arguments?.getString("userName")
```

#### 使用 Safe Args（推荐）

```kotlin
// 添加插件后自动生成 Directions 类
val action = HomeFragmentDirections.actionHomeToDetail(userId = 123)
findNavController().navigate(action)
```

### 3.4 返回栈管理 (popUpTo)

#### XML 配置方式

```xml

<action android:id="@+id/action_detail_to_home" app:destination="@id/homeFragment"
    app:popUpTo="@id/homeFragment" app:popUpToInclusive="true" />
```

| 属性                         | 说明                  |
|----------------------------|---------------------|
| `popUpTo`                  | 弹出返回栈直到指定目的地        |
| `popUpToInclusive="false"` | 保留 popUpTo 指定的目的地   |
| `popUpToInclusive="true"`  | 同时弹出 popUpTo 指定的目的地 |

#### 代码配置方式

```kotlin
val navOptions = NavOptions.Builder()
    .setPopUpTo(R.id.homeFragment, inclusive = true)
    .build()
findNavController().navigate(R.id.detailFragment, null, navOptions)
```

#### 本项目中的应用示例

```kotlin
// A2Fragment.kt - 跳转到 A21 时将 A2 从返回栈移除
val navOptions = NavOptions.Builder()
    .setPopUpTo(R.id.a2Fragment, true)
    .build()
findNavController().navigate(R.id.action_a2_to_a21, null, navOptions)
```

### 3.5 全局 Action

全局 Action 定义在 `<navigation>` 根元素下，可以从任意 Fragment 触发：

```xml

<navigation android:id="@+id/nav_graph" ...>

    <!-- 全局 Action -->
<action android:id="@+id/action_global_settings" app:destination="@id/settingsFragment" />

<fragment ... /></navigation>
```

```kotlin
// 从任意位置触发全局导航
findNavController().navigate(R.id.action_global_settings)
```

### 3.6 导航动画

```xml

<action android:id="@+id/action_home_to_detail" app:destination="@id/detailFragment"
    app:enterAnim="@anim/slide_in_right" app:exitAnim="@anim/slide_out_left"
    app:popEnterAnim="@anim/slide_in_left" app:popExitAnim="@anim/slide_out_right" />
```

---

## 四、本项目导航实现要点

### 4.1 模块化嵌套结构

项目采用**三层嵌套导航图**架构：

1. **主导航图** (`nav_graph.xml`) - 管理模块间导航
2. **模块导航图** (`nav_a_graph.xml`, `nav_b_graph.xml`) - 管理模块内导航
3. **子模块导航图** (`nav_a2_graph` 内联在 `nav_a_graph.xml`) - 管理子模块导航

### 4.2 跨模块导航

```kotlin
// A1Fragment -> B模块
binding.btnGoToB.setOnClickListener {
    findNavController().navigate(R.id.action_a1_to_b)
}

// BFragment -> A模块
binding.btnGoToA.setOnClickListener {
    findNavController().navigate(R.id.action_b_to_a)
}
```

### 4.3 条件跳转（跳过中间页）

```kotlin
// A1Fragment - 传递标记直接跳转到 A21
binding.btnGoToA21.setOnClickListener {
    val bundle = bundleOf("navigateToA21" to true)
    findNavController().navigate(R.id.action_a1_to_nav_a2_graph, bundle)
}

// A2Fragment - 检查标记并自动跳转
val navigateToA21 = arguments?.getBoolean("navigateToA21", false) ?: false
if (navigateToA21) {
    arguments?.remove("navigateToA21")
    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.a2Fragment, true)
        .build()
    findNavController().navigate(R.id.action_a2_to_a21, null, navOptions)
}
```

---

## 五、最佳实践建议

1. **模块化导航图** - 按功能模块拆分导航图，便于维护和团队协作
2. **使用 Safe Args** - 避免手写 Bundle 键名，提供编译时类型安全
3. **合理使用 popUpTo** - 避免返回栈过深，提升用户体验
4. **全局 Action 谨慎使用** - 仅用于真正需要全局访问的目的地
5. **统一导航入口** - 通过 ViewModel 或统一方法管理导航逻辑

---

## 六、常见问题

### Q1: 如何返回到起始页并清空返回栈？

```kotlin
findNavController().navigate(
    R.id.homeFragment, null,
    NavOptions.Builder()
        .setPopUpTo(R.id.nav_graph, true)
        .build()
)
```

### Q2: 如何监听导航变化？

```kotlin
navController.addOnDestinationChangedListener { _, destination, _ ->
    when (destination.id) {
        R.id.homeFragment -> { /* 处理首页 */
        }
        R.id.detailFragment -> { /* 处理详情页 */
        }
    }
}
```

### Q3: 如何处理深层链接 (Deep Link)？

```xml

<fragment android:id="@+id/detailFragment" ...><deepLink android:id="@+id/deepLink"
app:uri="myapp://detail/{id}" /></fragment>
```

---

*文档生成时间: 2025-12-11*
