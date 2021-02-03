# fluent_aliyun_oss

[![Pub Version (including pre-releases)](https://img.shields.io/pub/v/fluent_aliyun_oss?include_prereleases)](https://pub.flutter-io.cn/packages/fluent_aliyun_oss) [![GitHub license](https://img.shields.io/github/license/jeremaihloo/fluent_aliyun_oss)](https://github.com/jeremaihloo/fluent_aliyun_oss/blob/master/LICENSE) [![GitHub stars](https://img.shields.io/github/stars/jeremaihloo/fluent_aliyun_oss?style=social)](https://github.com/jeremaihloo/fluent_aliyun_oss/stargazers)

阿里云对象存储(aliyun oss)的flutter插件.

## Getting Started

First, add image_picker as a dependency in your pubspec.yaml file.

## Example

```dart
import 'package:cached_network_image/cached_network_image.dart';
import 'package:fluent_aliyun_oss/client_config.dart';
import 'package:fluent_aliyun_oss/put_object_event_handler.dart';
import 'package:fluent_aliyun_oss/put_object_request.dart';
import 'package:fluent_aliyun_oss_example/syncing_api_config.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:fluent_aliyun_oss/fluent_aliyun_oss.dart';
import 'package:getdemos_commonapis/api.dart';
import 'package:image_picker/image_picker.dart';
import 'package:percent_indicator/linear_percent_indicator.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  final ImagePicker _picker = ImagePicker();

  String text = "";

  String imgUrl;

  double _precent1 = 0;
  double _precent2 = 0;

  FluentAliyunOss ossClient;

  ApiClient apiClient;
  AuthApi authApi;
  FilesApi filesApi;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await FluentAliyunOss.platformVersion;

      apiClient =
          new ApiClient(basePath: "https://dev.syncing.mashangjiama.com");
      authApi = new AuthApi(apiClient);
      filesApi = new FilesApi(apiClient);

      final payload = new LoginModel()..loginId = username;
      payload.password = password;

      final loginResult = await authApi.apiAuthLoginPwdPost(body: payload);
      if (loginResult.ok) {
        apiClient.setAccessToken(loginResult.data.replaceAll("Bearer ", ""));
        final csResult = await filesApi.apiFilesCsGet();
        if (csResult.ok) {
          ossClient = await FluentAliyunOss.init(
            config: new AliyunOssClientConfig(
              endpoint: "oss-cn-shanghai.aliyuncs.com",
              accessKeyId: csResult.data.accessKeyId,
              accessKeySecret: csResult.data.accessKeySecret,
              securityToken: csResult.data.securityToken,
            ),
          );
        }
      }
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('fluent_aliyun_oss example app'),
        ),
        body: Builder(
          builder: (context) {
            return Container(
              padding: EdgeInsets.all(16),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                    onPressed: () {
                      _picker
                          .getImage(source: ImageSource.gallery)
                          .then((PickedFile file) {
                        if (file == null) return;
                        Future.delayed(Duration(milliseconds: 60), () async {
                          _precent1 = 0;
                          setState(() {});
                          PutObjectEventHandler handler = await ossClient
                              .putObject(AliyunOssPutObjectRequest(
                                  bucketName: 'syncing-api',
                                  objectName: "test",
                                  file: file.path));
                          handler.onProgress = (currentSize, totalSize) {
                            _precent1 = currentSize / totalSize;
                            if (_precent1 >= 1) _precent1 = 0.9999;
                            setState(() {});
                          };
                          handler.onSuccess = (url) {
                            _precent1 = 1;
                            ossClient
                                .signUrl('syncing-api', 'test')
                                .then((signedUrl) => setState(() {
                                      imgUrl = signedUrl;
                                    }));
                          };
                          handler.onFailure = (message) {
                            print(message);
                          };
                        });
                      });
                    },
                    child: Text('异步上传'),
                  ),
                  SizedBox(
                    height: 10,
                  ),
                  new LinearPercentIndicator(
                    percent: _precent1,
                    backgroundColor: Colors.grey,
                    progressColor: Colors.blue,
                  ),
                  SizedBox(
                    height: 10,
                  ),
                  Center(
                    child: Text('Running on: $_platformVersion\n'),
                  ),
                  Padding(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 15, vertical: 15),
                      child: Text(text)),
                  imgUrl != null
                      ? CachedNetworkImage(imageUrl: imgUrl)
                      : SizedBox(
                          height: 0,
                        )
                ],
              ),
            );
          },
        ),
      ),
    );
  }
}

```


