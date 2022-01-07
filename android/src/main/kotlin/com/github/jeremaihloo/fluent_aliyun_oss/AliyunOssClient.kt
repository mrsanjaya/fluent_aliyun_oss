package com.github.jeremaihloo.fluent_aliyun_oss

import android.content.Context
import com.alibaba.sdk.android.oss.ClientException
import com.alibaba.sdk.android.oss.OSSClient
import com.alibaba.sdk.android.oss.ServiceException
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask
import com.alibaba.sdk.android.oss.model.PutObjectRequest
import com.alibaba.sdk.android.oss.model.PutObjectResult
import com.github.jeremaihloo.fluent_aliyun_oss.entity.AliyunPutObjectResult
import com.github.jeremaihloo.fluent_aliyun_oss.inter.AliyunOssPutObjectCallBack
import io.flutter.Log
import java.util.*

class AliyunOssClient(context: Context?, private val clientConfig: AliyunOssClientConfig, private val connectConfig: AliyunOssClientConnectConfig) {
    private val ossClient: OSSClient

    /// <requestId, task>
    private val tasks: MutableMap<String, OSSAsyncTask<PutObjectResult>> = HashMap()
    fun putObject(bucketName: String, objectName: String, filePath: String?, callBack: AliyunOssPutObjectCallBack): String {
        var objectName = objectName
        objectName = getObjectName(objectName)
        val put = PutObjectRequest(bucketName, objectName, filePath)
        val url = "https://" + bucketName + "." + clientConfig.endpoint + "/" + objectName
        val taskId = UUID.randomUUID().toString().replace("-", "")
        // 异步上传时可以设置进度回调。
        put.progressCallback = OSSProgressCallback { request, currentSize, totalSize ->
            callBack.onProgress(taskId, currentSize, totalSize)
            Log.d("FluentAliyunOssPlugin", "onProgress: $currentSize/$totalSize")
        }
        val task = ossClient.asyncPutObject(put, object : OSSCompletedCallback<PutObjectRequest, PutObjectResult?> {
            override fun onSuccess(request: PutObjectRequest?, result: PutObjectResult?) {
                Log.d("FluentAliyunOssPlugin", "onSuccess1")
                val aliyunPutObjectResult = AliyunPutObjectResult(result!!, url)
                Log.d("FluentAliyunOssPlugin", "onSuccess2")
                callBack.onSuccess(taskId, aliyunPutObjectResult)
                Log.d("FluentAliyunOssPlugin", "onSuccess3")
            }

            override fun onFailure(request: PutObjectRequest?, clientExcepion: ClientException, serviceException: ServiceException) {
                Log.d("FluentAliyunOssPlugin", "onFailure1")
                var aliyunPutObjectResult: AliyunPutObjectResult? = null
                // 请求异常。
                if (clientExcepion != null) {
                    Log.d("FluentAliyunOssPlugin", "onFailure2")
                    aliyunPutObjectResult = AliyunPutObjectResult(clientExcepion)
                }
                if (serviceException != null) {
                    Log.d("FluentAliyunOssPlugin", "onFailure3")
                    aliyunPutObjectResult = AliyunPutObjectResult(serviceException)
                }
                Log.d("FluentAliyunOssPlugin", "onFailure4")
                callBack.onFailure(taskId, aliyunPutObjectResult)
                tasks.remove(taskId)
                Log.d("FluentAliyunOssPlugin", "onFailure5")
            }
        })
        tasks[taskId] = task
        return taskId
    }

    private fun getObjectName(objectName: String): String {
        var objectName: String? = objectName
        if (objectName == null) objectName = UUID.randomUUID().toString().replace("-", "")
        return objectName
    }

    fun signUrl(bucketName: String, objectKey: String, expiredTimeInSeconds: Int): String {
        return ossClient.presignConstrainedObjectURL(bucketName, objectKey, expiredTimeInSeconds)
    }

    init {
        ossClient = OSSClient(context, "https://" + clientConfig.endpoint, clientConfig.oSSCredentialProvider, connectConfig.clientConfiguration)
    }
}
