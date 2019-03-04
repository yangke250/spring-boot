package cn.linkedcare.springboot.portal;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Launch {

	@Bean("validator")
	public Validator getValidator(){
		Validator validator = 
				Validation.buildDefaultValidatorFactory().getValidator();
		return validator;
	}
}
