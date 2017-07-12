package cn.linkedcare.springboot.dubbo.init;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Resource;

import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import com.alibaba.dubbo.config.spring.ServiceBean;

import cn.linkedcare.springboot.dubbo.annotation.DubboProducer;

/**
 * 根据annotation加载相关的bean到dubbo
 * @author wl
 *
 */
@Component
public class DubboProducerInit implements  BeanFactoryPostProcessor,BeanPostProcessor{
	
	public static final String BASE_PACKAGE="cn.linkedcare";
	
	private ConfigurableListableBeanFactory beanFactory;
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
		
		Reflections reflections = new Reflections(BASE_PACKAGE);
		Set<Class<?>>  clazz =  reflections.getTypesAnnotatedWith(DubboProducer.class);
		
		for(Class<?> c:clazz){
			DubboProducer dubboService = c.getAnnotation(DubboProducer.class);
			Class<?> refClass = dubboService.refClass();
			Class<?> interfaceClass = dubboService.interfaceClass();

			Object ref = beanFactory.getBean(refClass);
			
			
			GenericBeanDefinition definition = new GenericBeanDefinition();
			
			definition.setBeanClass(ServiceBean.class);
			definition.getPropertyValues().add("proxy", "javassist");
			//definition.getPropertyValues().add("version", dubboVersion);
			definition.getPropertyValues().add("interface", interfaceClass.getName());
			definition.getPropertyValues().add("ref", ref);
			definition.getPropertyValues().add("timeout", dubboService.timeout());
			definition.getPropertyValues().add("retries", dubboService.retry());
			
			DefaultListableBeanFactory defaultBeanFactory = (DefaultListableBeanFactory) beanFactory;
			defaultBeanFactory.registerBeanDefinition(interfaceClass.getName(),definition);
			
		}
	}


	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		return bean;
	}


	public static void setAutowired(Object bean,ConfigurableListableBeanFactory beanFactory){
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			Resource resource = field.getAnnotation(Resource.class);
			if(resource!=null){
				Class<?> requiredType = field.getType();
				Object value = null;
				if(resource.name()!=null){
					value = beanFactory.getBean(requiredType,resource.name());
				}else{
					value = beanFactory.getBean(requiredType);
				}
				field.setAccessible(true);
				ReflectionUtils.setField(field, bean, value);
			}
		}
	}
	
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof ServiceBean){
			ServiceBean<?> other = (ServiceBean<?>)bean;
			Object ref = other.getRef();
			setAutowired(ref,this.beanFactory);
		}
		return bean;
	}



}
