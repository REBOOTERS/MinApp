#!/bin/sh

echo "$1"
echo "$2"

sign_1=$(keytool -printcert -jarfile "$1" | grep SHA1 | awk '{print $2}')
sign_2=$(keytool -printcert -jarfile "$2" | grep SHA1 | awk '{print $2}')

echo "sign1 = $sign_1"
echo "sign2 = $sign_2"

if test "$sign_1" = "$sign_2"; then
  echo '签名相等 \(^o^)/~'
else
  echo '签名不相同！！! o(╥﹏╥)o'
fi
