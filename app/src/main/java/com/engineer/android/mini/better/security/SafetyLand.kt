package com.engineer.android.mini.better.security

import android.util.Base64
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException


object SafetyLand {

    fun genKeyPair(): Pair<String, String> {
        var publicKeyStr = ""
        var privateKeyStr = ""

        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(1024)
            val keyPair = keyPairGenerator.generateKeyPair()
            val publicKey = keyPair.public
            val privateKey = keyPair.private
            publicKeyStr = String(Base64.encode(publicKey.encoded, Base64.NO_WRAP))
            privateKeyStr = String(Base64.encode(privateKey.encoded, Base64.NO_WRAP))
            return Pair(publicKeyStr, privateKeyStr)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return Pair("", "")
    }
}
