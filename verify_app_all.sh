#!/usr/bin/env sh


declare -A map

package_apk() {
./gradlew -q clean :other:assembleRelease :app:assemblehuaweiglobalrelease
}


verify_apk_sign() {
  cert_XSA=`jar tf $1 | grep SA` #获取签名文件在Apk中路径
#  echo  $cert_XSA
  jar xf $1 $cert_XSA #提取签名文件
  sha1=`keytool -printcert -file $cert_XSA | grep SHA1`
  map["kye"]=$sha1
  echo --------------------------------------------------------------------------
  echo $1
  echo 证书 SHA1 指纹: $sha1
  echo --------------------------------------------------------------------------
  rm -rf  `dirname $cert_XSA` #删除提取的文件夹
}

verify_final() {
  app='./app/build/outputs/apk/huaweiGlobal/release/app-huawei-global-release.apk'
  other='./other/build/outputs/apk/release/other-release.apk'

  for var in $app $other
  do
    verify_apk_sign $var
  done

  for key in ${!map[*]}
  do
    echo $key ,${map[$key]}
  done
}

###################################### function done ##########################

package_apk

verify_final