package com.github.jeremaihloo.fluent_aliyun_oss.entity

import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.model.PutObjectResult
import java.util.*

class AliyunPutObjectResult : PutObjectResult {
    var url: String? = null
    var isSuccess: Boolean? = null
    var errorCode: String? = null
    var hostId: String? = null
    var rawMessage: String? = null
    var partNumber: String? = null
    var partEtag: String? = null
    var message: String? = null

    var taskId: String? = null
    var currentSize = 0
    var totalSize = 0
    var errorMessage: String? = null

    constructor() {}
    constructor(putObjectResult: PutObjectResult, url: String?) {
        eTag = putObjectResult.eTag
        requestId = putObjectResult.requestId
        statusCode = putObjectResult.statusCode
        clientCRC = putObjectResult.clientCRC
        responseHeader = putObjectResult.responseHeader
        serverCRC = putObjectResult.serverCRC
        serverCallbackReturnBody = putObjectResult.serverCallbackReturnBody
        this.url = url
        message = "ok"
        errorCode = "0"
        isSuccess = true
    }

    constructor(e: ServiceException) {
        requestId = e.requestId
        statusCode = e.statusCode
        errorCode = e.errorCode
        hostId = e.hostId
        rawMessage = e.rawMessage
        partNumber = e.partNumber
        partEtag = e.partEtag
        isSuccess = false
        errorMessage = e.message
    }

    constructor(e: ClientException) {
        message = e.message
        isSuccess = false
        errorMessage = e.message
    }

    fun toMap(): Map<String, Any?> {
        val map: MutableMap<String, Any?> = HashMap()
        map["url"] = url
        map["isSuccess"] = isSuccess
        map["errorCode"] = errorCode
        map["statusCode"] = statusCode
        map["message"] = message
        map["errorMessage"] = errorMessage
        map["eTag"] = eTag
        map["requestId"] = requestId
        map["taskId"] = taskId
        map["currentSize"] = currentSize
        map["totalSize"] = totalSize
        return map
    }
}