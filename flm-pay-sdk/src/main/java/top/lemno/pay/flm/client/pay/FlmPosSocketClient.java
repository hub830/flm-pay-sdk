package top.lemno.pay.flm.client.pay;

import org.springframework.util.Assert;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.lemno.pay.commons.client.SocketClient;
import top.lemno.pay.flm.properties.FLMPayProperties;
import top.lemno.pay.flm.properties.flm.PayProperties;
import top.lemno.pay.flm.properties.flm.SocketServerProperties;
import top.lemno.pay.utils.TransferUtil;

/**
 * 交易请求发送端
 *
 * @author 元歌
 */
@Slf4j
public class FlmPosSocketClient implements SocketClient{
  private SocketChannel channel;
  private EventLoopGroup workerGroup;

  private final FLMPayProperties properties;

  public FlmPosSocketClient(FLMPayProperties properties) {
    this.properties = properties;
  }

  public void stop() {
    log.info("destroy client resources {}", workerGroup);
    if (null == channel) {
      log.error("server channel is null");
    }
    if (null != workerGroup) {
      workerGroup.shutdownGracefully();
    } else if (channel != null) {
      channel.closeFuture().syncUninterruptibly();
    }
    workerGroup = null;
    channel = null;
  }

  public void connect() {
    PayProperties payProperties = properties.getPay();
    log.info("properties={}", payProperties);
    workerGroup = new NioEventLoopGroup(payProperties.getWorkGroupSize());
    SocketServerProperties socketServer = payProperties.getSocketServer();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap//
          .group(workerGroup)//
          .option(ChannelOption.SO_REUSEADDR, true)//
          .option(ChannelOption.SO_SNDBUF, 16 * 1024)//
          .option(ChannelOption.SO_KEEPALIVE, true)//
          .channel(NioSocketChannel.class)//
          .handler(new ClientChannelInitializer(this))//
      ;
      ChannelFuture future = bootstrap//
          .connect(socketServer.getIp(), socketServer.getPort())//
          .addListener(new ConnectionListener(this))//
          .sync();
      if (future.isSuccess()) {
        log.info("发送端启动成功");
        channel = (SocketChannel) future.channel();
      } else {
        log.info("发送端启动失败:{}", future);
      }
    } catch (Exception e) {
      log.error("发送端启动失败:{}", e);
      if (workerGroup != null) {
        try {
          workerGroup.shutdownGracefully();
        } catch (Exception e1) {
          log.error(e1.getMessage(), e1);
        }
      }
    }
  }

  public void sendMsg(String msg) {
    if (null != channel && channel.isActive()) {
      log.info("发送端发送消息：{}", msg);
      byte[] bytes = TransferUtil.hex2Byte(msg);
      Assert.notNull(bytes, "发送端发送数据不允许为空");
      channel.writeAndFlush(bytes);
    } else {
      log.warn("发送端断开重新连接中：{},发送失败", msg);
    }

  }

}
