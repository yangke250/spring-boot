package cn.linkedcare.springboot.portal.aop;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWTVerifier;

import cn.linkedcare.mall.common.dto.auth.MallContextDto;
import cn.linkedcare.mall.common.dto.auth.SaasContextDto;
import cn.linkedcare.mall.common.dto.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpSessionInterceptor  implements HandlerInterceptor{
	
	
	//前台session
	private static ThreadLocal<MallContextDto> frontThreadLocal = new ThreadLocal<MallContextDto>();
	
	//后台session
	private static ThreadLocal<SaasContextDto> backThreadLocal = new ThreadLocal<SaasContextDto>();
	
	
	public static final String PLATFORM = "platform";
	public static final String AUTH     = "Authorization";

	/**
	 * 得到前台session
	 * @return
	 */
	public static MallContextDto getFrontSession() {
		log.info("getFrontSession:{}",JSON.toJSONString(frontThreadLocal.get()));
		
		return frontThreadLocal.get();
	}

	/**
	 * 得到后台session
	 * @return
	 */
	public static SaasContextDto getBackSession() {
		log.info("getBackSession:{}",JSON.toJSONString(backThreadLocal.get()));
		
		return backThreadLocal.get();
	}
	
	

	/**
	 * 是否是jwt token
	 * @param request
	 * @return
	 */
	private boolean isJWTToken(HttpServletRequest request){
		String uri = request.getRequestURI();
		//此方法不做拦截
		if(uri.contains("getJWTCode")) {
			return false;
		}
		
		String token = request.getHeader(AUTH);
		if(token==null){
			return false;
		}
		final String[] pieces = token.split("\\.");
        if (pieces.length != 3) {
        	return false;
        }
        	return true;
	}
	
	/**
	 * 设置前端的session
	 * @param request
	 * @return
	 */
	private void setFrontSession(HttpServletRequest request){
		if(!isJWTToken(request)){
			return;
		}
		
		String token = request.getHeader(AUTH);
		log.info("front content:{}",JSON.toJSONString(token));
		
		MallContextDto mallContextDto = JwtUtil.getWxContext(token);
		frontThreadLocal.set(mallContextDto);
	}
	
	/**
	 * 设置后台session
	 * @param request
	 */
	private void setBackSession(HttpServletRequest request){
		if(!isJWTToken(request)){
			return;
		}
		
		String token = request.getHeader(AUTH);
		log.info("back content:{}",JSON.toJSONString(token));
		
		SaasContextDto saasContextDto = JwtUtil.getSaaSContext(token);
		backThreadLocal.set(saasContextDto);
	}
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String platform = request.getHeader(PLATFORM);
		
		log.info("http headers:{},{}",platform,JSON.toJSONString(request.getHeaderNames()));
		
		
		if(platform==null){
			return true;
		}
		
		switch(platform){
		case "backTenant":
			setBackSession(request);
			break;
		case "frontH5":
			setFrontSession(request);
			break;
		case "backLinkedcare":
			
			break;
		}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		String platform = request.getHeader(PLATFORM);
		if(platform==null){
			return;
		}
		
		
		switch(platform){
		case "backTenant":
			backThreadLocal.remove();
			break;
		case "frontH5":
			frontThreadLocal.remove();
			break;
		case "backLinkedcare":
			
			break;
		}
	}

}

