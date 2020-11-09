package NIODemo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * @Title: 使用netty的 bossGroup workerGroup 自定义handler等实现群聊功能
 * @Author: ggh
 * @Date: 2020/11/7 22:40
 */
public class GroupChatClient2 {

    private String host;
    private int port;

    public GroupChatClient2(String host, int port) {
        this.host = host;
        this.port = port;
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

                            //加入解码 编码器
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());

                            //加入检测读写空闲时间的handler，13表示是否超过13s没有读，5表示是否超过5s没有写，6表示是否超过6s没有发生读写
                            //pipeline.addLast(new IdleStateHandler(13,5,6, TimeUnit.SECONDS));

                            pipeline.addLast(new GroupChatClientHandler2());

                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();

            Channel channel = channelFuture.channel();

            Scanner scanner = new Scanner(System.in);

            String line = scanner.nextLine();

            while (!"".equals(line)){
                channel.writeAndFlush(line+"\r\n");
                line = scanner.nextLine();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {

            group.shutdownGracefully();

        }

    }

    public static void main(String[] args) {

        new GroupChatClient2("127.0.0.1",6666).run();

    }

}
