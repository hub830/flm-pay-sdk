package top.lemno.pay.flm.client.pay;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import top.lemno.pay.commons.client.SocketClient;

/**
 * 心跳Handler
 *
 * @author 元歌
 */
public class HeartBeatHandler extends SimpleChannelInboundHandler<ByteBuf> {

  private final static Logger LOGGER = LoggerFactory.getLogger(HeartBeatHandler.class);
  /**
   * 心跳数据
   */
  private final static byte[] HEART_BEAT_BYTES = {48, 48, 48, 48};

  private SocketClient socketClient;

  public HeartBeatHandler(SocketClient socketClient) {
    this.socketClient = socketClient;
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object event) throws Exception {
    // 客户端空闲一定时间后发送
    if (event instanceof IdleStateEvent) {
      IdleStateEvent idleStateEvent = (IdleStateEvent) event;
      if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
        ctx.writeAndFlush(HEART_BEAT_BYTES).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
      }
    }
    LOGGER.info("发送端发送心跳");
    super.userEventTriggered(ctx, event);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    // 客户端和服务端建立连接时调用
    LOGGER.info("发送端建立连接");
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    final EventLoop eventLoop = ctx.channel().eventLoop();
    eventLoop.schedule(new Runnable() {
      @Override
      public void run() {
        LOGGER.info("发送端被断开,重新建立连接");
        socketClient.connect();
      }
    }, 3L, TimeUnit.SECONDS);
    super.channelInactive(ctx);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf in)
      throws Exception {
    // 从服务端收到消息时被调用
    LOGGER.info("发送端收到消息{}", in.toString(CharsetUtil.UTF_8));

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // 异常时断开连接
    LOGGER.error("发送端发生异常{}", cause.getMessage(), cause);
    cause.printStackTrace();
  }

}
