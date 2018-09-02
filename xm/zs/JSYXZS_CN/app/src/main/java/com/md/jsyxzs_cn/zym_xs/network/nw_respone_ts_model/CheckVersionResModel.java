package com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model;

public class CheckVersionResModel {
	/**
	 * window PC 版
	 */
	private String windows;
	/**
	 * 苹果版
	 */
	private String ios;
	/**
	 * 安卓版
	 */
	private String andriod;

	public String getWindows() {
		return windows;
	}

	public void setWindows(String windows) {
		this.windows = windows;
	}

	public String getIos() {
		return ios;
	}

	public void setIos(String ios) {
		this.ios = ios;
	}

	public String getAndriod() {
		return andriod;
	}

	public void setAndriod(String andriod) {
		this.andriod = andriod;
	}

	@Override
	public String toString() {
		return "CheckVersionResModel{" +
				"windows='" + windows + '\'' +
				", ios='" + ios + '\'' +
				", andriod='" + andriod + '\'' +
				'}';
	}
}
