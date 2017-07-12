package cn.linkedcare.spring.boot.redis;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import cn.linkedcare.spring.boot.redis.config.PropertyConfig;
import cn.linkedcare.spring.boot.redis.config.RedisConfig;
import cn.linkedcare.spring.boot.redis.template.RedisTemplate;


@Configurable
@ComponentScan
@Import(value={RedisConfig.class,PropertyConfig.class})
public class Launch {
}
