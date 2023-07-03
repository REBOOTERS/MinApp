Android Apk 签名

### 生成签名证书

生成签名，可以直接使用 Android Studio 生成，参见[Android签名和JKS文件查看方法](https://www.jianshu.com/p/81c6c7d3390a) 。

#### keytool 生成签名

命令行生成 ,使用 `keytool -genkeypair`

options 部分说明

```shell
 -alias <alias>          要处理的条目的别名
 -keyalg <alg>           密钥算法名称
 -keysize <size>         密钥位大小
 -groupname <name>       Group name. For example, an Elliptic Curve name.
 -sigalg <alg>           签名算法名称
 -destalias <alias>      目标别名
 -dname <name>           唯一判别名
 -startdate <date>       证书有效期开始日期/时间
 -ext <value>            X.509 扩展
 -validity <days>        有效天数
 -keypass <arg>          密钥口令
 -keystore <keystore>    密钥库名称
 -storepass <arg>        密钥库口令
 -storetype <type>       密钥库类型
 -providername <name>    提供方名称
 -addprovider <name>     按名称 (例如 SunPKCS11) 添加安全提供方
   [-providerarg <arg>]    配置 -addprovider 的参数
 -providerclass <class>  按全限定类名添加安全提供方
   [-providerarg <arg>]    配置 -providerclass 的参数
 -providerpath <list>    提供方类路径
 -v                      详细输出
 -protected              通过受保护的机制的口令
```

- 生成签名证书命令

```shell
echo y | keytool -v -genkeypair -dname "cn=China, ou=Beijing, o=Sun, c=China" -alias business -keypass asd123 -keystore my.keystore -storepass qwe123 -validity 365
```

### 用 keytool 查看签名证书的信息

可以使用以下命令查看已有签名文件的信息，当然前提得知道签名密码。

```shell
keytool -v -list -keystore my.keystore -storepass qwe123
```

or

```shell
keytool -v -list -keystore my.keystore
```

输入密码后回车


### 用 apksigner 查看 apk 签名信息

可以使用以下命令查看 apk 的签名情况。可以输出 apk 是否签名及使用了哪种签名方式。

> tips: 打 release 包的时候，去除 `android.buildTypes.release` 闭包下 `signingConfig` 的配置，就可以打出不含签名的包。

```
$ANDROID_HOME/build-tools/29.0.2/apksigner verify  --verbose xxx.apk
```

```
Verifies
Verified using v1 scheme (JAR signing): true
Verified using v2 scheme (APK Signature Scheme v2): true
Verified using v3 scheme (APK Signature Scheme v3): true
Number of signers: 1
```

### 对 apk 进行签名

#### v1 签名

```
jarsigner -verbose -keystore my.keystore -storepass qwe123 -signedjar result.apk input.apk business
```

#### v2 签名

```
$ANDROID_HOME/build-tools/29.0.2/apksigner sign --verbose --ks my.keystore xxx.apk
```

or

```
$ANDROID_HOME/build-tools/29.0.2/apksigner sign --verbose --ks my.keystore --ks-pass pass:qwe123 --out dest.apk xxx.apk
```

## 参考文档

- [Android签名和JKS文件查看方法](https://www.jianshu.com/p/81c6c7d3390a)
- [how-can-i-create-a-keystore](https://stackoverflow.com/questions/3997748/how-can-i-create-a-keystore)
- [apksigner](https://developer.android.com/studio/command-line/apksigner)
- [Android 签名机制 v1、v2、v3](https://mp.weixin.qq.com/s?__biz=MzIyNTY1MDc4NQ==&mid=2247483933&idx=1&sn=91db6685ff16e832897fb31215b1c44b)
- [Android 端 V1/V2/V3 签名的原理](https://zhuanlan.zhihu.com/p/108034286)