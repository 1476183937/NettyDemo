package NIODemo.protocoltcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;
import java.util.UUID;

/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/8 19:53
 */
public class TCPServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count = 0;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

        //读取客户端发送的数据
        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("服务器收到的数据如下：");
        System.out.println("长度：" + len);
        System.out.println("内容：" + new String(content, Charset.forName("utf-8")));
        System.out.println("收到的消息数量：" + (++this.count));


        //给客户端回复消息
        String replay = UUID.randomUUID().toString();

        MessageProtocol messageProtocol = new MessageProtocol();
        messageProtocol.setLen(replay.getBytes("utf-8").length);
        messageProtocol.setContent(replay.getBytes("utf-8"));

        ctx.writeAndFlush(messageProtocol);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
