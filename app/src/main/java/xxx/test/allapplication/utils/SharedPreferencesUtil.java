package xxx.test.allapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;
import java.util.Map.Entry;

/**
 * sharepreferences 存取操作类
 * @author zhangjianqin
 *
 */
public class SharedPreferencesUtil {
	private SharedPreferences preferences;
	private static SharedPreferencesUtil instance = null;

	public static SharedPreferencesUtil getinstance() {

		if (instance == null) {
			instance = new SharedPreferencesUtil();
		}
		return instance;

	}

	/**
	 * String类型存储
	 * @param context
	 * @param PREFS_NAME
	 *            默认--"" Desired preferences file. If a preferences file by this
	 *            name does not exist, it will be created when you retrieve an
	 *            editor (SharedPreferences.edit()) and then commit changes
	 *            (Editor.commit()).
	 * @param map
	 */
	public void SetStringSharedPreferences(Context context,
			Map<String, String> map) {
		preferences = context.getSharedPreferences("checkauto3", 0);
		Editor editor = preferences.edit();
		editor = preferences.edit();
		if (map != null) {
			for (Entry<String, String> e : map.entrySet()) {
				editor.putString(e.getKey(), e.getValue());
				editor.commit();
			}
		}

	}

	public void SetStringSharedPreferences(Context context,String spName,
										   Map<String, String> map) {
		if (context!=null){
			preferences = context.getSharedPreferences(spName, 0);
			Editor editor = preferences.edit();
			editor = preferences.edit();
			if (map != null) {
				for (Entry<String, String> e : map.entrySet()) {
					editor.putString(e.getKey(), e.getValue());
					editor.commit();
				}
			}
		}
	}

	/**
	 * Integer类型存储
	 * @param context
	 * @param PREFS_NAME
	 *            默认--"" Desired preferences file. If a preferences file by this
	 *            name does not exist, it will be created when you retrieve an
	 *            editor (SharedPreferences.edit()) and then commit changes
	 *            (Editor.commit()).
	 * @param map
	 */
	public void SetIntegerSharedPreferences(Context context, String PREFS_NAME,
			Map<String, Integer> map) {
		preferences = context.getSharedPreferences(PREFS_NAME, 0);
		Editor editor = preferences.edit();
		if (map != null) {
			for (Entry<String, Integer> e : map.entrySet()) {
				editor.putInt(e.getKey(), e.getValue());
				editor.commit();
			}
		}

	}
	/**
	 * Boolean类型存储
	 * @param context
	 * @param PREFS_NAME
	 *            默认--"" Desired preferences file. If a preferences file by this
	 *            name does not exist, it will be created when you retrieve an
	 *            editor (SharedPreferences.edit()) and then commit changes
	 *            (Editor.commit()).
	 * @param map
	 */
	public void SetBooleanSharedPreferences(Context context, String PREFS_NAME,
			Map<String, Boolean> map) {
		preferences = context.getSharedPreferences(PREFS_NAME, 0);
		Editor editor = preferences.edit();
		if (map != null) {
			for (Entry<String, Boolean> e : map.entrySet()) {
				editor.putBoolean(e.getKey(), e.getValue());
				editor.commit();
			}
		}

	}
	/**
	 * String类型读取
	 * @param context
	 * @param name
	 *            获取对应值名称
	 * @return result 返回结果
	 */
	public String GetStringSharedPreferences(Context context, String name) {
		String result = "";
		preferences = context.getSharedPreferences("checkauto3", 0);
		result = preferences.getString(name, "");
		return result;

	}

	/**
	 * Integer类型读取
	 * @param context
	 * @param name
	 *            获取对应值名称
	 * @return result 返回结果
	 */
	public int GetIntegerSharedPreferences(Context context, String name) {
		int result = 0;
		preferences = context.getSharedPreferences("checkauto3", 0);
		result = preferences.getInt(name, 0);
		return result;

	}
	/**
	 * Boolean类型读取
	 * @param context
	 * @param name
	 *            获取对应值名称
	 * @return result 返回结果
	 */
	public boolean GetBooleanrSharedPreferences(Context context, String name) {
		boolean result;
		preferences = context.getSharedPreferences("checkauto3", 0);
		result = preferences.getBoolean(name, false);
		return result;

	}
}
