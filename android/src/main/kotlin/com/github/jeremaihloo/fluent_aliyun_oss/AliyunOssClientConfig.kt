package com.github.jeremaihloo.fluent_aliyun_oss

import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider

class AliyunOssClientConfig(val endpoint: String, private val accessKeyId: String, private val accessKeySecret: String, private val securityToken: String) {
    val oSSCredentialProvider: OSSCredentialProvider
        get() = OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken)
}