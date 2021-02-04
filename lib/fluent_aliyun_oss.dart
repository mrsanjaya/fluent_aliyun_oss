import 'dart:async';

import 'package:fluent_aliyun_oss/client_config.dart';
import 'package:fluent_aliyun_oss/put_object_event_handler.dart';
import 'package:fluent_aliyun_oss/put_object_request.dart';
import 'package:fluent_aliyun_oss/put_object_result.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

const String CLIENT_KEY = "primary";

/// 阿里云OSS插件
class FluentAliyunOss {
  static MethodChannel _channel = const MethodChannel('fluent_aliyun_oss')
    ..setMethodCallHandler(_methodHandler);

  static String _clientKey = CLIENT_KEY;

  static Map<String, PutObjectEventHandler> _handlers = {};

  static final StreamController<PutObjectResult> _streamController =
      new StreamController<PutObjectResult>.broadcast();

  /// 初始化阿里云OSS配置
  static Future<FluentAliyunOss> init(
      {@required AliyunOssClientConfig config}) async {
    Map<String, Object> obj = config.toMap();
    obj['clientKey'] = _clientKey;
    await _channel.invokeMethod("init", obj);
    FluentAliyunOss aliyunOssClient = new FluentAliyunOss();

    _streamController.stream.listen((event) {
      final handler = _handlers[event.taskId];
      if (handler != null) {
        handler.dispatch(event);
        if (event.isFinished) _handlers.remove(event.taskId);
      }
    });
    return aliyunOssClient;
  }

  /// 简单文件上传
  Future<PutObjectEventHandler> putObject(
      AliyunOssPutObjectRequest putObjectRequest) async {
    String taskId = await _channel.invokeMethod(
        "putObject", {...putObjectRequest.toMap(), "clientKey": _clientKey});
    final PutObjectEventHandler handler =
        new PutObjectEventHandler(taskId: taskId);
    _handlers[taskId] = handler;
    return handler;
  }

  static Future _methodHandler(MethodCall call) {
    final result = call.arguments;
    final taskId = result['taskId'];
    switch (call.method) {
      case 'onProgress':
        _streamController.sink.add(new PutObjectResult(
            taskId: taskId,
            url: '',
            currentSize: result['currentSize'],
            totalSize: result['totalSize']));
        break;
      case 'onSuccess':
        _streamController.sink
            .add(new PutObjectResult(taskId: taskId, url: result['url']));
        break;
      case 'onFailure':
        _streamController.sink.add(new PutObjectResult(
            taskId: taskId, errorMessage: result['errorMessage']));
    }
    return Future.value();
  }

  /// 签名URL
  Future<String> signUrl(
    String bucketName,
    String objectKey, {
    int expiredTimeInSeconds = 3600,
  }) async {
    return await _channel.invokeMethod("signUrl", {
      "bucketName": bucketName,
      "objectKey": objectKey,
      "expiredTimeInSeconds": expiredTimeInSeconds,
      "clientKey": _clientKey
    });
  }

  void dispose() {
    _streamController.close();
    _handlers.clear();
  }

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
