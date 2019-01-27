package top.lemno.pay.flm.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;
import lombok.ToString;
import top.lemno.pay.flm.properties.flm.ApplicationProperties;
import top.lemno.pay.flm.properties.flm.BindCardProperties;
import top.lemno.pay.flm.properties.flm.PayProperties;
import top.lemno.pay.flm.properties.flm.SettleProperties;

/**
 * 付临门配置
 * 
 * @author mux
 *
 */

@Data
@ToString
@ConfigurationProperties(prefix = "pay.flm")
public class FLMPayProperties {
  /**
   * 支付配置
   */
  private PayProperties pay;

  /**
   * 结算/代付 配置
   */
  private SettleProperties settle;

  /**
   * 绑卡 配置
   */
  private BindCardProperties bindCard;

  /**
   * 进件 配置
   */
  private ApplicationProperties application;
}
