package com.android.web.bridge;

import com.android.web.bridge.interfaces.BridgeHandler;
import com.android.web.bridge.interfaces.CallBackFunction;

/**
 * created by jiangshide on 2014-09-22.
 * email:18311271399@163.com
 */
public class DefaultHandler implements BridgeHandler {

	@Override
	public void handler(String data, CallBackFunction function) {
		if(function != null){
			function.onCallBack("DefaultHandler response data");
		}
	}

}
