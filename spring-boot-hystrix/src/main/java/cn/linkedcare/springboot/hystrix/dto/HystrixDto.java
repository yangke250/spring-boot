package cn.linkedcare.springboot.hystrix.dto;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class HystrixDto {
	private String appName;
	private ServletRequest request;
	private ServletResponse response;
	private FilterChain chain;
	private final HystrixThreadType type =HystrixThreadType.normal ;
	
	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public ServletRequest getRequest() {
		return request;
	}
	public void setRequest(ServletRequest request) {
		this.request = request;
	}
	public ServletResponse getResponse() {
		return response;
	}
	public void setResponse(ServletResponse response) {
		this.response = response;
	}
	public FilterChain getChain() {
		return chain;
	}
	public void setChain(FilterChain chain) {
		this.chain = chain;
	}
	public HystrixThreadType getType() {
		return type;
	}
	
	
}
