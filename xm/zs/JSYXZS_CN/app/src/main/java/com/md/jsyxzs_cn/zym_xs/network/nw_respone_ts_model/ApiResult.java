package com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model;

import com.google.gson.annotations.SerializedName;

public class ApiResult {
	@SerializedName("Code")
	private int code;
	@SerializedName("Message")
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getIsSuccessCode() {
		return code == ApiResultCode.Success.getCode();
	}

	@Override
	public String toString() {
		return "ApiResult{" +
				"code=" + code +
				", message='" + message + '\'' +
				'}';
	}
}
