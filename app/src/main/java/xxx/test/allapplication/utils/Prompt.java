package xxx.test.allapplication.utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import xxx.test.allapplication.App;

/**
 * 提示
 * 
 * @author lee
 * 
 */
public class Prompt {

	private static Toast toast;

	/**
	 * 
	 * @param tipStr
	 *            提示内容
	 */
	public static void showToast(String tipStr) {
		if (!TextUtils.isEmpty(tipStr)) {
			if (toast == null) {
				toast = Toast.makeText(App.getInstance(), tipStr, Toast.LENGTH_SHORT);
			} else {
				toast.setText(tipStr);
			}
			toast.show();
		}
	}

	public static void showToastSubthread(Context context, final String tipStr) {
		Handler handler = new Handler(App.getInstance().getMainLooper());
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (!TextUtils.isEmpty(tipStr)) {
					if (toast == null) {
						toast = Toast.makeText(App.getInstance(), tipStr, Toast.LENGTH_SHORT);
					} else {
						toast.setText(tipStr);
					}
					toast.show();
				}
			}
		});
	}

	/**
	 * 自定义文本内入，例如：
	 */
	public static void showToast(View view) {
		if (toast == null) {
			toast = Toast.makeText(App.getInstance(), "", Toast.LENGTH_SHORT);
		}
		toast.setGravity(Gravity.CENTER, toast.getXOffset(), toast.getYOffset());
		LinearLayout toastView = (LinearLayout) toast.getView();
		toastView.addView(view, 0);
		toast.show();
	}
}
