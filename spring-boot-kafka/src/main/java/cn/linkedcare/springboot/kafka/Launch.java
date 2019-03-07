package cn.linkedcare.springboot.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cn.linkedcare.springboot.kafka.config.KafkaConsumerConfig;
import cn.linkedcare.springboot.kafka.config.KafkaProducerConfig;

@Configuration
@Import(value = {KafkaConsumerConfig.class,KafkaProducerConfig.class})
public class Launch {

}
