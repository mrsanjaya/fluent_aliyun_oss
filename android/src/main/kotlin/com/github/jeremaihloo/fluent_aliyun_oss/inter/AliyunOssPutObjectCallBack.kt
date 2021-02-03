package com.github.jeremaihloo.fluent_aliyun_oss.inter

import com.github.jeremaihloo.fluent_aliyun_oss.entity.AliyunPutObjectResult

interface AliyunOssPutObjectCallBack {
    fun onSuccess(taskId: String?, result: AliyunPutObjectResult?)
    fun onFailure(taskId: String?, result: AliyunPutObjectResult?)
    fun onProgress(taskId: String?, currentSize: Long, totalSize: Long)
}