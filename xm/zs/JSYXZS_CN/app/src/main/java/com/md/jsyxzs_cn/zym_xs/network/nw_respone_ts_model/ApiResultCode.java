package com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model;

public enum ApiResultCode {
	Success(0),
	BadRequest(400),
	Unauthorized(401),
	NotFound(404);

	private int code;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	private ApiResultCode(int code) {
		this.code = code;
	}
}
