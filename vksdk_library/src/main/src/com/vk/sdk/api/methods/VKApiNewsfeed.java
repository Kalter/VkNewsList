package com.vk.sdk.api.methods;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.model.VKPostArray;

public class VKApiNewsfeed extends VKApiBase{
	
	public VKRequest get(VKParameters params){
//		if (((Integer) params.get("extended")) == 1) {
            return prepareRequest("get", params, VKRequest.HttpMethod.GET, VKPostArray.class);
//        } else {
//            return prepareRequest("get", params);
//        }
	}

}
