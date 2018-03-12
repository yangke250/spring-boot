package cn.linkedcare.springboot.portal.dto;

import cn.linkedcare.springboot.portal.annotation.limitrate.LimitRate;

/**
 * limit相关dto
 * @author wl
 *
 */
public class LimitRateDto {
	private LimitRate limitRate;
	private String[] uris;
	
	public LimitRate getLimitRate() {
		return limitRate;
	}
	public void setLimitRate(LimitRate limitRate) {
		this.limitRate = limitRate;
	}
	public String[] getUris() {
		return uris;
	}
	public void setUris(String[] uris) {
		this.uris = uris;
	}
	
	
}
