package NIODemo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/7 22:47
 */
public class GroupChatClientHandler2 extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println(msg.trim()+"\n");
    }


    /*@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        //判断读写空闲是否超过一定的值
        if(evt instanceof IdleStateEvent){

            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()){

                case READER_IDLE:
                    System.out.println("都空闲");
                    break;
                case WRITER_IDLE:
                    System.out.println("写空闲");
                    break;
                case ALL_IDLE:
                    System.out.println("读写空闲");
                    break;
                default:
                    break;

            }

        }

    }*/
}
