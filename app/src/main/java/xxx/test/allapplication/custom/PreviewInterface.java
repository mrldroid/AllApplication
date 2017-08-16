package xxx.test.allapplication.custom;

import android.hardware.Camera;

import java.util.List;

public interface PreviewInterface {
	/**
	 * 重拍更新ui
	 */
	public void updateUIforRephoto();
	/**
	 * 刷新闪光灯状态
	 */
	public void updateFlash();
	/**
	 * 拍照回调Camera.PictureCallback()
	 */
	public void onPictureTaken(byte[] data, Camera cam);
	/**
	 * 设置对焦
	 */
	public void setAutoFocus(boolean isAutoFoces);
	/**
	 * 获取cameraAcitivty的isEdit域
	 */
	public boolean isCameraEdit();
	/**
	 * 设置cameraAcitivty的Param
	 */
	public void updateParam(String key, String value);
	/**
	 * 刷新CloudEd的ui
	 */
	public void updateCloudEdUI(List<String> supported_flash_modes);

}
