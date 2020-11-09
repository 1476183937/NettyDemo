package NIODemo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/6 21:18
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
    *@title:
    *@description: 读取消息
    *@param: ctx
    *@param: msg
    *@author:ggh
    *@updateTime: 2020/11/6 21:21
    **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf info = (ByteBuf) msg;

        System.out.println("收到客户端的信息："+ info.toString(CharsetUtil.UTF_8));


        //给任务队列添加普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(5000);

                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 我是 普通任务",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });


        //添加定时任务
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 我是 定时任务",CharsetUtil.UTF_8));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },5, TimeUnit.SECONDS);

    }

    /**
    *@title:
    *@description: 读取完信息后的操作
    *@param: ctx
    *@author:ggh
    *@updateTime: 2020/11/6 21:23
    **/
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        //给客户端回送信息
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端...",CharsetUtil.UTF_8));

    }

    /**
    *@title:
    *@description: 发送异常后执行的操作
    *@param: ctx
    *@param: cause
    *@author:ggh
    *@updateTime: 2020/11/6 21:27
    **/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
