package top.lemno.pay.flm.properties.flm;

import lombok.Data;
import lombok.ToString;

/**
 * 支付配置
 * 
 * @author mux
 *
 */
@Data
@ToString
public class PayProperties {

  private int workGroupSize;

  private SocketServerProperties socketServer;
}
