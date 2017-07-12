package cn.linkedcare.springboot.dubbo;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import cn.linkedcare.springboot.dubbo.config.DubboConfig;
import cn.linkedcare.springboot.dubbo.config.PropertyConfig;


@Configurable
@ComponentScan
@Import(value={PropertyConfig.class,DubboConfig.class})
public class Launch {

}
