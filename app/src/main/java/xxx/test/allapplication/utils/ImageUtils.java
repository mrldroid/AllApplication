package xxx.test.allapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.Size;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Description: 图片工具类 <br/>
 */

public class ImageUtils {
	public static Bitmap byte2Bitmap(byte[] data, Size picSize, int picWidth, int picHeight) {
		Bitmap bitmap = null;
		try {
			float ratio;
			float wRatio = (float) picSize.width / picWidth;
			float hRatio = (float) picSize.height / picHeight;
			if (wRatio < hRatio) {// 选择比例小的，可以裁出比需要的尺寸，一边宽另一边相等的图片
				ratio = wRatio;
			} else {
				ratio = hRatio;
			}
			Options opts = new Options();
			opts.inSampleSize = (int) (ratio > 1 ? ratio : 1);// 由于垃圾手机拍的图片可能小，必须要控制这个值不能小于1
			opts.inPreferredConfig = Config.RGB_565;
			opts.inPurgeable = true;
			opts.inInputShareable = true;
			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			//照片裁剪的部分
			Matrix m = new Matrix();
			int height = bitmap.getHeight();
			int width = bitmap.getWidth();
			if ((float) width / height > (float) picWidth / picHeight) {// 如果大于1.5代表这个图片宽了
				m.setScale((float) picHeight / height, (float) picHeight / height);
				int widthP = height * picWidth / picHeight;
				bitmap = Bitmap.createBitmap(bitmap, (width - widthP) / 2, 0, widthP, height, m, false);
			} else {// 长了
				m.setScale((float) picWidth / width, (float) picWidth / width);
				int hightP = width * picHeight / picWidth;
				bitmap = Bitmap.createBitmap(bitmap, 0, (height - hightP) / 2, width, hightP, m, false);
			}
		} catch (Exception e) {
			//oom
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 从资源中获取Bitmap
	 */
	public static Bitmap res2Bitmap(Context context, int drawableId) {
		Resources res = context.getResources();
		Bitmap bmp = BitmapFactory.decodeResource(res, drawableId);
		return bmp;
	}

	/** 保存方法 */
	public static void Bitmap2File(String filePath, Bitmap bm) {
		File f = new File(filePath);
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Bitmap转成byte[]
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * byte[]转成Bitmap
	 */
	public static Bitmap bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/**
	 * Bitmap转换成Drawable
	 */
	public static Drawable bitmap2Drawable(Context context, Bitmap bmp) {
		BitmapDrawable bd = new BitmapDrawable(context.getResources(), bmp);
		return bd;
	}

	/**
	 * 将Drawable转化为Bitmap
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	/**
	 * 获得圆角图片
	 */
	public static Bitmap getRoundedCorner(Bitmap bitmap, float roundPx) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, w, h);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 获得带倒影的图片
	 */
	public static Bitmap getShadowBitmap(Bitmap bitmap) {
		final int reflectionGap = 4;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
				h / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
				Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * Drawable缩放
	 */
	@SuppressWarnings("deprecation")
	public static Drawable getSmallDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		// drawable转换成bitmap
		Bitmap oldbmp = drawable2Bitmap(drawable);
		// 创建操作图片用的Matrix对象
		Matrix matrix = new Matrix();
		// 计算缩放比例
		float sx = ((float) w / width);
		float sy = ((float) h / height);
		// 设置缩放比例
		matrix.postScale(sx, sy);
		// 建立新的bitmap，其内容是对原bitmap的缩放后的图
		try {
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
					matrix, true);
			return new BitmapDrawable(newbmp);
		} catch (Exception e) {
			return drawable;

		}
	}

	/**
	 * 获取图片大小
	 *
	 * @param bitmap
	 * @return
	 */
	public static long getBitmapsize(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * 压缩bitmap的质量以kb为单位（按照指定的压缩大小来处理）如果不传，默认为100kb（耗时）
	 *
	 * @param bitmap
	 * @param compressSize
	 * @return
	 * @method
	 */
	public static byte[] getSmallBitmap(Bitmap bitmap, int size_kb) {
		if (bitmap == null) {
			return null;
		}
		if (size_kb == 0)
			size_kb = 100;// 默认压缩100kb
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		if (baos.toByteArray().length / 1024 < size_kb) {
			return baos.toByteArray();
		}
		while (baos.toByteArray().length / 1024 > size_kb) { // 循环判断如果压缩后图片是否大于compressSizekb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 6;// 每次都减少6
			if (options <= 10)
				break;
		}
		return baos.toByteArray();
	}

	/*
	 * 压缩图片
	 */
	public static Bitmap getSmallBitmap(String srcPath, int newWidth, int newHeight) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(srcPath, opt);
		opt.inJustDecodeBounds = false;
		int w = opt.outWidth;
		int h = opt.outHeight;
		int be = 1;
		if (w > h && w > newWidth) {
			be = w / newWidth;
		} else if (w < h && h > newHeight) {
			be = h / newHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		opt.inSampleSize = be;
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		return BitmapFactory.decodeFile(srcPath, opt);
	}

	/**
	 * 处理图片旋转
	 */
	public static int getBitmapRotate(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
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
			}
		} catch (IOException e) {
		}
		return degree;
	}

