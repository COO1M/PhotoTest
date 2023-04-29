## 功能：
1.照片拍摄：调用系统摄像头拍摄、内置自定义摄像头拍摄(CameraX实现，预览包括手势缩放、点击对焦、前后置切换)。
2.照片裁剪：通过手势检测+Canvas+bitmap裁剪实现。
3.照片识别：文字识别，ML Kit的Text recognition V2实现。
4.照片美化：图片超分辨率增强，将Real-ESRGAN模型转换部署到Android端实现(由于移动设备性能所限，设定像素超过1百万的图片会先压缩到1百万像素再进行超分，减小计算量)。
5.照片管理：索引、删除、分享、媒体信息，MediaStore、File实现。


> 由于时间关系，有些逻辑不是很合理，或者没拆分好，有待修改。