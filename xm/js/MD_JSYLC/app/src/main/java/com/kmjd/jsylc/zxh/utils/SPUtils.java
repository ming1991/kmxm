
package com.kmjd.jsylc.zxh.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SPUtils {

	public enum SP_KEY{
		/*速度最快的域名*/
		FAST_DOMAIN,
		/*会员对应的验证参数*/
		LOGIN_VERIFICATION,
		/*会员登录状态*/
		MEMBER_LOGIN_STATE,
		/*免费影城上次进入的时间*/
		MFYC_SUPER_TIME,
		/*讨论区上次进入的时间*/
		TLQ_SUPER_TIME,
		/*登录页讨论区上次进入的时间*/
		LOGIN_TLQ_SUPER_TIME,
		/*登录页的轮播图用不用10s限制*/
		LOGIN_BBS,
		/*登录页的菜单键是否在显示*/
		LOGIN_MENU_IS_SHOWING,
		/*主页面的菜单键是否显示*/
		HOME_MENU_IS_SHOWING,
		/*主页面会员中心菜单是否显示*/
		HOME_HYZX_MENU_IS_SHOWING,
		/*自定义的Toolbar的高度*/
		TOOLBAR_HEIGHT,
		/*RadioButtom的高度*/
		RADIOBUTTOM_HEIGHT,
		/*个人信息栏的高度*/
		PERSONHEIGHT,
		/*home界面上面按钮的大小*/
		HOMEUP,
		/*home界面下面按钮的大小*/
		HOMEDOWN,
		/*RSA私钥*/
		RSA_PRIVATE,
		/*是否是测试帐号*/
		IS_TEST_ACCOUNTID,
		/*客服中心-回电客服的弹框是否出现*/
		IS_APPEAR,
		/*loginWebFragment里是否有web弹窗出现*/
		LOGINWEBFRAGMENT_WEB_DIALOG,
		PUBLIC_ID//公告信息ID
	}
	private static final String SP_NAME = "jsylc_sp";
	public static void put(Context context, String key, Object value) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		Editor edit = null;
		if (null != sp) {
			edit = sp.edit();
		}
		if (null != edit) {
			if (value instanceof String) {
				edit.putString(key, (String) value);
			} else if (value instanceof Integer) {
				edit.putInt(key, (Integer) value);
			} else if (value instanceof Boolean) {
				edit.putBoolean(key, (boolean) value);
			} else if (value instanceof Long){
				edit.putLong(key, (Long) value);
			}
		/*
		这两个方法的区别在于：
		 1. apply没有返回值而commit返回boolean表明修改是否提交成功
		 2. apply是将修改数据原子提交到内存, 而后异步真正提交到硬件磁盘, 而commit是同步的提交到硬件磁盘，因此，
		 在多个并发的提交commit的时候，他们会等待正在处理的commit保存到磁盘后在操作，从而降低了效率。
		 而apply只是原子的提交到内容，后面有调用apply的函数的将会直接覆盖前面的内存数据，这样从一定程度上提高了很多效率。
		 3. apply方法不会提示任何失败的提示。
		 由于在一个进程中，sharedPreference是单实例，一般不会出现并发冲突，如果对提交的结果不关心的话，
		 建议使用apply，当然需要确保提交成功且有后续操作的话，还是需要用commit的
		 */
			edit.apply();
		}
	}

	public static String getString(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (null != sp) {
			return sp.getString(key, "");
		} else {
			return "";
		}
	}

	public static int getInt(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (null != sp) {
			return sp.getInt(key, 0);
		} else {
			return 0;
		}
	}

	public static boolean getBoolean(Context context, String key, boolean strDefault) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);

		if (null != sp) {
			return sp.getBoolean(key, strDefault);
		} else {
			return strDefault;
		}
	}

	public static long getLong(Context context, String key, long defaultValue) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (null != sp) {
			return sp.getLong(key, defaultValue);
		} else {
			return defaultValue;
		}
	}
	public static void clear(Context context, String key) {
		SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (null != sp) {
			sp.edit().remove(key).commit();
		}
	}
}
