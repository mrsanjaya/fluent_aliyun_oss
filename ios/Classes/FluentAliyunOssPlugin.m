#import "FluentAliyunOssPlugin.h"
#if __has_include(<fluent_aliyun_oss/fluent_aliyun_oss-Swift.h>)
#import <fluent_aliyun_oss/fluent_aliyun_oss-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "fluent_aliyun_oss-Swift.h"
#endif

@implementation FluentAliyunOssPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    [OSSPutClient shareInstall].channel = [FlutterMethodChannel methodChannelWithName:@"fluent_aliyun_oss" binaryMessenger:registrar.messenger];
    [[OSSPutClient shareInstall].channel setMethodCallHandler:^(FlutterMethodCall * _Nonnull call, FlutterResult  _Nonnull result) {
        NSDictionary *arguments = (NSDictionary *)call.arguments;
        if ([call.method isEqualToString:@"init"]) {
            AliyunUploadManager *manager = [AliyunUploadManager managerWithArguments:call.arguments];
            [[OSSPutClient shareInstall].clients setObject:manager forKey:[arguments objectForKey:@"clientKey"]];
            result(@"true");
        }else if ([call.method isEqualToString:@"getPlatformVersion"]){
            //获取系统版本号
            result([NSString stringWithFormat:@"iOS%@",[UIDevice currentDevice].systemVersion]);
        }else if ([call.method isEqualToString:@"putObjectSync"] || [call.method isEqualToString:@"putObject"]){
           NSString *taskId = [[OSSPutClient shareInstall] invokeMethod:call.method arguments:arguments];
            result(taskId);
//            [AliyunUploadManager updateWithArguments:call.arguments channel:weakChannel];
        }else if([call.method isEqualToString:@"signUrl"]){
            AliyunUploadManager *manager = [OSSPutClient.shareInstall.clients objectForKey:arguments[@"clientKey"]];
            NSString *url = [manager signUrl:arguments];
            result(url);
        }
    }];
}
@end

