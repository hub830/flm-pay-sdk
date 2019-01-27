package top.lemno.pay.flm;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import top.lemno.pay.flm.properties.FLMPayProperties;

@Configuration
@EnableConfigurationProperties(FLMPayProperties.class)
public class FlmPayConfig {

}
