package com.github.jeremaihloo.fluent_aliyun_oss

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import com.alibaba.sdk.android.oss.common.OSSLog
import com.github.jeremaihloo.fluent_aliyun_oss.entity.AliyunPutObjectResult
import com.github.jeremaihloo.fluent_aliyun_oss.inter.AliyunOssPutObjectCallBack
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.EventChannel.EventSink
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.*


/** FluentAliyunOssPlugin */
class FluentAliyunOssPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    private val mainHandler = Handler(Looper.getMainLooper())

    private val ossClients = mutableMapOf<String, AliyunOssClient>()

    private var context: Context? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "fluent_aliyun_oss")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "init") {
            val endpoint = call.argument<String>("endpoint")
            val accessKeyId = call.argument<String>("accessKeyId")
            val accessKeySecret = call.argument<String>("accessKeySecret")
            val securityToken = call.argument<String>("securityToken")
            val clientKey = call.argument<String>("clientKey")
            OSSLog.enableLog()
            val ossClient = AliyunOssClient(context, AliyunOssClientConfig(endpoint!!, accessKeyId!!, accessKeySecret!!, securityToken!!),
                    AliyunOssClientConnectConfig(null, null, null, null))
            ossClients[clientKey!!] = ossClient
            result.success(true)
        } else if (call.method == "putObject") {
            val bucketName = call.argument<String>("bucketName")
            val objectName = call.argument<String>("objectName")
            val filePath = call.argument<String>("file")
            val clientKey = call.argument<String>("clientKey")
            val ossClient: AliyunOssClient = ossClients.get(clientKey)!!
            val taskId = ossClient.putObject(bucketName!!, objectName!!, filePath, object : AliyunOssPutObjectCallBack {
                override fun onSuccess(taskId: String?, aliyunPutObjectResult: AliyunPutObjectResult?) {
                    Log.d("AliyunOssPutObjectCallBack", "onSuccess1")
                    aliyunPutObjectResult?.taskId = taskId;
                    val res: MutableMap<String, Any> = HashMap()
                    res["url"] = aliyunPutObjectResult?.url ?: ""
                    res["taskId"] = taskId ?: ""
                    Log.d("AliyunOssPutObjectCallBack", "onSuccess2")
                    val runnable = Runnable { channel.invokeMethod("onSuccess", aliyunPutObjectResult?.toMap()) }
                    mainHandler.post(runnable)
                    Log.d("AliyunOssPutObjectCallBack", "onSuccess3")
                }

                override fun onFailure(taskId: String?, aliyunPutObjectResult: AliyunPutObjectResult?) {
                    Log.d("AliyunOssPutObjectCallBack", "onFailure1")
                    val res: MutableMap<String, Any> = HashMap()
                    Log.d("AliyunOssPutObjectCallBack", "onFailure2")
                    res["errorMessage"] = aliyunPutObjectResult?.message ?: "error"
                    Log.d("AliyunOssPutObjectCallBack", "onFailure3")
                    res["taskId"] = taskId ?: ""
                    Log.d("AliyunOssPutObjectCallBack", "onFailure4")
                    val runnable = Runnable { channel.invokeMethod("onFailure", res) }
                    mainHandler.post(runnable)
                    Log.d("AliyunOssPutObjectCallBack", "onFailure2")
                }

                override fun onProgress(taskId: String?, currentSize: Long, totalSize: Long) {
                    val aliyunPutObjectResult = AliyunPutObjectResult()
                    aliyunPutObjectResult.taskId = taskId
                    aliyunPutObjectResult.currentSize = currentSize.toInt()
                    aliyunPutObjectResult.totalSize = totalSize.toInt()
                    val res: MutableMap<String, Any> = HashMap()
                    res["currentSize"] = currentSize
                    res["totalSize"] = totalSize
                    res["taskId"] = taskId ?: ""
                    /// fix: Methods marked with @UiThread must be executed on the main thread.
                    val runnable = Runnable { channel.invokeMethod("onProgress", res) }
                    mainHandler.post(runnable)
                }
            })
            result.success(taskId)
        } else if (call.method == "signUrl") {
            val bucketName = call.argument<String>("bucketName")
            val objectName = call.argument<String>("objectName")
            val clientKey = call.argument<String>("clientKey")
            val expiredTimeInSeconds = call.argument<Int>("expiredTimeInSeconds")!!.toLong()
            val ossClient: AliyunOssClient = ossClients.get(clientKey)!!
            val signedUrl = ossClient.signUrl(bucketName!!, objectName!!, expiredTimeInSeconds!! )
            result.success(signedUrl)
        } else if (call.method == "dispose") {
            val clientKey = call.argument<String>("clientKey")
            ossClients.remove(clientKey)
        } else {
            result.notImplemented()
        }

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
