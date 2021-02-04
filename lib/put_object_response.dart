import 'package:fluent_aliyun_oss/put_object_progress.dart';
import 'package:fluent_aliyun_oss/put_object_result.dart';

/// 上传返回
class PutObjectResponse {
  final PutObjectResult result;

  final PutObjectProgress progress;

  PutObjectResponse({this.result, this.progress});
}
