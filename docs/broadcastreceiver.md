## BroadcastReceiver

### 静态注册

```xml
        <receiver
            android:name=".ui.behavior.MiniReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="com.android.mini.receiver" />
            </intent-filter>
        </receiver>
```

### 动态注册

```java
        MiniReceiver receiver = new MiniReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.mini.receiver");
        registerReceiver(receiver, filter);
```

### 发送广播

```java
        Intent intent = new Intent();
        intent.setAction("com.android.mini.receiver");
        sendBroadcast(intent);
```
Android O 开始还要加上包名

```java
intent.setPackage("com.engineer.android.mini");
intent.addFlags(Intent.FLAG_RECEIVER_INCLUDE_BACKGROUND);
```
## 用 adb 模拟发送广播

```shell
adb shell am broadcast -a com.action.test -f 0x01000000 --es “key01” “value01”
```

### 发送 json

发送 json 之前需要预先对json 字符串进行转义，可以使用 [json.cn](https://www.json.cn/json/jsonzip.html)
这个工具

```shell
adb shell am broadcast -f 0x01000000 -a com.android.mini.receiver -e name "{\"sites\":{\"site\":[{\"id\":\"1\",\"name\":\"菜鸟教程\",\"url\":\"www.runoob.com\"},{\"id\":\"2\",\"name\":\"菜鸟工具\",\"url\":\"c.runoob.com\"},{\"id\":\"3\",\"name\":\"Google\",\"url\":\"www.google.com\"}]}}"
```

### 发送 url

发送 url 时，如果有多个参数，需要对 `&` 连接符进行转义 `\&`

```shell
adb shell am broadcast -f 0x01000000 -a com.android.mini.receiver -e name "http://www.baidu.com?q=where\&name=mike\&age=18
```
也可以使用中文

```shell
adb shell am broadcast -f 0x01000000 -a com.android.mini.receiver -e name "http://www.baidu.com?q=在哪里\&name=路飞"
```



## 参考文档

- https://blog.csdn.net/wangwei890702/article/details/99644607
- http://www.taodudu.cc/news/show-4838146.html?action=onClick