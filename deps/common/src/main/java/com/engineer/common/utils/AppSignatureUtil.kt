package com.engineer.common.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

object AppSignatureUtil {

    private const val TAG = "AppSignatureUtil"


    fun getAppSignSHA256(context: Context, packageName: String): String? {
        return getAppSignature(context, packageName, "SHA256")
    }

    fun getAppSignMD5(context: Context, packageName: String): String? {
        return getAppSignature(context, packageName, "MD5")
    }

    private fun getAppSignature(context: Context, packageName: String, algorithm: String): String? {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNING_CERTIFICATES
            )

            // 提取签名（取第一个有效签名）
            val signingInfo = packageInfo.signingInfo ?: run {
                Log.i(TAG, "获取 $packageName 的签名失败：签名列表为空")
                return null
            }
//            val schemeVersion = signingInfo.schemeVersion
//            LogUtil.d(TAG, "schemeVersion = $schemeVersion")

            // 从 SigningInfo 中获取签名数据（兼容 API 26+）
            val signatures = if (signingInfo.hasMultipleSigners()) {
                signingInfo.apkContentsSigners
            } else {
                signingInfo.signingCertificateHistory
            }

            if (signatures.isNullOrEmpty()) {
                Log.i(TAG, "获取 $packageName 的签名失败：签名数据为空")
                return null
            }

            // 取第一个签名进行计算
            val signature = signatures[0]

            // 解析证书并计算签名
            val certInputStream = ByteArrayInputStream(signature.toByteArray())
            val x509Cert = CertificateFactory.getInstance("X.509")
                .generateCertificate(certInputStream) as X509Certificate
            val digestBytes = MessageDigest.getInstance(algorithm).digest(x509Cert.encoded)

            // 转换为格式化字符串（大写+冒号分隔）
            bytesToHex(digestBytes)

        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            Log.i(TAG, "获取 $packageName 的签名失败：应用未安装")
            null
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i(TAG, "解析 $packageName 的 $algorithm 签名失败")
            null
        }
    }

    /**
     * 字节数组转格式化签名字符串
     */
    private fun bytesToHex(bytes: ByteArray): String {
        Log.i(TAG, "bytes = ${bytes.contentToString()}")
        return bytes.joinToString(separator = ":") { "%02X".format(it) }
    }
}