	/**
	 * 图片旋转
	 *
	 * @param bitmap
	 * @param rotate
	 * @return
	 */
	public static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
		if (bitmap == null)
			return null;
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	public static final int TAKE_PICTURE = 1 << 4;
	public static final int TAKE_GALLERY = 1 << 5;
	public static final int CROPPICTURE = 1 << 6;

	/**
	 * 选取照片在onactivityresult方法中如下获取图片 if (requestCode == ImageUtils.TAKE_GALLERY
	 * && resultCode == RESULT_OK && null != data) { Uri selectedImage =
	 * data.getData(); String[] filePathColumn = { MediaStore.Images.Media.DATA
	 * }; Cursor cursor = getContentResolver().query(selectedImage,
	 * filePathColumn, null, null, null); cursor.moveToFirst(); int columnIndex
	 * = cursor.getColumnIndex(filePathColumn[0]); String picturePath =
	 * cursor.getString(columnIndex); cursor.close(); Bitmap bitmap =
	 * BitmapFactory.decodeFile(picturePath); }
	 *
	 * @param activity
	 */
	public static void takePictureGallery(Activity activity) {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		activity.startActivityForResult(i, TAKE_GALLERY);
	}

	/**
	 * 通过手机照相获取图片
	 *
	 * @param activity
	 * @param fileTarget如果不为空
	 *            ，图片将存到这个地址，如果为空在onactivityresult使用如下代码获取返回的bitmap , if
	 *            (requestCode == ImageUtils.TAKE_PICTURE && resultCode ==
	 *            RESULT_OK && null != data) { bitmap = (Bitmap)
	 *            data.getExtras().get("data"); }
	 */
	public static void takePictureCamera(Activity activity, Uri fileTarget) {
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		cameraIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		if (fileTarget != null) {
			cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, fileTarget);
		}
		activity.startActivityForResult(cameraIntent, TAKE_PICTURE);
	}

	/**
	 * 裁剪图片在onactivityresult中使用如下方法获取图片 if (requestCode ==
	 * ImageUtils.CROPPICTURE && resultCode == RESULT_OK&& null != data) bitmap
	 * = (Bitmap) data.getExtras().get("data");
	 *
	 * @param activity
	 * @param path
	 */
	public static void cropPicture(Activity activity, Uri uri, int outputX, int outputY, int xweight, int yweight) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", xweight);
		intent.putExtra("aspectY", yweight);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, CROPPICTURE);
	}


	/**
	 * 获取视频文件截图
	 *
	 * @param path 视频文件的路径
	 * @return Bitmap 返回获取的Bitmap
	 */
	public static Bitmap getVideoThumb(String path) {
		MediaMetadataRetriever media = new MediaMetadataRetriever();
		media.setDataSource(path);
		return media.getFrameAtTime();
	}
	/**
	 * 获取视频文件缩略图 API>=8(2.2)
	 *
	 * @param path 视频文件的路径
	 * @param kind 缩略图的分辨率：MINI_KIND、MICRO_KIND、FULL_SCREEN_KIND
	 * @return Bitmap 返回获取的Bitmap
	 */
	public static Bitmap getVideoThumb2(String path, int kind) {
		return ThumbnailUtils.createVideoThumbnail(path, kind);
	}
	public static Bitmap getVideoThumb2(String path) {
		return getVideoThumb2(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
	}


	public static  Bitmap getThumbnail(String path) {
		if (path == null) return null;
		return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
	}
}
