package com.link.bianmi.entity;

import java.io.Serializable;

/**
 * 服务端下发的配置
 * 
 * @author pangfq
 * @date 2014-10-12 上午10:08:03
 */
public class Config implements Serializable {

	private static final long serialVersionUID = -5636949155393543280L;

	public boolean showAd = false;// 是否展示广告

	public boolean smsAccess = true;// 短信验证是否可用(防止第三方出现短信无法发送的情况)

}
