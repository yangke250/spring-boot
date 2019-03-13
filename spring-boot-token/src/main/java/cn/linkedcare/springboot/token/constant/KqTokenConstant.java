package cn.linkedcare.springboot.token.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(
value = {"classpath:application-${spring.profiles.active}.properties"},
ignoreResourceNotFound = false, encoding = "UTF-8")
public class KqTokenConstant {

	
	private static String tokenUsername;
	
	
	private static String tokenPassword;
	
	
	private static String tokenUrl;
	
	private static String oldTokenTenantId;
			
	private static String oldTokenTicket;
	
	private static String oldTokenUrl;

	public static String getTokenUsername() {
		return tokenUsername;
	}

	@Value("${kq.token.username}")
	public void setTokenUsername(String tokenUsername) {
		KqTokenConstant.tokenUsername = tokenUsername;
	}

	public static String getTokenPassword() {
		return tokenPassword;
	}

	@Value("${kq.token.password}")
	public void setTokenPassword(String tokenPassword) {
		KqTokenConstant.tokenPassword = tokenPassword;
	}

	public static String getTokenUrl() {
		return tokenUrl;
	}

	@Value("${kq.token.url}")
	public void setTokenUrl(String tokenUrl) {
		KqTokenConstant.tokenUrl = tokenUrl;
	}

	
	public static String getOldTokenTenantId() {
		return oldTokenTenantId;
	}

	@Value("${kq.oldToken.tenantId}")
	public void setOldTokenTenantId(String oldTokenTenantId) {
		KqTokenConstant.oldTokenTenantId = oldTokenTenantId;
	}

	public static String getOldTokenTicket() {
		return oldTokenTicket;
	}

	@Value("${kq.oldToken.ticket}")
	public void setOldTokenTicket(String oldTokenTicket) {
		KqTokenConstant.oldTokenTicket = oldTokenTicket;
	}

	public static String getOldTokenUrl() {
		return oldTokenUrl;
	}

	@Value("${kq.oldToken.url}")
	public void setOldTokenUrl(String oldTokenUrl) {
		KqTokenConstant.oldTokenUrl = oldTokenUrl;
	}

	
	
}
