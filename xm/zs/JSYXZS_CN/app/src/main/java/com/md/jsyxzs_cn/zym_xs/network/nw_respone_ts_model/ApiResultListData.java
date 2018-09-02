package com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ApiResultListData<T> extends ApiResult {
	@SerializedName("Data")
	private List<T> data;

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

}
