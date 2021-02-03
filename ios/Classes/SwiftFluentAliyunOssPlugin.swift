import Flutter
import UIKit

public class SwiftFluentAliyunOssPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "fluent_aliyun_oss", binaryMessenger: registrar.messenger())
    let instance = SwiftFluentAliyunOssPlugin()
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion)
  }
}
