package NIODemo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/6 20:55
 */
public class NettyServer {

    public static void main(String[] args) {


        //创建 bossGroup 和 workerGroup，
        //说明
        //1.创建两个线程组   bossGroup和    workerGroup
        //2. bossGroup只是处理连接请求   ,真正的和客户端业务处理，会交给    workerGroup完成
        //3.两个都是无限循环
        //4. bossGroup 和 workerGroup含有的子线程(NioEventLoop)的个数默认为实际 cpu核数  *2
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {

            //创建服务器端的启动对象，配置参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(bossGroup, workerGroup)  //设置两个线程组
                    .channel(NioServerSocketChannel.class)    //设置 ServerSocketChannel 来作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置线程保持活动状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道测试对象

                        //给 pipline 设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            System.out.println("服务器 is ok...");
            //绑定一个端口并且同步,生成了一个 ChannelFuture对象
            //启动服务器(并绑定端口)
            ChannelFuture cf = serverBootstrap.bind(6666).sync();
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            //关闭 bossGroup 和 workerGroup
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
