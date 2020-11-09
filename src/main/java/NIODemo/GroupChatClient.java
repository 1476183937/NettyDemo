package NIODemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Title: 单纯使用channel buffer selector等组件实现群聊功能
 * @Author: ggh
 * @Date: 2020/11/5 21:11
 */
public class GroupChatClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 6666;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;

    public GroupChatClient() throws IOException {

        selector = Selector.open();

        socketChannel = SocketChannel.open(new InetSocketAddress(HOST,PORT));

        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);

        username = socketChannel.getLocalAddress().toString();

        System.out.println(username + " is ok...");
    }

    /**
    *@title:
    *@description: 发送消息到服务器
    *@param: msg
    *@author:ggh
    *@updateTime: 2020/11/5 21:19
    **/
    public void sendMsg(String msg){
        System.out.println(username + "说： " + msg);
        try {
            socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
    *@title:
    *@description: 读取服务器发送的消息
    *@param:
    *@author:ggh
    *@updateTime: 2020/11/5 21:19
    **/
    public void readMsg(){

        try {
            int count = selector.select();

            if (count > 0){

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()){

                    SelectionKey sk = iterator.next();

                    if (sk.isReadable()){

                        SocketChannel channel = (SocketChannel) sk.channel();

                        ByteBuffer buf = ByteBuffer.allocate(1024);

                        channel.read(buf);

                        System.out.println(new String(buf.array()));

                    }

                    //移除已经处理过的 SelectionKey
                    iterator.remove();

                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {


        try {
            GroupChatClient client = new GroupChatClient();

            new Thread(){
                @Override
                public void run() {
                    while (true){
                        client.readMsg();

                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();

            Scanner scanner = new Scanner(System.in);

            String line = scanner.nextLine();

            while (!"".equals(line)){
                client.sendMsg(line);
                line = scanner.nextLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
