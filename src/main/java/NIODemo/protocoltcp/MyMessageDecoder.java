package NIODemo.protocoltcp;

import com.sun.org.apache.xpath.internal.operations.String;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @Title: 自定义解码器
 * @Author: ggh
 * @Date: 2020/11/8 19:55
 */
public class MyMessageDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        System.out.println("MyMessageDecoder decode 被调用...");
        //数据的长度
        int len = in.readInt();
        //读取指定长度的内容
        byte[] buf = new byte[len];
        in.readBytes(buf);

        //封装成 MessageProtocol
        MessageProtocol messageProtocol = new MessageProtocol();

        messageProtocol.setLen(len);
        messageProtocol.setContent(buf);

        //添加到 List 集合中，交由下一个 handler 处理
        out.add(messageProtocol);

    }
}
