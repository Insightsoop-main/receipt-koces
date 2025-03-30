package io.allink.tcp.koces.receipt.server;

import io.allink.tcp.koces.receipt.protocol.KocesMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TcpDecoder extends ByteToMessageDecoder {

    public static final int REQUEST_LENGTH = 600;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 데이터 길이 체크 (최소 길이 검증)
        if (in.readableBytes() < REQUEST_LENGTH) { // 최소한의 길이 검증
            return;
        }

        KocesMessage receipt = new KocesMessage();

        receipt.setMsgLength(readString(in, 4)); // 전문길이 (4)
        receipt.setAnswerCd(readString(in, 4)); // 응답코드 (4)
        receipt.setSvcType(readString(in, 2)); // 서비스타입 (2)
        receipt.setTrdType(readString(in, 2)); // 업무구분 (2)
        receipt.setTrdUniKey(readString(in, 20)); // 거래고유번호 (20)
        receipt.setTermId(readString(in, 10)); // Terminal ID (10)
        receipt.setBusinessNo(readString(in, 10)); // 사업자번호 (10)
        receipt.setCardNo(readString(in, 70)); // 카드번호 or 바코드번호 (70)
        receipt.setPayGubun(readString(in, 3)); // 페이구분 (3)
        receipt.setInsMon(readString(in, 2)); // 할부개월 (2)
        receipt.setTrdAmtTot(readString(in, 12)); // 총승인금액 (12)
        receipt.setSvcAmt(readString(in, 12)); // 봉사료 (12)
        receipt.setTaxAmt(readString(in, 12)); // 세금 (12)
        receipt.setAmt1(readString(in, 12)); // 금액1 (12)
        receipt.setAmt2(readString(in, 12)); // 금액2 (12)
        receipt.setAuNo(readString(in, 40)); // 승인번호 (40)
        receipt.setTransDate(readString(in, 8)); // 전송일자 (8)
        receipt.setTransTime(readString(in, 6)); // 전송시간 (6)
        receipt.setAuDate(readString(in, 8)); // 원거래일자 (8)
        receipt.setOriTrdUniKey(readString(in, 20)); // 원거래고유번호 (20)
        receipt.setOriAuNo(readString(in, 40)); // 원승인번호 (40)
        receipt.setCancelCd(readString(in, 1)); // 취소구분 (1)
        receipt.setIssCd(readString(in, 4)); // 발급사코드 (4)
        receipt.setBuyCd(readString(in, 4)); // 매입사코드 (4)
        receipt.setDdcYn(readString(in, 1)); // DDC여부 / 개인법인구분 (1)
        receipt.setSwipe(readString(in, 1)); // Swipe구분 (1)
        receipt.setMchNo(readString(in, 16)); // 가맹점번호 (16)
        receipt.setMchData(readString(in, 100)); // 가맹점데이터 (100)
        receipt.setCheckYn(readString(in, 1)); // 체크카드여부 / 취소사유 / 캐시백여부 (1)
        receipt.setForeignYn(readString(in, 1)); // 해외카드여부 / 우대가맹점구분 (1)
        receipt.setDscYn(readString(in, 1)); // 전자서명여부 (1)
        receipt.setVanCode(readString(in, 2)); // VAN사 코드 (2)
        receipt.setFiller(readString(in, 159)); // 여유필드 (159)

        // ServerHandler 에 전달
        out.add(receipt);
    }

    private String readString(ByteBuf in, int length) {
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }
}
