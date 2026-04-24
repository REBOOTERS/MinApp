# APK 解析器

本项目提供浏览器与 Node.js 两种方式解析 APK 中的应用信息（包名、版本号、SDK、权限等）。

## 功能

- 解压 APK 并读取 `AndroidManifest.xml`
- 解析 AXML（二进制 XML）
- 提取包名、版本号、应用名称、SDK、权限
- 浏览器页面展示解析结果
- Node.js 命令行解析输出 JSON

## 浏览器使用

1) 启动本地静态服务

```bash
python -m http.server 8080
```

2) 打开页面

```
http://localhost:8080/apk-parser.html
```

3) 上传 APK 解析

> 浏览器依赖 `JSZip` CDN，需通过 HTTP 服务访问，不能直接用 `file://` 打开。

## Node.js 使用

1) 安装依赖

```bash
npm i jszip
```

2) 执行解析

```bash
node apk-parser.js "你的apk路径"
```

输出为 JSON。

## 模块说明

- `apk-parser.js`：解析核心模块（浏览器 + Node.js 兼容）
- `apk-parser.html`：页面入口
- `apk-parse.md`：解析实现说明

## 模块 API

在浏览器中：

```js
const info = await ApkParser.parseApkFile(file, { debug: true });
```

在 Node.js 中：

```js
const ApkParser = require('./apk-parser');
const info = await ApkParser.parseApkPath('app.apk', { debug: true });
```

可选参数：

- `debug: true` 打印解析日志

## 注意事项

- `AndroidManifest.xml` 为二进制 AXML，不是普通 XML
- 若 `label` 为资源 ID，需要解析 `resources.arsc`（当前为预留接口）

## 目录结构

```
.
├─ apk-parser.js
├─ apk-parser.html
├─ apk-parse.md
└─ readme.md
```
