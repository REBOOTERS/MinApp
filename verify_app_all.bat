
rem ./gradlew -q clean :other:assembleRelease :app:assemblehuaweiglobalrelease


set app='./app/build/outputs/apk/huaweiGlobal/release/app-huawei-global-release.apk'
set other='./other/build/outputs/apk/release/other-release.apk'

echo %app%

rem 获取签名文件在Apk中路径
set cert_XSA=`jar tf %app% | findstr SA`
echo  $cert_XSA

rem
  rem 提取签名文件
  jar xf %app% %cert_XSA%
  set sha1=`keytool -printcert -file $cert_XSA | findstr SHA1`
  echo --------------------------------------------------------------------------
  echo %app%
  echo 证书 SHA1 指纹: %sha1%
  echo --------------------------------------------------------------------------
  rem #删除提取的文件夹
  rd /S /Q  %cert_XSA%


