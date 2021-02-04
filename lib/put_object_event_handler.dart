import 'package:fluent_aliyun_oss/put_object_result.dart';

/// 成功回调
typedef OnUploadSuccess = void Function(String url);

/// 失败回调
typedef OnUploadFailure = void Function(String message);

/// 进度回调
typedef OnProgress = void Function(int currentSize, int totalSize);

/// 上传时的相关事件回调
class PutObjectEventHandler {
  final String taskId;

  PutObjectEventHandler({this.taskId});

  void dispatch(PutObjectResult event) {
    if (event.taskId != taskId) return;
    if (event.isFinished) {
      onSuccess?.call(event.url);
    } else if (event.isError) {
      onFailure?.call(event.errorMessage);
    } else {
      onProgress?.call(event.currentSize, event.totalSize);
    }
  }

  /// 成功回调
  OnUploadSuccess onSuccess;

  /// 失败回调
  OnUploadFailure onFailure;

  /// 进度回调
  OnProgress onProgress;
}
