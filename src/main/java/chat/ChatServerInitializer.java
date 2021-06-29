package chat;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
 
public class ChatServerInitializer extends ChannelInitializer<SocketChannel> {
 
    private final SslContext sslCtx;
    
    public ChatServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }
    
    // 클라이언트 소켓 채널이 생성될 때 호출된다.
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
    	
    	// Netty에서 할당한 빈 채널 pipeline을 가져온다.
        ChannelPipeline pipeline = socketChannel.pipeline();
        
        // if android client, remove this
        pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));

        // if android client, add this
        // pipeline.addLast(new ByteToMessageDecoder() {
        //     @Override
        //     public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //         out.add(in.readBytes(in.readableBytes()));
        //     }
        // });

        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new StringEncoder());
        
        // pipeline에 handler를 등록한다.
        pipeline.addLast(new ChatServerHandler());
 
    }
 
}