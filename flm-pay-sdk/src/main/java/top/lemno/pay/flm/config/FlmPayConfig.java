package top.lemno.pay.flm.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.solab.iso8583.IsoMessage;
import com.solab.iso8583.MessageFactory;
import top.lemno.pay.flm.properties.FLMPayProperties;

@Configuration
@EnableConfigurationProperties(FLMPayProperties.class)
// @PropertySource(value = "classpath:uidGenerator.properties")
public class FlmPayConfig {

  @Bean
  MessageFactory<IsoMessage> messageFactory() {
    MessageFactory<IsoMessage> messageFactory = new MessageFactory<IsoMessage>();
    return messageFactory;
  }
  
}
