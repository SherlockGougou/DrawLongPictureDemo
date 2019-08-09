package cc.shinichi.drawlongpicturedemo.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 工藤
 * @email 18883840501@163.com
 * cc.shinichi.drawlongpicturedemo.util
 * create at 2018/8/28  10:46
 * description:
 */
public class ImageUtil {

    public static Bitmap getImageBitmap(String srcPath, float maxWidth, float maxHeight) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int originalWidth = newOpts.outWidth;
        int originalHeight = newOpts.outHeight;

        float be = 1;
        if (originalWidth > originalHeight && originalWidth > maxWidth) {
            be = originalWidth / maxWidth;
        } else if (originalWidth < originalHeight && originalHeight > maxHeight) {
            be = newOpts.outHeight / maxHeight;
        }
        if (be <= 0) {
            be = 1;
        }

        newOpts.inSampleSize = (int) be;
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        newOpts.inDither = false;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }

        try {
            bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        } catch (OutOfMemoryError e) {
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            Runtime.getRuntime().gc();
        }

        if (bitmap != null) {
            bitmap = rotateBitmapByDegree(bitmap, getBitmapDegree(srcPath));
        }
        return bitmap;
    }

    public static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation =
                exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    degree = 0;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }

    public static int[] getWidthHeight(String imagePath) {
        if (TextUtils.isEmpty(imagePath)) {
            return new int[] { 0, 0 };
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            Bitmap originBitmap = BitmapFactory.decodeFile(imagePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 使用第一种方式获取原始图片的宽高
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // 使用第二种方式获取原始图片的宽高
        if (srcHeight <= 0 || srcWidth <= 0) {
            try {
                ExifInterface exifInterface = new ExifInterface(imagePath);
                srcHeight =
                    exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, ExifInterface.ORIENTATION_NORMAL);
                srcWidth =
                    exifInterface.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, ExifInterface.ORIENTATION_NORMAL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 使用第三种方式获取原始图片的宽高
        if (srcWidth <= 0 || srcHeight <= 0) {
            Bitmap bitmap2 = BitmapFactory.decodeFile(imagePath);
            if (bitmap2 != null) {
                srcWidth = bitmap2.getWidth();
                srcHeight = bitmap2.getHeight();
                try {
                    if (!bitmap2.isRecycled()) {
                        bitmap2.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return new int[] { srcWidth, srcHeight };
    }

    public static float getImageRatio(String imagePath) {
        int[] wh = getWidthHeight(imagePath);
        if (wh[0] > 0 && wh[1] > 0) {
            return (float) Math.max(wh[0], wh[1]) / (float) Math.min(wh[0], wh[1]);
        }
        return 1;
    }

    public static Bitmap resizeImage(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (!origin.isRecycled()) {
            origin.recycle();
        }
        return newBM;
    }

    public static String saveBitmapBackPath(Bitmap bm) throws IOException {
        String path = Environment.getExternalStorageDirectory() + "/ShareLongPicture/.temp/";
        File targetDir = new File(path);
        if (!targetDir.exists()) {
            try {
                targetDir.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fileName = "temp_LongPictureShare_" + System.currentTimeMillis() + ".jpeg";
        File savedFile = new File(path + fileName);
        if (!savedFile.exists()) {
            savedFile.createNewFile();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(savedFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return savedFile.getAbsolutePath();
    }
}