package com.kmjd.android.zxhzm.alipaybill.utils;

import android.util.Log;

import java.util.Hashtable;

/**
 * The class for print log
 * 
 * @author Android-帅
 */
public class MyLogger {
	private final static boolean logFlag = false;

	private final static int logLevel = Log.VERBOSE;
	private static Hashtable<String, MyLogger> sLoggerTable = new Hashtable<String, MyLogger>();
	private String mAndroidName;
	// 不同开发人员的日志使用对象
	private static MyLogger zlog;
	private static MyLogger xlog;
	private static MyLogger hlog;
	private static MyLogger flog;
	private static MyLogger mlog;
	// 开发人员的名字
	private static final String ZYM = "Android-Star";
	private static final String XS = "Android-帅";
	private static final String HLN = "Android-黄";
	private static final String ZYF = "Android-张";
	private static final String MWB = "Android-彬";

	private MyLogger(String name) {
		mAndroidName = name;
	}

	@SuppressWarnings("unused")
	private static MyLogger getLogger(String className) {
		MyLogger classLogger = (MyLogger) sLoggerTable.get(className);
		if (classLogger == null) {
			classLogger = new MyLogger(className);
			sLoggerTable.put(className, classLogger);
		}
		return classLogger;
	}

	/**
	 * Purpose:Mark user one
	 * 
	 * @return
	 */
	public static MyLogger zLog() {
		if (zlog == null) {
			zlog = new MyLogger(ZYM);
		}
		return zlog;
	}
	
	/**
	 * Purpose:Mark user two
	 * 
	 * @return
	 */
	public static MyLogger xLog() {
		if (xlog == null) {
			xlog = new MyLogger(XS);
		}
		return xlog;
	}

	/**
	 * Purpose:Mark user three
	 * 
	 * @return
	 */
	public static MyLogger hLog() {
		if (hlog == null) {
			hlog = new MyLogger(HLN);
		}
		return hlog;
	}

	/**
	 * Purpose:Mark user four
	 *
	 * @return
	 */
	public static MyLogger fLog() {
		if (flog == null) {
			flog = new MyLogger(ZYF);
		}
		return flog;
	}

	/**
	 * Purpose:Mark user five
	 *
	 * @return
	 */
	public static MyLogger mLog() {
		if (mlog == null) {
			mlog = new MyLogger(MWB);
		}
		return mlog;
	}

	/**
	 * Get The Current Function Name
	 * 
	 * @return
	 */
	private String getFunctionName() {
		StackTraceElement[] sts = Thread.currentThread().getStackTrace();
		if (sts == null) {
			return null;
		}
		for (StackTraceElement st : sts) {
			if (st.isNativeMethod()) {
				continue;
			}
			if (st.getClassName().equals(Thread.class.getName())) {
				continue;
			}
			if (st.getClassName().equals(this.getClass().getName())) {
				continue;
			}
			return "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName() + " ]";
		}
		return null;
	}

	/**
	 * The Log Level:i
	 * 
	 * @param str
	 */
	public void i(Object str) {
		if (logFlag) {
			if (logLevel <= Log.INFO) {
				String name = getFunctionName();
				if (name != null) {
					Log.i(mAndroidName, name + " - " + str);
				} else {
					Log.i(mAndroidName, str.toString());
				}
			}
		}

	}

	/**
	 * The Log Level:d
	 * 
	 * @param str
	 */
	public void d(Object str) {
		if (logFlag) {
			if (logLevel <= Log.DEBUG) {
				String name = getFunctionName();
				if (name != null) {
					Log.d(mAndroidName, name + " - " + str);
				} else {
					Log.d(mAndroidName, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:V
	 * 
	 * @param str
	 */
	public void v(Object str) {
		if (logFlag) {
			if (logLevel <= Log.VERBOSE) {
				String name = getFunctionName();
				if (name != null) {
					Log.v(mAndroidName, name + " - " + str);
				} else {
					Log.v(mAndroidName, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:w
	 * 
	 * @param str
	 */
	public void w(Object str) {
		if (logFlag) {
			if (logLevel <= Log.WARN) {
				String name = getFunctionName();
				if (name != null) {
					Log.w(mAndroidName, name + " - " + str);
				} else {
					Log.w(mAndroidName, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param str
	 */
	public void e(Object str) {
		if (logFlag) {
			if (logLevel <= Log.ERROR) {
				String name = getFunctionName();
				if (name != null) {
					Log.e(mAndroidName, name + " - " + str);
				} else {
					Log.e(mAndroidName, str.toString());
				}
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param ex
	 */
	public void e(Exception ex) {
		if (logFlag) {
			if (logLevel <= Log.ERROR) {
				Log.e(mAndroidName, "error", ex);
			}
		}
	}

	/**
	 * The Log Level:e
	 * 
	 * @param log
	 * @param tr
	 */
	public void e(String log, Throwable tr) {
		if (logFlag) {
			String line = getFunctionName();
			Log.e(mAndroidName, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mAndroidName + line + ":] " + log + "\n", tr);
		}
	}
}