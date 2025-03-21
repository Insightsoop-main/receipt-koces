package io.allink.tcp.koces.receipt.protocol;

import io.allink.tcp.koces.receipt.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Setter
@Getter
public class pReceipt {
    private String msgLength;       // 전문길이 (4)
    private String answerCd;        // 응답코드 (4)
    private String svcType;         // 서비스타입 (2) CC : 신용 거래, CB : 현금영수증, CI : 현금IC, PA : 페이, 정상:0000, 중복데이터:ER01, 미등록가맹점:ER02, 내부시스템장애:ER03, 재전송요청:ER04
    private String trdType;         // 업무구분 (2)CC : D1(승인), D2(취소), J1(전화승인), J2(전화승인취소), CB : H1(승인), H2(취소), CI : C1(승인), C2(취소), PA : P1(승인), P2(취소)
    private String trdUniKey;       // 거래고유번호 (20)
    private String termId;          // Terminal ID (10)
    private String businessNo;      // 사업자번호 (10)
    private String cardNo;          // 카드번호 or 바코드번호 (70) CC : 카드번호, CB : 카드번호 or 식별번호, CI : IC일련번호, PA : 바코드 or QR
    private String payGubun;        // 페이구분 (3) 000 : 일반, SSP : 삼성페이(BC카드 제외), ZRP : 제로페이, KKM : 카카오페이(MONEY), KKO : 카카오페이(OTC), TSM : 토스페이(MONEY/계좌), TSO : 토스페이(OTC)
    private String insMon;          // 할부개월 (2) 3개월:"03", 일시불:"00"
    private String trdAmtTot;       // 총승인금액 (12)
    private String svcAmt;          // 봉사료 (12)
    private String taxAmt;          // 세금 (12)
    private String amt1;            // 금액1 (12) CI : 캐쉬백금액, 그외 : 현재미사용
    private String amt2;            // 금액2 (12) CI : 가맹점수수료, 그외 : 현재미사용
    private String auNo;            // 승인번호 (40)
    private String transDate;       // 전송일자 (8) YYYYMMDD
    private String transTime;       // 전송시간 (6) hh24miss
    private String auDate;          // 원거래일자 (8)
    private String oriTrdUniKey;    // 원거래고유번호 (20)
    private String oriAuNo;         // 원승인번호 (40)
    private String cancelCd;        // 취소구분 (1) 0(일반취소), 1(망취소), 2(자동취소), a(거래고유번호취소)
    private String issCd;           // 발급사코드 (4) 카드사 코드표
    private String buyCd;           // 매입사코드 (4) 카드사 코드표
    private String ddcYn;           // DDC여부 / 개인법인구분 (1) CC(DDC여부) : 1(DDC), 0(해당없음), CB(개인법인구분) : 0(개인), 1(법인)
    private String swipe;           // Swipe구분 (1) S:Swipe, K:key-in, I:IC, B:Barcode, Q:QRCode
    private String mchNo;           // 가맹점번호 (16)
    private String mchData;         // 가맹점데이터 (100)
    private String checkYn;         // 체크카드여부 / 취소사유 / 캐시백여부 (1) CC(체크카드여부) : Y(체크카드), N(신용카드), CB(취소사유): 1(거래취소), 2(오류발급), 3(기타), CI(캐쉬백여부)   : Y(캐쉬백 O), N(캐쉬백 X)
    private String foreignYn;       // 해외카드여부 / 우대가맹점구분 (1) CC(해외카드여부) : Y(해외카드), N(국내카드), CI(우대가맹점구분) : N(일반가맹점), P(공공가맹점), S(영세가맹점)
    private String dscYn;           // 전자서명여부 (1) Y : 전자서명, N : 그외
    private String vanCode;         // VAN사 코드 (2) 01 : KCP, space : KOCES
    private String filler;          // 여유필드 (159) 현재 미사용

