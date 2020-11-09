package NIODemo.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.charset.Charset;

/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/8 19:52
 */
public class TCPClientHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

        int len = msg.getLen();
        byte[] content = msg.getContent();

        System.out.println("客户端接收到的数据如下：");
        System.out.println("长度：" + len);
        System.out.println("内容：" + new String(content, Charset.forName("utf-8")));
        System.out.println("接收到的消息数量：" + (++this.count));

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //给服务器发送5条数据
        for (int i = 0; i < 5; i++) {

            String info = "今天天气不错 " + i;

            MessageProtocol messageProtocol = new MessageProtocol();

            byte[] content = info.getBytes(Charset.forName("utf-8"));
            int length = content.length;

            messageProtocol.setLen(length);
            messageProtocol.setContent(content);

            //发送数据，发出去后会经过客户端设置的编码器进行编码后发往服务器
            ctx.writeAndFlush(messageProtocol);

        }

    }
}
