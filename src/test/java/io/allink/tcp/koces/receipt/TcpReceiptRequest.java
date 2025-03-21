package io.allink.tcp.koces.receipt;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;


public class TcpReceiptRequest {

    private final String host = "43.203.61.90"; // 43.203.61.90
    private final int port = 10033;

    @Test
    public void run() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    /*
    public static void main(String[] args) throws InterruptedException {
        new TcpReceiptRequest("127.0.0.1", 10033).run();
    }
    */

    static class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            String message = createMessage();
            ByteBuf buffer = Unpooled.copiedBuffer(message, StandardCharsets.UTF_8);
            ctx.writeAndFlush(buffer);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
            String response = msg.toString(StandardCharsets.UTF_8);
            System.out.println("Server Response: " + response);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        private String createMessage() {
            StringBuilder sb = new StringBuilder();
            sb.append(formatFixedLength("0600", 4)); // 전문길이 (4)
            sb.append(formatFixedLength("", 4)); // 응답코드 (4)
            sb.append(formatFixedLength("CC", 2)); // 서비스타입 (2)
            sb.append(formatFixedLength("D1", 2)); // 업무구분 (2)
            sb.append(formatFixedLength("000000010653", 20)); // 거래고유번호 (20)
            sb.append(formatFixedLength("0000000000", 10)); // Terminal ID (10)
            sb.append(formatFixedLength("1231212312", 10)); // 사업자번호 (10) // 6038203952
            sb.append(formatFixedLength("1234567891234567", 70)); // 카드번호 (70)
            sb.append(formatFixedLength("000", 3)); // 페이구분 (3)
            sb.append(formatFixedLength("00", 2)); // 할부개월 (2)
            sb.append(rightJustifyWithZero("000000019200", 12)); // 총승인금액 (12)
            sb.append(rightJustifyWithZero("000000000000", 12)); // 봉사료 (12)
            sb.append(rightJustifyWithZero("000000001745", 12)); // 세금 (12)
            sb.append(formatFixedLength("", 12)); // 금액1 (12)
            sb.append(formatFixedLength("", 12)); // 금액2 (12)
            sb.append(formatFixedLength("34272067", 40)); // 승인번호 (40)
            sb.append(formatFixedLength("20250312", 8)); // 전송일자 (8)
            sb.append(formatFixedLength("000928", 6)); // 전송시간 (6)
            sb.append(formatFixedLength("", 8)); // 원거래일자 (8)
            sb.append(formatFixedLength("", 20)); // 원거래고유번호 (20)
            sb.append(formatFixedLength("", 40)); // 원승인번호 (40)
            sb.append(formatFixedLength("", 1)); // 취소구분 (1)
            sb.append(formatFixedLength("1104", 4)); // 발급사코드 (4)
            sb.append(formatFixedLength("1104", 4)); // 매입사코드 (4)
            sb.append(formatFixedLength("1", 1)); // DDC여부 (1)
            sb.append(formatFixedLength("I", 1)); // Swipe구분 (1)
            sb.append(formatFixedLength("72643966", 16)); // 가맹점번호 (16)
            sb.append(formatFixedLength("", 100)); // 가맹점데이터 (100)
            sb.append(formatFixedLength("N", 1)); // 체크카드여부 (1)
            sb.append(formatFixedLength("N", 1)); // 해외카드여부 (1)
            sb.append(formatFixedLength("N", 1)); // 전자서명여부 (1)
            sb.append(formatFixedLength("", 2)); // VAN사 코드 (2)
            sb.append(formatFixedLength("", 159)); // 여유필드 (159)
            return sb.toString();
        }


        // 주어진 문자열을 지정된 길이로 맞추고, 모자란 부분을 '0'으로 채워 우측 정렬한다.
        public String rightJustifyWithZero(String value, int length) {
            if (value == null) {
                value = "";
            }
            if (value.length() >= length) {
                return value.substring(value.length() - length); // 초과하면 오른쪽 일부만 반환
            }
            return String.format("%" + length + "s", value).replace(' ', '0');
        }

        // 주어진 문자열을 지정된 길이로 맞추고, 모자란 부분을 Space(' ')로 채워 좌측 정렬한다.
        public String leftJustifyWithSpace(String value, int length) {
            if (value == null) {
                value = "";
            }
            if (value.length() >= length) {
                return value.substring(0, length); // 초과하면 왼쪽 일부만 반환
            }
            return String.format("%-" + length + "s", value);
        }

        // 기본적인 고정 길이 포맷 (기본은 Space Padding)
        public String formatFixedLength(String value, int length) {
            return leftJustifyWithSpace(value, length);
        }

    }
}
