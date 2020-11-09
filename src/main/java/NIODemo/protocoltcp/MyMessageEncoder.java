package NIODemo.protocoltcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Title: 自定义编码器
 * @Author: ggh
 * @Date: 2020/11/8 19:54
 */
public class MyMessageEncoder extends MessageToByteEncoder<MessageProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {

        System.out.println("MyMessageEncoder encode 被调用...");
        out.writeInt(msg.getLen());
        out.writeBytes(msg.getContent());

    }
}
