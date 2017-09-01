package com.dwwang.payment;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Promise;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.net.Uri;
import android.view.ViewGroup;
import android.content.Intent;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;

public class RNPaymentModule extends ReactContextBaseJavaModule {

    ReactApplicationContext mContext;

//    @SuppressLint("HandlerLeak")
//	private Handler mHandler = new Handler() {
//		@SuppressWarnings("unused")
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case SDK_PAY_FLAG: {
//				@SuppressWarnings("unchecked")
//				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
//				/**
//				 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
//				 */
//				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
//				String resultStatus = payResult.getResultStatus();
//				// 判断resultStatus 为9000则代表支付成功
//				if (TextUtils.equals(resultStatus, "9000")) {
//					// 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//					//Toast.makeText(PayDemoActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//				} else {
//					// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//					//Toast.makeText(PayDemoActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
//				}
//				break;
//			}
//			case SDK_AUTH_FLAG: {
//				@SuppressWarnings("unchecked")
//				AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
//				String resultStatus = authResult.getResultStatus();
//
//				// 判断resultStatus 为“9000”且result_code
//				// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
//				if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
//					// 获取alipay_open_id，调支付时作为参数extern_token 的value
//					// 传入，则支付账户为该授权账户
////					Toast.makeText(PayDemoActivity.this,
////							"授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT)
////							.show();
//				} else {
//					// 其他状态值则为授权失败
////					Toast.makeText(PayDemoActivity.this,
////							"授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
//
//				}
//				break;
//			}
//			default:
//				break;
//			}
//		};
//	};

    public RNPaymentModule(ReactApplicationContext context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public String getName() {
        return "RNPayment";
    }

    @ReactMethod
    public void alipay(final String orderInfo, final Promise promise) {
        Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
                try {
                    Activity activity = mContext.getCurrentActivity();
                    PayTask alipay = new PayTask(activity);
                    Map<String, String> result = alipay.payV2(orderInfo, true);
                    //Log.i("msp", "result = " + result.toString());

                    PayResult payResult = new PayResult(result);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    promise.resolve((TextUtils.equals(resultStatus, "9000")));
                } catch (Exception e) {
                    promise.reject("EUNSPECIFIED", e.getMessage());
                }
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("IsAndroid", true);
        return constants;
    }
}
