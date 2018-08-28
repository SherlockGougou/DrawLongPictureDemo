##Demo地址：
https://github.com/SherlockGougou/DrawLongPictureDemo

# 先看需求：

1.用户点击生成长图按钮，弹出等待框，后台生成一张长图。

2.用户展示界面和最终生成的长图，布局完全不一样，所以不能通过直接将view转换成bitmap，或者长截图来实现。

3.生成的长图，头部加上公司logo，尾部加上二维码。

# 难点分析：

1.后台进行。

2.长图保证清晰度，并且不能过大，过大可能会分享失败。


# 效果展示：
![Screenshot_2018-08-11-15-49-31-798_十六番旅行.png](https://upload-images.jianshu.io/upload_images/1710902-e3772d34c4612cd7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2018-08-11-15-49-39-284_十六番旅行.png](https://upload-images.jianshu.io/upload_images/1710902-5529b51a1faf5c88.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2018-08-11-15-49-45-423_十六番旅行.png](https://upload-images.jianshu.io/upload_images/1710902-584274b1e1f19f4c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2018-08-11-15-49-55-954_十六番旅行.png](https://upload-images.jianshu.io/upload_images/1710902-5a36f13ab4cdacd2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#具体实现：

## 长图描述（纯手画，别介意 T_T）
![image.png](https://upload-images.jianshu.io/upload_images/1710902-3a41ee5a3fde30ce.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


####1.准备数据：

- a.所需的文字内容

- b.所需的图片（必须下载到本地，你可以开启一个线程进行图片的下载，在图片下载完成后，再进行绘制的操作）

####2.大致流程：

创建一个类继承自LinearLayout，初始化绑定xml布局文件： （布局中需要包含的是头部view、底部view等宽高固定的view；文字等高度wrap_content的view需要在代码中动态绘制出来，不然高度会有问题，下文有说明）

```
public LiveDrawLongPictureUtil(Context context) { super(context); init(context);}

public LiveDrawLongPictureUtil(Context context, AttributeSet attrs) { super(context, attrs); init(context);}

public LiveDrawLongPictureUtil(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);init(context);}

private void init(Context context) {

this.context = context;

// 初始化各个控件

rootView = LayoutInflater.from(context) .inflate(R.layout.layout_draw_long_picture, this, false);

llTopView = rootView.findViewById(R.id.llTopView);// 头部view，高度固定，可直接获取到对应的bitmap

llContent = rootView.findViewById(R.id.llContent);// 各种固定高度的view，高度固定，可直接获取到对应的bitmap

llBottomView = rootView.findViewById(R.id.llBottomView);// 底部view，高度固定，可直接获取到对应的bitmap

// 测量各个块儿的view的宽高（这步很重要，后面需要用到宽高数据，进行画布的创建）

    layoutView(llTopView);
    layoutView(llContent);
    layoutView(llBottomView);

    widthTop = llTopView.getMeasuredWidth();
    heightTop = llTopView.getMeasuredHeight();

    widthBottom = llBottomView.getMeasuredWidth();
    heightBottom = llBottomView.getMeasuredHeight();
}

// 测量view宽高的方法（仅测量父布局）
  private void measureView(View v) {
    int width = HomepageUtil.getPhoneWid();
    int height = HomepageUtil.getPhoneHei();

v.layout(0, 0, width, height);
    int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
    int measuredHeight = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    v.measure(measuredWidth, measuredHeight);
    v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
  }
```
###获取第一步得到的数据，包括图片，需要下载完毕，这是前提；

- a.计算头部view、底部view、中间文字内容、中间图片，以及其他view的总高度（px）和宽度（px）；

- b.创建一个空白的bitmap，使用bitmap的createBitmap方法，传入第一步计算得到的宽高，Config可以随意，推荐RGB_565（省内存）：

```
Bitmap bitmapAll = Bitmap.createBitmap(allBitmapWidth, allBitmapHeight, Bitmap.Config.RGB_565);// 创建所需大小的bitmap

Canvas canvas = new Canvas(bitmapAll);// 创建空白画布
canvas.drawColor(Color.WHITE);// 背景颜色
Paint paint = new Paint();// 画笔
paint.setAntiAlias(true);// 设置抗锯齿
paint.setDither(true);// 防抖动
paint.setFilterBitmap(true);// 设置允许过滤
```
- c.把view从顶部到底部的顺序，一块块绘制到画布上；

- d.全部view绘制完毕后，保存bitmapAll到本地文件，如需压缩，可压缩到指定大小和尺寸；

- e.进行分享的操作。至此，基本过程就这样。 


以下是Demo效果：

![Screenshot_2018-08-28-12-19-08-117_cc.shinichi.drawlongpicturedemo.png](https://upload-images.jianshu.io/upload_images/1710902-874462b6ae9e8702.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2018-08-28-12-19-13-060_cc.shinichi.drawlongpicturedemo.png](https://upload-images.jianshu.io/upload_images/1710902-d871e55723187edd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2018-08-28-12-19-19-900_cc.shinichi.drawlongpicturedemo.png](https://upload-images.jianshu.io/upload_images/1710902-eac7bed7287fc26a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![Screenshot_2018-08-28-12-19-27-860_com.miui.gallery.png](https://upload-images.jianshu.io/upload_images/1710902-2fb34b1a5c85aa06.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


##源码请查阅github：(如果觉得有用，记得收藏哦~)
https://github.com/SherlockGougou/DrawLongPictureDemo
