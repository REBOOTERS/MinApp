# Android 系统设置权限说明：`WRITE_SETTINGS`、system app、root

## 1. 背景

在本项目中，`show_touches` 和 `pointer_location` 这两个功能分别对应如下系统命令：

```bash
adb shell settings put system show_touches 1
adb shell settings put system show_touches 0
adb shell settings put system pointer_location 1
adb shell settings put system pointer_location 0
```

项目中当前通过两种方式尝试实现：

1. 直接调用 `Settings.System.putInt(...)`
2. 通过 `Runtime.exec()` 执行 `settings put system ...`

这两种方式依赖的权限模型并不相同，因此容易混淆出“是否必须是系统应用”的问题。

相关实现位于：

- `app/src/main/java/com/engineer/android/mini/util/SystemUtil.kt`

配套 UML 图：

- `../uml/system/write_settings_system_app_flow.puml`

---

## 2. 结论先行

### 结论 1：`WRITE_SETTINGS` 不是普通运行时权限

`android.permission.WRITE_SETTINGS` 属于 **特殊权限（Special App Access）**，不能像普通危险权限一样通过 `requestPermissions()` 直接弹框申请。

正确做法是：

1. 在 `AndroidManifest.xml` 中声明权限
2. 运行时通过 `Settings.System.canWrite(context)` 检查
3. 如果未授权，跳转 `Settings.ACTION_MANAGE_WRITE_SETTINGS`
4. 由用户在系统设置页手动授予“修改系统设置”权限

所以：

> 对于 `Settings.System.putInt(...)` 这种方式，**普通三方应用也可以用**，前提是用户已手动授予 `WRITE_SETTINGS`。

---

### 结论 2：普通“系统应用”不等于拥有所有高权限

“系统应用”通常只是指 APK 随 ROM 预装在系统分区中，例如：

- `/system/app`
- `/system/priv-app`
- `/product/app`
- `/product/priv-app`
- `/system_ext/app`
- `/system_ext/priv-app`

但：

> **system app != 自动拥有所有系统权限**

还需要继续区分：

- 是否是 **普通 system app**
- 是否是 **privileged app（特权系统应用）**
- 是否是 **platform-signed app（平台签名应用）**
- 是否在 ROM 中被加入了额外权限白名单

---

### 结论 3：通过 `Runtime.exec()` 执行 shell 命令，通常要求更高

例如：

```bash
settings put system show_touches 1
settings put system pointer_location 1
```

如果是在应用进程中通过 `Runtime.exec()` 执行，而不是通过 `adb shell` 在 PC 侧执行，那么通常需要：

- `root` 权限，或
- 应用具备很高的系统级能力（例如特权系统应用 / 平台签名应用 / 某些厂商 ROM 放行）

因此：

> `ViaShell` 这类方法对普通三方应用通常不可用，不能简单理解成“只要是 system app 就一定可以”。

---

## 3. Android 中“系统应用”的常见层级

## 3.1 普通三方应用

特点：

- 一般安装在 `/data/app`
- 由用户手动安装
- 默认不具备系统预装身份

对于本项目：

- 可以在用户授权后使用 `WRITE_SETTINGS`
- 通常不能靠 `Runtime.exec()` 去模拟 `adb shell settings put ...`

---

## 3.2 普通系统应用（system app）

特点：

- APK 位于系统镜像分区
- 随系统预装
- 具备 `system app` 身份

但是：

- 并不等于自动获得所有受保护权限
- 并不等于可以直接执行所有 shell 级操作

所以：

> “是 system app” 只是一个基础身份，不代表权限无限制。

---

## 3.3 特权系统应用（privileged app）

特点：

- APK 位于 `priv-app` 目录
- 能拿到一部分 `signature|privileged` 级别权限
- 某些权限还需要系统白名单（`privapp-permissions`）

这类应用相比普通 system app 权限更高，但仍然不代表所有能力都自动具备。

---

## 3.4 平台签名应用（platform-signed app）

特点：

- APK 使用系统 platform key 签名
- 能获得很多 `signature` 级权限

这和“是否是 system app”不是完全同一个维度。

也就是说，一个应用可能：

- 是 system app，但不是 platform-signed
- 是 platform-signed，但未放入 `priv-app`
- 同时满足 platform-signed + privileged，能力通常最强

---

## 4. 和本项目直接相关的权限关系

## 4.1 `Settings.System.putInt(...)`

对应项目实现：

- `SystemUtil.setShowTouches(...)`
- `SystemUtil.setPointerLocation(...)`

其核心依赖是：

- `WRITE_SETTINGS`

特点：

- 普通三方应用可用
- 用户必须手动授权
- 推荐作为 App 内实现系统设置修改的主路径

当前项目采用的也是这条路径。

---

## 4.2 `Runtime.exec("settings put system ...")`

对应项目实现：

- `SystemUtil.setShowTouchesViaShell(...)`
- `SystemUtil.setPointerLocationViaShell(...)`

特点：

- 更接近 `adb shell settings put ...`
- 但在 App 进程内执行时，通常需要更高权限
- 普通三方应用一般不可依赖此方式作为正式能力

更准确的理解应是：

> 这种方式通常要求 **root**，或者应用具备较高系统特权；并不是“普通 system app 就一定可用”。

---

## 5. 一个更准确的判断表

| 场景 | 普通三方应用 | system app | privileged app | root |
|---|---|---:|---:|---:|
| `Settings.System.putInt(...)` + 已授予 `WRITE_SETTINGS` | 通常可行 | 可行 | 可行 | 可行 |
| `Runtime.exec("settings put system ...")` | 通常不可依赖 | 不一定 | 更可能可行 | 可行 |
| 自动获得所有系统权限 | 否 | 否 | 否 | 不适用 |

> 重点：`system app` 本身不是“万能权限”的同义词。

---

## 6. 对当前项目的建议

### 推荐路线

对 `show_touches`、`pointer_location` 这类功能，优先使用：

- `Settings.System.putInt(...)`
- `Settings.System.canWrite(context)`
- `ACTION_MANAGE_WRITE_SETTINGS`

这是最适合普通应用工程实践的路径。

### 不推荐路线

不要把“变成系统应用”当成常规方案，因为这通常意味着：

- 定制 ROM
- 刷机
- root 后放入系统分区
- 处理 platform key / 特权白名单 / 系统镜像构建

这已经超出普通应用开发范畴。

---

## 7. 对 `SystemUtil.kt` 中注释的解释

当前 `ViaShell` 方法里写的是：

> 需要 root 权限或者应用是系统应用

这句话在工程语义上可以理解，但不够严谨。

更准确的表达应当是：

> 通过应用内执行 shell 命令修改系统设置，通常需要 root，或者应用具备系统级/特权级能力；普通三方应用一般不可用。

原因是：

- “系统应用”有层级差异
- 不是所有 system app 都拥有足够能力
- 很多能力还受签名、白名单、SELinux、厂商 ROM 策略影响

---

## 8. 一句话总结

对于本项目中的 `show_touches` / `pointer_location`：

- **首选方案**：`Settings.System.putInt(...)` + `WRITE_SETTINGS` 特殊授权
- **不要误解**：system app 不等于拥有所有系统能力
- **shell 方案**：通常只适合作为 root / 系统特权环境下的补充方案
