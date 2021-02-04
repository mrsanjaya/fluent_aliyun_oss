import 'package:flutter/cupertino.dart';

/// 阿里云OSS配置
class AliyunOssClientConfig {
  /// oss-cn-shenzhen.aliyuncs.com
  final String endpoint;

  String accessKeyId;
  String accessKeySecret;
  String securityToken;

  AliyunOssClientConfig(
      {@required this.endpoint,
      @required this.accessKeyId,
      @required this.accessKeySecret,
      @required this.securityToken});

  Map<String, String> toMap() {
    return {
      "endpoint": this.endpoint,
      "accessKeyId": this.accessKeyId,
      "accessKeySecret": this.accessKeySecret,
      "securityToken": this.securityToken
    };
  }
}
