package com.android.widget.jsbridge;

import com.android.widget.jsbridge.interfaces.BridgeHandler;
import com.android.widget.jsbridge.interfaces.CallBackFunction;

public class DefaultHandler implements BridgeHandler {

	@Override
	public void handler(String data, CallBackFunction function) {
		if(function != null){
			function.onCallBack("DefaultHandler response data");
		}
	}

}
