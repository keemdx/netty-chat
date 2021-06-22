package chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
 
public class ChatServer {
    private final int port;
 
    public ChatServer(int port) {
        super();
        this.port = port;
    }
    
    public static void main(String[] args) throws Exception {
        new ChatServer(5001).run();
    }
    
    public void run() throws Exception {
        
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
            .build();
        
        // [boss] 외부에서 들어오는 클라이언트 연결을 받는다.
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // [worker] 연결된 클라이언트 소켓을 바탕으로 데이터 입출력 및 이벤트 처리를 한다.
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
        	
        	// Netty bootstrap : 애플리케이션의 각종 동작, 설정 지정 헬퍼
            ServerBootstrap bootstrap = new ServerBootstrap();
            
            // bootstrap에 thread를 등록한다. (boss, worker)
            bootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class) // boss thread 가 사용할 네트워크 입출력 모드
            .handler(new LoggingHandler(LogLevel.INFO)) // worker thread 에서 발생한 이벤트에 대한 로그 출력
            .childHandler(new ChatServerInitializer(sslCtx)); 
            
            // 서버를 비동기식으로 바인딩 후 채널의 closeFuture 를 얻어 완료될 때까지 현재 스레드를 블록킹
            bootstrap.bind(port).sync().channel().closeFuture().sync();
            
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
