package NIODemo.protocoltcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Title: TCP 粘包/拆包测试 client
 * @Author: ggh
 * @Date: 2020/11/8 19:51
 */
public class TCPClient {

    private int port;
    private String host;

    public TCPClient( String host,int port) {
        this.port = port;
        this.host = host;
    }

    public void run(){

        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();

        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            //添加自定义的编码/解码器
                            pipeline.addLast(new MyMessageEncoder());
                            pipeline.addLast(new MyMessageDecoder());

                            //添加自定义的 handler
                            pipeline.addLast(new TCPClientHandler());

                        }
                    });

            System.out.println("client is ok...");
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            group.shutdownGracefully();

        }

    }

    public static void main(String[] args) {

        new TCPClient("127.0.0.1",6666).run();

    }

}
