package chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
 
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
 
public class ChatClient {
 
    static final String HOST = System.getProperty("host", "192.168.0.121");
    static final int PORT = Integer.parseInt(System.getProperty("port", "5001"));
    
 
    
    public static void main(String[] args) throws Exception {
        new ChatClient().run();
    }
    
    public void run() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        
        try {
            
            final SslContext sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChatClientInitializer(sslCtx));
            
            Channel channel = bootstrap.connect(HOST, PORT).sync().channel();
            
            ChannelFuture lastWriteFuture = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            for (;;) {
                String line = in.readLine();
                if (line == null) {
                    break;
                }
 
                // Sends the received line to the server.
                lastWriteFuture = channel.writeAndFlush(line + "\r\n");
 
                // If user typed the 'bye' command, wait until the server closes
                // the connection.
                if ("bye".equals(line.toLowerCase())) {
                    channel.closeFuture().sync();
                    break;
                }
            }
 
            // Wait until all messages are flushed before closing the channel.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }
        } finally {
            group.shutdownGracefully();
        }
    }
 
}