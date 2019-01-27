package top.lemno.pay.flm.client.pay;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import top.lemno.pay.commons.client.SocketClient;

/**
 * 客户端初始化Channel
 * 
 * @author 元歌
 */
public class ClientChannelInitializer extends ChannelInitializer<Channel> {

  private SocketClient socketClient;

  public ClientChannelInitializer(SocketClient posClient) {
    this.socketClient = posClient;
  }

  /**
   * 空闲10秒发送心跳包
   * 
   * @param channel
   * @throws Exception
   */
  @Override
  protected void initChannel(Channel channel) throws Exception {
    channel//
        .pipeline()//
        .addLast(new IdleStateHandler(0, 30, 0))//
        .addLast(new HeartBeatEncoder())//
        .addLast(new HeartBeatHandler(socketClient))//
    ;
  }
}

