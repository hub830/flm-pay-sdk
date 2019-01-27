package top.lemno.pay.commons.client;

/**
 * socket客户端
 * @author mux
 *
 */
public interface SocketClient { 

  public void stop();

  public void connect();

  public void sendMsg(String msg);

}
