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

  /**
   * 报文头域5 源ID 11位定长数字字符数据 标识报文发送者，但并不一定是原始交易数据的收集者
   */
  private String sourceStationId;

  /**
   * 报文头域4 目的ID 11位定长数字字符数据 该域表示报文的路由选择
   */
  private String destinationId;
  
  /**
   * socket服务器地址及端口配置
   */
  private SocketServerProperties socketServer;
}
