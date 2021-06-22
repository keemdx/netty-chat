package chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
 
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
 
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
 
    // 사용자가 추가되었을 때 기존 사용자에게 알린다.
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerAdded of [SERVER]");
        Channel incoming = ctx.channel();
        for (Channel channel : channelGroup) {
          
            channel.write("[SERVER] - " + incoming.remoteAddress() + "has joined!\n");
        }
        channelGroup.add(incoming);
    }
    
    // 사용자가 접속했을 때 서버에 표시한다.
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        
        System.out.println("User Access!");
    }
 
    // 사용자가 나갈 경우 기존 사용자에게 알린다.
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handlerRemoved of [SERVER]");
        Channel incoming = ctx.channel();
        for (Channel channel : channelGroup) {
            channel.write("[SERVER] - " + incoming.remoteAddress() + "has left!\n");
        }
        channelGroup.remove(incoming);
    }
 
    // 메시지가 들어오면 (데이터 수신 시) 호출된다.
    @Override 
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	
        String message = null;
        message = (String)msg;
        System.out.println("channelRead of [SERVER]" +  message);
        Channel incoming = ctx.channel();
        for (Channel channel : channelGroup) {
        	
        	//메시지를 전달한다.
            if (channel != incoming) {
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]" + message + "\n");
            }
        }
        if ("bye".equals(message.toLowerCase())) {
            ctx.close();
        }
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
 
}