    public String setReceipt(){
        StringBuffer sb = new StringBuffer();

        sb.append(StringUtil.formatFixedLength(this.msgLength, 4));       // 전문길이 (4)
        sb.append(StringUtil.formatFixedLength(this.answerCd, 4));        // 응답코드 (4)
        sb.append(StringUtil.formatFixedLength(this.svcType, 2));         // 서비스타입 (2)
        sb.append(StringUtil.formatFixedLength(this.trdType, 2));         // 업무구분 (2)
        sb.append(StringUtil.formatFixedLength(this.trdUniKey, 20));      // 거래고유번호 (20)
        sb.append(StringUtil.formatFixedLength(this.termId, 10));         // Terminal ID (10)
        sb.append(StringUtil.formatFixedLength(this.businessNo, 10));     // 사업자번호 (10)
        sb.append(StringUtil.formatFixedLength(this.cardNo, 70));         // 카드번호 or 바코드번호 (70)
        sb.append(StringUtil.formatFixedLength(this.payGubun, 3));        // 페이구분 (3)
        sb.append(StringUtil.formatFixedLength(this.insMon, 2));          // 할부개월 (2)
        sb.append(StringUtil.formatFixedLength(this.trdAmtTot, 12));      // 총승인금액 (12)
        sb.append(StringUtil.formatFixedLength(this.svcAmt, 12));         // 봉사료 (12)
        sb.append(StringUtil.formatFixedLength(this.taxAmt, 12));         // 세금 (12)
        sb.append(StringUtil.formatFixedLength(this.amt1, 12));           // 금액1 (12)
        sb.append(StringUtil.formatFixedLength(this.amt2, 12));           // 금액2 (12)
        sb.append(StringUtil.formatFixedLength(this.auNo, 40));           // 승인번호 (40)
        sb.append(StringUtil.formatFixedLength(this.transDate, 8));       // 전송일자 (8)
        sb.append(StringUtil.formatFixedLength(this.transTime, 6));       // 전송시간 (6)
        sb.append(StringUtil.formatFixedLength(this.auDate, 8));          // 원거래일자 (8)
        sb.append(StringUtil.formatFixedLength(this.oriTrdUniKey, 20));   // 원거래고유번호 (20)
        sb.append(StringUtil.formatFixedLength(this.oriAuNo, 40));        // 원승인번호 (40)
        sb.append(StringUtil.formatFixedLength(this.cancelCd, 1));        // 취소구분 (1)
        sb.append(StringUtil.formatFixedLength(this.issCd, 4));           // 발급사코드 (4)
        sb.append(StringUtil.formatFixedLength(this.buyCd, 4));           // 매입사코드 (4)
        sb.append(StringUtil.formatFixedLength(this.ddcYn, 1));           // DDC여부 / 개인법인구분 (1)
        sb.append(StringUtil.formatFixedLength(this.swipe, 1));           // Swipe구분 (1)
        sb.append(StringUtil.formatFixedLength(this.mchNo, 16));          // 가맹점번호 (16)
        sb.append(StringUtil.formatFixedLength(this.mchData, 100));       // 가맹점데이터 (100)
        sb.append(StringUtil.formatFixedLength(this.checkYn, 1));         // 체크카드여부 / 취소사유 / 캐시백여부 (1)
        sb.append(StringUtil.formatFixedLength(this.foreignYn, 1));       // 해외카드여부 / 우대가맹점구분 (1)
        sb.append(StringUtil.formatFixedLength(this.dscYn, 1));           // 전자서명여부 (1)
        sb.append(StringUtil.formatFixedLength(this.vanCode, 2));         // VAN사 코드 (2)
        sb.append(StringUtil.formatFixedLength(this.filler, 159));        // 여유필드 (159)

        return sb.toString();
    }


    public void getReceiptPrint() {
        log.debug("[request] msgLength: {}", this.msgLength);
        log.debug("[request] answerCd: {}", this.answerCd);
        log.debug("[request] svcType: {}", this.svcType);
        log.debug("[request] trdType: {}", this.trdType);
        log.debug("[request] trdUniKey: {}", this.trdUniKey);
        log.debug("[request] termId: {}", this.termId);
        log.debug("[request] businessNo: {}", this.businessNo);
        log.debug("[request] cardNo: {}", this.cardNo);
        log.debug("[request] payGubun: {}", this.payGubun);
        log.debug("[request] insMon: {}", this.insMon);
        log.debug("[request] trdAmtTot: {}", this.trdAmtTot);
        log.debug("[request] svcAmt: {}", this.svcAmt);
        log.debug("[request] taxAmt: {}", this.taxAmt);
        log.debug("[request] amt1: {}", this.amt1);
        log.debug("[request] amt2: {}", this.amt2);
        log.debug("[request] auNo: {}", this.auNo);
        log.debug("[request] transDate: {}", this.transDate);
        log.debug("[request] transTime: {}", this.transTime);
        log.debug("[request] auDate: {}", this.auDate);
        log.debug("[request] oriTrdUniKey: {}", this.oriTrdUniKey);
        log.debug("[request] oriAuNo: {}", this.oriAuNo);
        log.debug("[request] cancelCd: {}", this.cancelCd);
        log.debug("[request] issCd: {}", this.issCd);
        log.debug("[request] buyCd: {}", this.buyCd);
        log.debug("[request] ddcYn: {}", this.ddcYn);
        log.debug("[request] swipe: {}", this.swipe);
        log.debug("[request] mchNo: {}", this.mchNo);
        log.debug("[request] mchData: {}", this.mchData);
        log.debug("[request] checkYn: {}", this.checkYn);
        log.debug("[request] foreignYn: {}", this.foreignYn);
        log.debug("[request] dscYn: {}", this.dscYn);
        log.debug("[request] vanCode: {}", this.vanCode);
        log.debug("[request] filler: {}", this.filler);
    }
}
