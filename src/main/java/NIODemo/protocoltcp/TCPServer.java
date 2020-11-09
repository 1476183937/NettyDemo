package NIODemo.protocoltcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Title: TCP 粘包/拆包测试 server
 * @Author: ggh
 * @Date: 2020/11/8 19:51
 */
public class TCPServer {

    private int port;

    public TCPServer(int port) {
        this.port = port;
    }

    public void run(){

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline pipeline = ch.pipeline();

                            //添加自定义的编码 解码器
                            pipeline.addLast(new MyMessageDecoder());
                            pipeline.addLast(new MyMessageEncoder());

                            //添加自定义的 handler
                            pipeline.addLast(new TCPServerHandler());

                        }
                    });

            System.out.println("server is ok...");
            ChannelFuture channelFuture = bootstrap.bind(6666).sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();

        }


    }

    public static void main(String[] args) {

        new TCPServer(6666).run();
    }

}
