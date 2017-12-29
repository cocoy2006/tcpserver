import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.io.FileOutputStream;

/**
 * Created by xujiayu on 17/12/28.
 */
public class TcpServerHandler extends ChannelInboundHandlerAdapter {
    private String filename;

    public TcpServerHandler(String filename) {
        this.filename = filename;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inBuffer = (ByteBuf) msg;
        byte [] bytes = new byte[inBuffer.readableBytes()];
        inBuffer.readBytes(bytes);
        String binStr = new String(bytes);

        String hexStr = binToHex(bytes);
        writeToFile(filename, hexStr);

        ctx.write(Unpooled.copiedBuffer("Hello " + binStr, CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    // 写入文件
    public void writeToFile(String filename, String hexStr) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        fileOutputStream.write(hexStr.getBytes());
        fileOutputStream.close();
    }

    // 二进制转十六进制
    public static String binToHex(byte[] bytes) {
        String string = new String(bytes);
        return Long.toHexString(Long.parseLong(string, 2));
    }
}
