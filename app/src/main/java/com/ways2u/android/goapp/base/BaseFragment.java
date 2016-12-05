package com.ways2u.android.goapp.base;

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.trello.rxlifecycle2.components.support.RxFragment;
import java.lang.ref.WeakReference;

/**
 *
 */
public abstract class BaseFragment extends RxFragment implements View.OnClickListener {
	protected FragmentHandler handler;
	private String TAG;

	//private AppProgressDialog pd;


	protected BaseActivity activity;
	private View rootView;

	private static long lastClickTime;
	protected LayoutInflater inflater;

	protected boolean hasLoad;//只加载一次
	protected boolean hasPrepared;//


	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}
/*
	public void showProgressDialog(String message, boolean isCancelable) {
		if (pd == null) {
			pd = new AppProgressDialog(getActivity());
			pd.setCanceledOnTouchOutside(false);
		}
		pd.setCancelable(isCancelable);
		pd.setMessage(message);
		if (!getActivity().isFinishing()) {
			pd.show();
		}
	}

	public void dimissProgressDialog() {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}

	}

	public void cancelProgressDialog() {
		if (pd != null && pd.isShowing()) {
			pd.cancel();
		}

	}
*/
	public static class FragmentHandler extends Handler {
		WeakReference<BaseFragment> mActivity;

		FragmentHandler(BaseFragment activity) {
			mActivity = new WeakReference<BaseFragment>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			mActivity.get().undateUI(msg);
		}
	}

	/**
	 * @param title
	 */
	public void setTitle(CharSequence title) {
		if(activity!=null){
			activity.setTitle(title);
		}
	}

	/**
	 * @param resid
	 */
	public void setTitle(int resid) {
		if(activity!=null){
			activity.setTitle(resid);
		}
	}

	/**
	 * 子Activity 实现该方法 处理主线程UI
	 * 
	 * @param msg
	 */
	public abstract void undateUI(Message msg);
	protected abstract int getLayoutId();
	protected abstract void initView();
	protected abstract void setListener();
	protected abstract void initData();
	protected void lazyLoad(){}

	@Override
	public void onClick(View v) {

	}


	/**
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.inflater = inflater;
		rootView = inflater.inflate(getLayoutId(), container, false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//context = getActivity().getApplicationContext();
		activity = (BaseActivity) getActivity();
		TAG = getClass().getSimpleName();
		handler = new FragmentHandler(this);

		initView();
		setListener();
		initData();

		hasPrepared = true;
		if (getUserVisibleHint()) {//防止页面在不可见时加载
			onVisible();//第一个页面在执行onActivityCreated前就已调用setUserVisibleHint,对用户可见getUserVisibleHint=true,
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
//            isVisible = true;
			onVisible();
		} else {
//            isVisible = false;
			onInvisible();
		}
	}

	protected void onInvisible() {}

	protected void onVisible() {
		if (hasPrepared && !hasLoad) {
			hasLoad = true;
			lazyLoad();
		}
	}



	@Override
	public void onDestroy() {
		/*
		if(pd!=null)
		{
			pd.dismiss();
		}
		*/
		activity = null;
		rootView = null;
		//msg = null;
		if(handler!=null){
			handler.removeCallbacksAndMessages(null);
			handler.mActivity.clear();
		}
		//pd = null;
		activity = null;
		handler = null;
		super.onDestroy();
		//System.gc();
	}

	public void printError(String Message) {
		if (Message != null) {
			Log.e(TAG, Message);
		}
	}

	public String getResStr(int id) {
		String str = "";
		try {
			str = getResources().getString(id);
		} catch (NotFoundException e) {

			e.printStackTrace();
			str = "";
		}
		return str;
	}

	private Toast toast;

	public <T extends View> T findViewById(@IdRes int id) {
		return (T)rootView.findViewById(id);
	}

	public <T extends View> T find(@IdRes int id) {
		return (T) findViewById(id);
	}



	/**
	 * 获取文本框的文本
	 * 
	 * @param e
	 * @return
	 */
	public String getEditTextString(EditText e) {
		String str = "";
		str = (e == null ? "" : e.getText().toString().trim());
		return str;
	}

	/**
	 * 隐藏视图
	 * 
	 * @param v
	 */
	public void hideView(View v) {
		if (v != null) {
			v.setVisibility(View.GONE);
		}
	}

	/**
	 * 隐藏视图但它占据了空间
	 * 
	 * @param v
	 */
	public void hideViewHasSpace(View v) {
		if (v != null) {
			v.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 显示视图
	 * 
	 * @param v
	 */
	public void showView(View v) {
		if (v != null) {
			v.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 激活视图
	 * 
	 * @param v
	 */
	public void enableView(View v) {
		if (v != null) {
			v.setEnabled(false);
		}
	}

	/**
	 * 禁用视图
	 * 
	 * @param v
	 */
	public void disableView(View v) {
		if (v != null) {
			v.setEnabled(true);
		}
	}

	/**
	 * 显示Toast提示
	 * 
	 * @param s
	 */
	public void showToatWithShort(String s) {
		if (toast == null && getActivity()!=null) {
			toast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);
		}
		toast.setText(s);
		toast.show();
	}

	/**
	 * 显示Toast提示
	 * 
	 * @param s
	 */
	public void showToatWithLong(String s) {
		if (toast == null) {
			toast = Toast.makeText(getActivity(), "", Toast.LENGTH_LONG);
		}
		toast.setText(s);
		toast.show();
	}

	/**
	 * 检测文本编辑器是否有输入内容
	 * 
	 * @param et
	 * @return true代表没有输入内容
	 */
	public boolean checkEditContentIsNull(EditText et) {
		if (et == null) {
			return true;
		} else {
			if (!getEditTextString(et).trim().equals("")) {
				return false;
			}
		}
		return true;
	}

}
