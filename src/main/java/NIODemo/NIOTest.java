package NIODemo;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Title:
 * @Author: ggh
 * @Date: 2020/11/8 19:31
 */
public class NIOTest {



    @Test
    public void client() throws IOException {

        //获取网络通道
        SocketChannel sCHannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //获取本地读取文件的通道
        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);

        //创建缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //读取本地文件的内容，并发送到网络通道sChannel
        while (inChannel.read(buf) != -1){
            //切换buf为读取模式
            buf.flip();
            //写入到网络通道
            sCHannel.write(buf);
            //清空buf
            buf.clear();
        }


        //关闭通道
        sCHannel.close();
        inChannel.close();


    }


    @Test
    public void server() throws IOException {

        //获取服务端的通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        //获取本地写入的通道,且为写模式
        FileChannel outChannel = FileChannel.open(Paths.get("2.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE);

        //绑定端口
        ssChannel.bind(new InetSocketAddress(9898));

        //接收连接
        SocketChannel sChannel = ssChannel.accept();

        //创建缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //从网络通道读取内容
        while (sChannel.read(buf) != -1){

            buf.flip();
            outChannel.write(buf);
            buf.clear();

        }

        //关闭通道

        ssChannel.close();
        outChannel.close();
        sChannel.close();

    }


    //    @Test
    public static void client2() throws IOException {

        //获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //将通道配置为非阻塞模式
        sChannel.configureBlocking(false);

        //创建缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        Scanner sc = new Scanner(System.in);

        String line = sc.nextLine();

        while (!line.equals("")){
            //网缓冲区里写数据
            buf.put(line.getBytes());

            //将buf切换为 读 模式
            buf.flip();

            //将缓冲区中的数据写到cChannel
            sChannel.write(buf);
            buf.clear();

            line = sc.nextLine();
        }


        //关闭通道
        sChannel.close();
    }

    @Test
    public void server2() throws IOException {

        //获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();


        //绑定端口
        ssChannel.bind(new InetSocketAddress(9898));

        //设置通道为非阻塞模式
        ssChannel.configureBlocking(false);

        //创建选择器
        Selector selector = Selector.open();

        //将通道注册到选择器中，并指定为 “监听接收事件”
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        //判断选择器中数量是否大于0
        while (selector.select() > 0){

            //获取迭代器
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            //遍历选择器中的内容
            while (it.hasNext()){

                //
                SelectionKey sk = it.next();

                //判断是否为 “接收状态”
                if (sk.isAcceptable()){

                    SocketChannel sChannel = ssChannel.accept();

                    //将其设置为非阻塞模式
                    sChannel.configureBlocking(false);

                    //注册到选择器
                    sChannel.register(selector,SelectionKey.OP_READ);

                }else if (sk.isReadable()){
                    //如果是可读状态

                    //获取到通道
                    SocketChannel channel = (SocketChannel) sk.channel();

                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    int len=0;

                    //读取通道中的数据到buf，并打印
                    while ((len = channel.read(buf)) > 0){

                        buf.flip();
                        System.out.println(new String(buf.array(),0,len));
                        buf.clear();

                    }

                }

                //移除已经处理的SelectionKey
                it.remove();

            }


        }

        ssChannel.close();
        selector.close();

    }


    public static void send() throws IOException {

        DatagramChannel dChannel = DatagramChannel.open();

        dChannel.configureBlocking(false);

        Scanner sc = new Scanner(System.in);

        String line = sc.nextLine();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (!line.equals("")){

            buf.put(line.getBytes());

            buf.flip();

            dChannel.send(buf,new InetSocketAddress("127.0.0.1",9898));

            buf.clear();

            line = sc.nextLine();

        }

        dChannel.close();

    }


    @Test
    public void receive() throws IOException {

        DatagramChannel dc = DatagramChannel.open();

        dc.bind(new InetSocketAddress(9898));

        dc.configureBlocking(false);

        Selector selector = Selector.open();

        dc.register(selector,SelectionKey.OP_READ);

        while (selector.select() > 0){

            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()){

                SelectionKey sk = iterator.next();

                if (sk.isReadable()){

                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    dc.receive(buf);

                    buf.flip();
                    System.out.println(new String(buf.array(),0,buf.limit()));

                    buf.clear();
                }

            }

            iterator.remove();

        }

    }

    @Test
    public void pipTest() throws IOException {

        Pipe pip = Pipe.open();

        //发送数据
        Pipe.SinkChannel sinkChannel = pip.sink();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put("管道发送数据".getBytes());
        buf.flip();
        sinkChannel.write(buf);


        //读取管道中的数据
        Pipe.SourceChannel sourceChannel = pip.source();
        buf.flip();
        int len = sourceChannel.read(buf);

        System.out.println(new String(buf.array(),0,len));

        sinkChannel.close();
        sourceChannel.close();

    }



}
