package top.lemno.pay.flm.client.pay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import top.lemno.pay.commons.client.SocketClient;

/**
 * 〈启动时连接监听器〉
 *
 * @author 元歌
 * @create 2018-10-12
 */
public class ConnectionListener implements ChannelFutureListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SocketClient.class);

    private SocketClient socketClient;

    public ConnectionListener(SocketClient posClient){
        this.socketClient=posClient;
    }

    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {

        if(!channelFuture.isSuccess()){
            //客户端连接不成功20秒之后进行连接重试
            int sleep = 5;
            LOGGER.info("监听发送端非成功 {}秒之后进行重连",sleep);
            Thread.sleep(sleep*1000);//当前线程休眠20秒
            //重新连接
            socketClient.connect();
        }
    }
}
