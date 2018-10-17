package nettyHeat07.clinet;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 本Client为测试netty重连机制
 * Server端代码都一样，所以不做修改
 * 只用在client端中做一下判断即可
 */
public class HeartBeatsClient2 {

    private  int port;
    private  String address;
    ChannelFuture future;

    public HeartBeatsClient2(int port, String address) {
        this.port = port;
        this.address = address;
    }

    public void start(){
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new HeartBeatsClientChannelInitializer());

        try {
            future = bootstrap.connect(address,port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //group.shutdownGracefully();
            if (null != future) {
                if (future.channel() != null && future.channel().isOpen()) {
                    future.channel().close();
                }
            }
            System.out.println("准备重连");
            start();
            System.out.println("重连成功");
        }

    }

    public static void main(String[] args) {
        HeartBeatsClient2 client = new HeartBeatsClient2(7788,"127.0.0.1");
        client.start();
    }
}
