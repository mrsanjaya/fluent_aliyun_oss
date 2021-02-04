
/// 上传进度
class PutObjectProgress {
  final int currentSize;
  final int totalSize;
  final double progress;

  PutObjectProgress({this.currentSize, this.totalSize, this.progress});
}
