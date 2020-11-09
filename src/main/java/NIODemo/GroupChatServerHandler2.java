package NIODemo;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/7 22:17
 */
public class GroupChatServerHandler2 extends SimpleChannelInboundHandler<String> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /**
    *@title:
    *@description: 当channel连接进来第一个调用的方法就是这个
    *@param: ctx
    *@author:ggh
    *@updateTime: 2020/11/7 22:30
    **/
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        //给其他客户端发送消息通知该客户端上线了
        channelGroup.writeAndFlush("客户 " + channel.remoteAddress() + "加入聊天!\n");

//        //将当前 channel 添加到 channelGroup
        channelGroup.add(channel);

    }

    /**
    *@title: 
    *@description: 当 handler 被移除时会调用该方法
    *@param: ctx
    *@author:ggh
    *@updateTime: 2020/11/7 22:34
    **/
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

        Channel channel = ctx.channel();
        
        channelGroup.writeAndFlush("客户 " + channel.remoteAddress() + " 离开了！");

        System.out.println("当前 channelGroup 的 size = " + channelGroup.size());

    }

    /**
    *@title:
    *@description: 当 channel 处于活动状态时会调用该方法
    *@param: ctx
    *@author:ggh
    *@updateTime: 2020/11/7 22:36
    **/
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("客户 " + ctx.channel().remoteAddress() + " 上线了！");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        System.out.println("客户 " + ctx.channel().remoteAddress() + " 离线了！");
    }

    /**
     * @title:
     * @description: 读取消息时会调用该方法
     * @param: ctx
     * @param: msg
     * @author:ggh
     * @updateTime: 2020/11/7 22:18
     **/
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        System.out.println("收到客户端的消息：" + msg);

        Channel channel = ctx.channel();
        //向其他客户端转发消息
        channelGroup.forEach(ch -> {

            if (ch != channel){
                ch.writeAndFlush("客户 ：" + channel.remoteAddress() + "发送了消息 ： " + msg + "\n");
            }else{
                ch.writeAndFlush("自己发送了消息：" + msg + "\n");
            }

        });
        System.out.println("消息转发完毕...");

    }

    /**
    *@title:
    *@description: 发生异常是会调用该方法
    *@param: ctx
    *@param: cause
    *@author:ggh
    *@updateTime: 2020/11/7 22:38
    **/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
