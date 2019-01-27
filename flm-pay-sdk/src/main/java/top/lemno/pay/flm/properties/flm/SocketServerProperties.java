package top.lemno.pay.flm.properties.flm;

import lombok.Data;
import lombok.ToString;

/**
 * Socket 配置
 * 
 * @author mux
 *
 */
@Data
@ToString
public class SocketServerProperties {
  private String ip;
  private int port;
}
