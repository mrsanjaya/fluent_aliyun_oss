package com.github.jeremaihloo.fluent_aliyun_oss

import com.alibaba.sdk.android.oss.ClientConfiguration

class AliyunOssClientConnectConfig(
        connectionTimeout: Int?,
        socketTimeout: Int?,
        maxConcurrentRequest: Int?,
        maxErrorRetry: Int?
) {
    val clientConfiguration: ClientConfiguration

    companion object {
        private const val DEFAULT_MAX_RETRIES = 2
        private const val DEFAULT_SOCKET_TIMEOUT = 60 * 1000
        private const val DEFAULT_CONNECTION_TIMEOUT = 60 * 1000
        private const val DEFAULT_MAX_CONCURRENT_REQUEST = 5
    }

    init {
        clientConfiguration = ClientConfiguration()
        clientConfiguration.connectionTimeout = connectionTimeout
                ?: DEFAULT_CONNECTION_TIMEOUT // 连接超时，默认15秒。
        clientConfiguration.socketTimeout = socketTimeout
                ?: DEFAULT_SOCKET_TIMEOUT // socket超时，默认15秒。
        clientConfiguration.maxConcurrentRequest = maxConcurrentRequest
                ?: DEFAULT_MAX_CONCURRENT_REQUEST // 最大并发请求数，默认5个。
        clientConfiguration.maxErrorRetry = maxErrorRetry ?: DEFAULT_MAX_RETRIES // 失败后最大重试次数，默认2次。
    }
}