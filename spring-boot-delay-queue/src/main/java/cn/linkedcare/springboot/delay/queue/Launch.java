package cn.linkedcare.springboot.delay.queue;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import cn.linkedcare.springboot.redis.config.RedisConfig;
import cn.linkedcare.springboot.redis.template.RedisTemplate;


@Configurable
@ComponentScan
public class Launch {
}
