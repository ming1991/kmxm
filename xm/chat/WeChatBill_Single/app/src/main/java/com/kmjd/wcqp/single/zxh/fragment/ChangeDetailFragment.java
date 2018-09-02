package com.kmjd.wcqp.single.zxh.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.model.ChangeDetailBean;
import com.kmjd.wcqp.single.zxh.model.rxbusBean.RxBusCurrentUploadInfo;
import com.kmjd.wcqp.single.zxh.util.ItemIntervalDecorationForRecyclerView;
import com.kmjd.wcqp.single.zxh.util.RxBus;

import net.lightbody.bmp.core.har.HarEntry;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class ChangeDetailFragment extends Fragment {

	private static String TAG = "Android-Star";
	private View contentViw;
	@BindView(R.id.rv_ChangeDetailFragment)
	RecyclerView mRecyclerView;
	public ChangeDetailAdapter changeDetailAdapter;
	private List<HarEntry> harEntryList = new ArrayList<>(20);
	private List<HarEntry> afterFilterHarEntryList = new ArrayList<>(20);
	public List<ChangeDetailBean> changeDetailBeanList = new ArrayList<>(20);

	@BindView(R.id.tv_title_fcd)
	TextView tv_title_fcd;
	private Disposable mSubscribe;

	public ChangeDetailFragment() {
		// Required empty public constructor
	}

	public static ChangeDetailFragment newInstance() {
		ChangeDetailFragment changeDetailFragment = new ChangeDetailFragment();
		return changeDetailFragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		if (null == contentViw) {
			contentViw = inflater.inflate(R.layout.fragment_change_detail, container, false);
		}
		ButterKnife.bind(this, contentViw);
		return contentViw;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		mRecyclerView.addItemDecoration(
				new ItemIntervalDecorationForRecyclerView(getContext(),
						ItemIntervalDecorationForRecyclerView.VERTICAL_LIST));
		mRecyclerView.setAdapter(changeDetailAdapter = new ChangeDetailAdapter());

		initView();
	}

	private void initView() {
		Flowable<RxBusCurrentUploadInfo> flowable = RxBus.getInstance().register(RxBusCurrentUploadInfo.class);
		mSubscribe = flowable.subscribe(new Consumer<RxBusCurrentUploadInfo>() {
			@Override
			public void accept(@NonNull RxBusCurrentUploadInfo rxBusCurrentUploadInfo) throws Exception {
				state = rxBusCurrentUploadInfo.getState();
				currentUrl = "url：https://kmjd." + rxBusCurrentUploadInfo.getCurrentUrl().split("//")[1].split("\\.")[0] + ".com";
				Log.d(TAG, "                  当前" + currentUrl);
				expectationContent = rxBusCurrentUploadInfo.getExpectationContent();
				tv_title_fcd.setText(state + "\n" + currentUrl);
				formatContent(expectationContent);
				changeDetailAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		tv_title_fcd.setText(state + "\n" + currentUrl);
		formatContent(expectationContent);
		changeDetailAdapter.notifyDataSetChanged();
	}



	private static String currentUrl = "";
	private static String state = "";
	private static String expectationContent = "";

	private void formatContent(String content) {
		changeDetailBeanList.clear();
		if (!TextUtils.isEmpty(content)) {
			String[] firstArray = content.split("allList.push" + " \\(");
			if (null != firstArray && firstArray.length > 0) {
				for (int i = 0; i < firstArray.length; i++) {
					if (i == 0) {
						continue;
					}
					//从第1个元素开始，抽取字符串
					String afterWipeLastBracketString = firstArray[i].substring(0, firstArray[i].indexOf(");"));
					ChangeDetailBean changeDetailBean = JSON.parseObject(afterWipeLastBracketString, ChangeDetailBean.class);
					changeDetailBeanList.add(changeDetailBean);
				}
			}
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}

	@Override
	public void onDetach() {
		super.onDetach();

	}


	public class ChangeDetailAdapter extends RecyclerView.Adapter<ChangeDetailAdapter.TradingRecordViewHolder> {

		@Override
		public TradingRecordViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			//inflate(R.layout.item_layout_change_detail,parent,false)
			//第一个参数是要解析成view的xml文件，
			//第二个参数是解析得到的view将被添加到的那个ViewGroup，可以为一个上下文（添加到上下文对应的布局中），
			//是否添加还需要看第三个参数的值
			//第三个参数表示是否添加，true表示添加，false表示不添加，没有第三个参数就默认为true
			//返回值将被ViewHolder接收
			return new TradingRecordViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_layout_change_detail, parent, false));
		}

		@Override
		public void onBindViewHolder(TradingRecordViewHolder holder, int position) {
			ChangeDetailBean changeDetailBean = changeDetailBeanList.get(position);
			String title = changeDetailBean.getTrans_state_name();
			String time = changeDetailBean.getCreatetime();
			String money = String.format("%.2f",Float.valueOf(changeDetailBean.getPaynum()) / 100.0f);
			String type = changeDetailBean.getType();
			holder.tv_title.setText(title);
			holder.tv_time.setText(time);
			switch (type) {
				case "1":
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						holder.tv_amount_money.setTextColor(getResources().getColor(R.color.green, null));
					} else {
						holder.tv_amount_money.setTextColor(getResources().getColor(R.color.green));
					}
					holder.tv_amount_money.setText("+" + money);
					break;
				case "2":
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
						holder.tv_amount_money.setTextColor(getResources().getColor(R.color.black, null));
					} else {
						holder.tv_amount_money.setTextColor(getResources().getColor(R.color.black));
					}
					holder.tv_amount_money.setText("-" + money);
					break;
				default:
					holder.tv_amount_money.setText("--");
					break;
			}

			holder.itemView.setOnClickListener(new ItemOnClickListener(position));

		}

		@Override
		public int getItemCount() {
			return null != changeDetailBeanList && changeDetailBeanList.size() > 0 ? changeDetailBeanList.size() : 0;
		}

		class TradingRecordViewHolder extends RecyclerView.ViewHolder {


			@BindView(R.id.tv_title_ChangeDetail)
			TextView tv_title;
			@BindView(R.id.tv_time_ChangeDetail)
			TextView tv_time;
			@BindView(R.id.tv_amount_money_ChangeDetail)
			TextView tv_amount_money;
			View itemView;

			TradingRecordViewHolder(View itemView) {
				super(itemView);
				this.itemView = itemView;
				ButterKnife.bind(this, itemView);
			}
		}


		class ItemOnClickListener implements View.OnClickListener {

			int position;
			View dialogRootView;

			public ItemOnClickListener(int position) {
				this.position = position;
				dialogRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_detail, null);
				ButterKnife.bind(this, dialogRootView);
			}

			@BindView(R.id.tv_amount_name)
			TextView tv_amount_name;
			@BindView(R.id.tv_amount_value)
			TextView tv_amount_value;
			@BindView(R.id.tv_type_value)
			TextView tv_type_value;
			@BindView(R.id.tv_time_value)
			TextView tv_time_value;
			@BindView(R.id.tv_transaction_number_value)
			TextView tv_transaction_number_value;
			@BindView(R.id.tv_remaining_change_value)
			TextView tv_remaining_change_value;
			@BindView(R.id.tv_remark_name)
			TextView tv_remark_name;
			@BindView(R.id.tv_remark_vlaue)
			TextView tv_remark_vlaue;

			@Override
			public void onClick(View v) {
				dialogRootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_detail, null);
				ButterKnife.bind(this, dialogRootView);
				ChangeDetailBean changeDetailBean = changeDetailBeanList.get(position);

				String type = changeDetailBean.getType();
				String paynum = String.format("%.2f",Float.valueOf(changeDetailBean.getPaynum()) / 100.0f);
				String balance_source = changeDetailBean.getBalance_source();
				String time = changeDetailBean.getCreatetime();
				String transid = changeDetailBean.getTransid();
				String balance = String.format("%.2f",Float.valueOf(changeDetailBean.getBalance()) / 100.0f);
				String remark = changeDetailBean.getExplain();
				switch (type) {
					case "1":
						tv_amount_name.setText(R.string.net_amount);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
							tv_amount_value.setTextColor(getResources().getColor(R.color.green, null));
						} else {
							tv_amount_value.setTextColor(getResources().getColor(R.color.green));
						}
						break;
					case "2":
						tv_amount_name.setText(R.string.change_off_amount);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
							tv_amount_value.setTextColor(getResources().getColor(R.color.black, null));
						} else {
							tv_amount_value.setTextColor(getResources().getColor(R.color.black));
						}
						break;
					default:
						break;
				}
				tv_amount_value.setText(paynum + "");
				tv_type_value.setText(balance_source);
				tv_time_value.setText(time);
				tv_transaction_number_value.setText(transid);
				tv_remaining_change_value.setText(balance + "");
				if (remark.equals("")) {
					tv_remark_vlaue.setText(R.string.no_remark);
				} else {
					tv_remark_vlaue.setText(remark);
				}
				//四种原生对话框
				//AlertDialog, ProgressDialog, DatePickerDialog, TimePickerDialog
				AlertDialog alertDialog = null;
				AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
				builder.setView(dialogRootView);
				alertDialog = builder.create();
				alertDialog.show();
			}
		}
	}

	@Override
	public void onDestroy() {
		if (null != mSubscribe && !mSubscribe.isDisposed()){
			mSubscribe.dispose();
		}
		super.onDestroy();
	}

}
