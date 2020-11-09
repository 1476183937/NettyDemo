package NIODemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/6 21:36
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    /**
    *@title:
    *@description: 当channel 就绪是就会调用该方法
    *@param: ctx
    *@author:ggh
    *@updateTime: 2020/11/6 21:37
    **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //给服务器发送一条信息
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 服务器...", CharsetUtil.UTF_8));

    }

    /**
    *@title:
    *@description: 当channel 收到消息时会调用该方法
    *@param: ctx
    *@param: msg
    *@author:ggh
    *@updateTime: 2020/11/6 21:39
    **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf info = (ByteBuf) msg;
        System.out.println("收到服务器回复的信息：" + info.toString(CharsetUtil.UTF_8));

    }

    /**
    *@title:
    *@description: 发生异常是会调用该方法
    *@param: ctx
    *@param: cause
    *@author:ggh
    *@updateTime: 2020/11/6 21:40
    **/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
    }
}
