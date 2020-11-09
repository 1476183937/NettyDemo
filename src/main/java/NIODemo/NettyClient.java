package NIODemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    public static void main(String[] args) {

        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandler());
                        }
                    });

            System.out.println("客户端 is ok...");
            //启动客户端去连接诶服务器，设置为异步
            ChannelFuture cf = bootstrap.connect("127.0.0.1", 6666).sync();

            //给关闭通道设置监听
            cf.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //关闭 group
            group.shutdownGracefully();

        }

    }

}


