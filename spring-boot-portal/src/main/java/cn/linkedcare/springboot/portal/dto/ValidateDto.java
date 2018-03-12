package cn.linkedcare.springboot.portal.dto;

/**
 * hibernate只能支持对象的验证，对于普通值的验证不支持，暂时手写
 * @author wl
 *
 */
public class ValidateDto {
	//为true的时候代表验证通过
	private boolean result = true;
	private String msg;//错误原因
	
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
