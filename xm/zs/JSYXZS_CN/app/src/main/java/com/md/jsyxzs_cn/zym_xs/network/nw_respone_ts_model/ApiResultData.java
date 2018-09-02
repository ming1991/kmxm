package com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model;

import com.google.gson.annotations.SerializedName;

public class ApiResultData<T> extends ApiResult {
	@SerializedName("Data")
	private T data;

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
