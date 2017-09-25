package com.dwwang.payment;

import android.app.Activity;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

public class RNPaymentModule extends ReactContextBaseJavaModule {

    public static ReactApplicationContext mContext = null;
    public static IWXAPI mIWXAPI = null;

    public RNPaymentModule(ReactApplicationContext context) {
        super(context);
        mContext = context;
    }

    @Override
    public String getName() {
        return "RNPayment";
    }

    public static ReactContext getContext() {
        return mContext;
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

                    WritableMap params = Arguments.createMap();
                    params.putString("resultInfo", resultInfo);
                    params.putString("resultStatus", resultStatus);

                    promise.resolve(params);
                } catch (Exception e) {
                    promise.reject("EUNSPECIFIED", e.getMessage());
                }
			}
		};

		Thread payThread = new Thread(payRunnable);
		payThread.start();
    }

    @ReactMethod
    public void registerApp(String appId) {
        if (mIWXAPI != null || mContext == null) {
            return;
        }

        mIWXAPI = WXAPIFactory.createWXAPI(mContext, appId, false);
        mIWXAPI.registerApp(appId);
    }

    @ReactMethod
    public boolean wxpay(ReadableMap object) {
        String appId = object.getString("appId");
        if (mIWXAPI == null) {
            this.registerApp(appId);
        }

        PayReq request = new PayReq();
        request.appId = appId;
        request.partnerId = object.getString("partnerId");
        request.prepayId = object.getString("prepayId");
        request.packageValue = object.getString("packageValue");
        request.nonceStr = object.getString("nonceStr");
        request.timeStamp = object.getString("timeStamp");
        request.sign = object.getString("sign");
        return mIWXAPI.sendReq(request);
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("IsAndroid", true);
        return constants;
    }
}
