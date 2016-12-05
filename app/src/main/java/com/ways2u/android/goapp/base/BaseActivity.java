package com.ways2u.android.goapp.base;
/**
 * Created by Onelong on 2015/10/31.
 */

import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.ways2u.android.net.util.NetContext;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public abstract class BaseActivity extends RxAppCompatActivity implements Finishable, OnClickListener {
	private static int sStartedActivityCount;
	private static long lastClickTime;
	private Toast toast;
	private String TAG;

	/**
	 *
	 */
	protected MyHandler myHandler;
	//public static long updateTime = 0;

	//protected boolean needSync;
	//protected View actionbarLayout;


	private static final List<Finishable> sfinishableList = new ArrayList<Finishable>();

	public static void finishAll() {
		List<Finishable> fList = new ArrayList<Finishable>(sfinishableList);
		for (Finishable f : fList) {
			f.exit();
		}
	}

	public void finishAllExceptSelf(Finishable finishable) {
		List<Finishable> fList = new ArrayList<Finishable>(sfinishableList);
		for (Finishable f : fList) {
			if(!f.equals(finishable))
			f.exit();
		}
	}

	public void putFinishList(Finishable f) {
		sfinishableList.add(f);
	}

	public void removeFromFinishList(Finishable f) {
		sfinishableList.remove(f);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = getClass().getName();
		//getSupportActionBar().hide();
		myHandler = new MyHandler(this);
		setContentView(getLayoutId());
		initView();
		setListener();
		initData();
		putFinishList(this);
	}

	/**
	 * 防止两次连续点击，执行两次相同操 *
	 *
	 * @return
	 */
	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		if (time - lastClickTime < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public <T extends View> T find(int viewId) {
		return (T) this.findViewById(viewId);
	}
	public static class MyHandler extends Handler {
		WeakReference<BaseActivity> mActivity;
		MyHandler(BaseActivity activity) {
			mActivity = new WeakReference<BaseActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			if (mActivity.get() != null && !mActivity.get().isFinishing()) {
				mActivity.get().undateUI(msg);
			}
		}
	}

	/**
	 * 子Activity 实现处理主线程UI
	 *
	 * @param msg
	 */
	public abstract void undateUI(Message msg);
	protected abstract int getLayoutId();
	protected abstract void initView();
	protected abstract void setListener();
	protected abstract void initData();


	public void finish() {
		removeFromFinishList(this);
		super.finish();
	}

	@Override
	public void exit() {
		try {
			finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	protected void onStart() {
		super.onStart();
		sStartedActivityCount++;
		if (1 == sStartedActivityCount) {
			applicationDidEnterForeground();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		sStartedActivityCount--;
		if (0 == sStartedActivityCount) {
			applicationDidEnterBackground();
		}
	}

	/**
	 * Activity的回调函数。当application进入前台时，该函数会被自动调用。
	 */
	protected void applicationDidEnterForeground() {
		//start
		//if(AppContext.getInstance().mRequestQueue!=null)
			//AppContext.getInstance().mRequestQueue.start();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * Activity的回调函数。当application进入后台时，该函数会被自动调用。
	 */
	protected void applicationDidEnterBackground() {
		//stop
		//if(AppContext.getInstance().mRequestQueue!=null)
			//AppContext.getInstance().mRequestQueue.stop();
		//System.out.println("applicationDidEnterBackground");
	}

	@Override
	public void onClick(View v) {

	}


	/**
	 *
	 */
	@Override
	protected void onDestroy() {
		if(myHandler!=null){
			myHandler.removeCallbacksAndMessages(null);
			myHandler.mActivity.clear();
		}
		myHandler = null;
		//退出排队的请求
		if(NetContext.getInstance()!=null)
			NetContext.getInstance().cancelAll(this);

		super.onDestroy();
	}

	/**
	 * 显示加载进度
	 *
	 * @param message
	 *            加载时提示文字
	 * @param isCancelable
	 *            点击外部是否可以取消
	 */
	/*
	public void showProgressDialog(String message, boolean isCancelable) {
		if (pd != null && pd.isShowing()) {
			return;
		}
		if(pd!=null)
			pd.dismiss();

		//if (pd == null) {
		pd = new AppProgressDialog(this);
		pd.setCanceledOnTouchOutside(false);
		//}
		pd.setCancelable(true);
		pd.setMessage(message);
		if (!this.isFinishing()) {
			pd.show();
		}

	}
*/
	/**
	 * 消失进度
	 */
	/*
	public void dimissProgressDialog() {
		if (pd != null && pd.isShowing()) {
			pd.dismiss();
		}

	}
*/
	/**
	 * 间隔一段时间消失进度
	 *
	 * @param time
	 */
	/*
	public void dimissProgressDialog(final long time) {
		if (pd != null && pd.isShowing()) {
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if(!isFinishing())
						pd.dismiss();
				}
			},time);


		}

	}
*/
	/**
	 * 取消进度
	 */
	/*
	public void cancelProgressDialog() {
		if (pd != null && pd.isShowing()) {
			pd.cancel();
		}

	}
*/
	/**
	 * 获取输入框的文本
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
	 * 隐藏视图但它占据了空�? *
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
	 * �?��视图
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
		if (toast == null) {
			toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
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
			toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
		}
		toast.setText(s);
		toast.show();
	}

	/**
	 * 文本编辑器是否有输入内容
	 *
	 * @param et
	 * @return true代表没有输入内容
	 */
	public boolean checkEditContentIsNull(EditText et) {
		if (et == null) {
			return true;
		} else {
			if (!getEditTextString(et).equals("")) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param Message
	 */
	public void printError(String Message) {
		if (Message != null) {
			Log.e(TAG, Message);
		}
	}

	public String getResStr(int id) {
		String str = "";
		try {
			str = getResources().getString(id);
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
			str = "";
		}
		return str;
	}

}