package cn.linkedcare.springboot.dubbo.init;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Resource;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
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

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.ServiceBean;

import cn.linkedcare.springboot.dubbo.annotation.DubboConsumer;
import cn.linkedcare.springboot.dubbo.annotation.DubboProducer;

/**
 * 根据annotation加载相关的bean到dubbo
 * @author wl
 *
 */
@Component
public class DubboConsumerInit implements  BeanFactoryPostProcessor{
	
	public static final String BASE_PACKAGE="cn.linkedcare";
	
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setScanners(new FieldAnnotationsScanner());
		configurationBuilder.setUrls(ClasspathHelper.forPackage(BASE_PACKAGE));
		Reflections reflections = new Reflections(configurationBuilder);
		
		Set<Field> fields =  reflections.getFieldsAnnotatedWith(DubboConsumer.class);
		
		for(Field f:fields){
			String name =  f.getType().getName();
			if(!beanFactory.containsBean(name)){
				GenericBeanDefinition definition = new GenericBeanDefinition();
				
				definition.setBeanClass(ReferenceBean.class);
				definition.getPropertyValues().add("interface",name);
				
				DefaultListableBeanFactory defaultBeanFactory = (DefaultListableBeanFactory) beanFactory;
				defaultBeanFactory.registerBeanDefinition(f.getType().getName(),definition);
			}
		}
	}



}
