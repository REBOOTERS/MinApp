# APK 解析实现说明

本文档描述页面中“解析应用信息”的实现逻辑，仅涵盖 APK 解析过程，不包含上传交互流程。

## 解析总体流程

1. 使用 `JSZip` 解压 APK（APK 本质是 ZIP）。
2. 读取 `AndroidManifest.xml` 的二进制内容（AXML 格式）。
3. 解析 AXML：
   - 解析 String Pool（字符串池）。
   - 遍历 XML 节点（Start Element）。
   - 抽取 `manifest`、`application`、`uses-sdk`、`uses-permission` 等节点属性。
4. 组装应用信息：包名、版本号、应用名、SDK、权限等。
5. 若 `label` 是资源 ID，尝试读取 `resources.arsc` 获取真实应用名（当前实现为预留）。

## 关键实现细节

### 1) 解压 APK 并读取 Manifest

- 通过 `JSZip.loadAsync` 解析 APK 文件内容。
- 读取 `AndroidManifest.xml` 的 `uint8array`，用于 AXML 解析。

### 2) AXML 头部解析

AXML 文件头是 `ResChunk_header`，使用以下字段：

- `fileType`：`0x0003` 表示 `RES_XML_TYPE`。
- `headerSize`：固定为 `0x0008`。
- `fileSize`：文件总长度。

若 `fileType` / `headerSize` 不符合，直接判定为无效 AXML。

### 3) String Pool 解析

String Pool 是 AXML 中所有字符串的集中存储。

- 读取 `stringCount`、`flags`、`stringsStart`。
- 根据 `flags` 判断编码：UTF-8 或 UTF-16。
- `dataStart = offset + stringsStart`，再通过偏移表逐个解码字符串。

辅助函数：

- `readUtf8String(data, offset)`
- `readUtf16String(data, offset)`

### 4) 解析 Start Element

对 `RES_XML_START_ELEMENT_TYPE` 节点解析：

- 根据 `nameIndex` 获取标签名称。
- 解析属性（每个属性 20 字节，索引为 `uint32`）。
- 解析属性值：
  - 若 `attrValueStringIndex != 0xFFFFFFFF`，直接取 String Pool 的字符串。
  - 否则根据 `dataType` 解析：int / hex / boolean / string 等。

### 5) 抽取关键信息

- `manifest`：
  - `package` → `packageName`
  - `versionCode`
  - `versionName`

- `application`：
  - `label` → `appName`（若是 `@0x...` 视为资源 ID）

- `uses-sdk`：
  - `minSdkVersion`
  - `targetSdkVersion`

- `uses-permission`：
  - `android.permission.*` → 去前缀后加入权限列表

### 6) 资源表（可选）

当 `label` 不是字符串而是资源 ID 时：

- 读取 `resources.arsc`
- 解析资源表并解析字符串

当前代码中 `getStringFromResources` 仅预留，尚未实现完整的 `ResTable` 解析。

## 当前输出字段

解析结果对象包含：

- `packageName`
- `appName`
- `version`（versionName）
- `versionCode`
- `minSdk`
- `targetSdk`
- `permissions`
