常用命令 (mac)

## 获取文件或内容摘要

- 获取 md5 值

```bash
md5 {filePath}
```
```shell
md5 -s "string-value"
```

```shell
md5 script.md
MD5 (script.md) = 7d57dea4079287893c9218acb6ea0e58
```

- 查看文件SHA1

```bash
openssl dgst -sha1 {targetContent}
```

- 查看文件SHA256

```bash
openssl dgst -sha256 {targetContent}
```
## 输出内容长度

```shell
cat "111" | awk '{print length}'
```
