package com.vk.sdk.api.methods;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;

public class VKApiLikes extends VKApiBase{
	
	 public VKRequest add(VKParameters params) {
		 return prepareRequest("add", params, VKRequest.HttpMethod.POST);
	    }
	 
	 public VKRequest delete(VKParameters params) {
		 return prepareRequest("delete", params, VKRequest.HttpMethod.POST);
	    }

}
