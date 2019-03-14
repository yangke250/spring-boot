package cn.linkedcare.springboot.kafka;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import cn.linkedcare.springboot.kafka.config.KafkaConsumerConfig;
import cn.linkedcare.springboot.kafka.config.KafkaProducerConfig;

@Configurable
@ComponentScan
@Import(value = {KafkaConsumerConfig.class,KafkaProducerConfig.class})
public class Launch {

}
