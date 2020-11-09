package NIODemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @Title: 单纯使用channel buffer selector等组件实现群聊功能
 * @Author: ggh
 * @Date: 2020/11/5 20:32
 */
public class GroupChatServer {

    private Selector selector;
    private ServerSocketChannel serverSocketChannel;
    private static final int PORT = 6666;

    public GroupChatServer() {

        try {
            //初始化 Selector
            selector = Selector.open();

            //获取 serverSocketChannel
            serverSocketChannel = ServerSocketChannel.open();
            //绑定到指定端口
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            //配置为非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //将 serverSocketChannel 注册到 selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("server is ok...");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @title:
     * @description: 监听
     * @param:
     * @author:ggh
     * @updateTime: 2020/11/5 20:38
     **/
    public void listen() throws IOException {

        while (true) {

            if (selector.select() > 0) {

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {

                    SelectionKey sk = iterator.next();

                    if (sk.isAcceptable()) {
                        //接收连接
                        SocketChannel channel = serverSocketChannel.accept();
                        //配置为非阻塞模式
                        channel.configureBlocking(false);
                        //将 channel 注册到 selector
                        channel.register(selector, SelectionKey.OP_READ);
                        System.out.println(channel.getRemoteAddress() + " 上线了...");
                    }

                    if (sk.isReadable()) {
                        //读取消息
                        readMsg(sk);
                    }

                    //移除已经处理过的 SelectionKey
                    iterator.remove();
                }
            }

        }

    }


    public void readMsg(SelectionKey sk) {

        SocketChannel channel = (SocketChannel) sk.channel();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        try {
            int count = channel.read(buf);

            if (count > 0){
                String info = new String(buf.array());
                System.out.println("from 客户端：" + info);

                //将信息发送到其他客户端
                sendMsgToOtherClient(info,channel);
            }

        } catch (Exception e) {

            //
            try {
                System.out.println(channel.getRemoteAddress().toString() + "下线了");
                //取消该 SelectionKey
                sk.cancel();
                //关闭通道
                channel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();
        }

    }


    public void sendMsgToOtherClient(String msg, SocketChannel self){

        System.out.println("服务器发送消息中...");
        //获取 selector 中的所有 SelectionKey 并遍历
        Set<SelectionKey> keys = selector.keys();

        for (SelectionKey key : keys) {

            Channel target = key.channel();
            if (target instanceof SocketChannel && target != self){

                SocketChannel dest = (SocketChannel) target;

                //将信息存到缓冲区
                ByteBuffer buf = ByteBuffer.wrap(msg.getBytes());

                try {
                    //发送信息
                    dest.write(buf);
                    buf.clear();
                } catch (IOException e) {

                    e.printStackTrace();
                }

            }

        }

    }


    public static void main(String[] args) {

        GroupChatServer server = new GroupChatServer();

        try {
            server.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
