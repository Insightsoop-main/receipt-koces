package io.allink.tcp.koces.receipt.protocol;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.allink.tcp.koces.receipt.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Setter
@Getter
@ToString
public class KocesMessage {
  @JsonIgnore
  private String msgLength;       // 전문길이 (4)
  @JsonIgnore
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

  @JsonIgnore
  public String getResponse() {
    return StringUtil.formatFixedLength(this.msgLength, 4) +       // 전문길이 (4)
        StringUtil.formatFixedLength(this.answerCd, 4) +        // 응답코드 (4)
        StringUtil.formatFixedLength(this.svcType, 2) +         // 서비스타입 (2)
        StringUtil.formatFixedLength(this.trdType, 2) +         // 업무구분 (2)
        StringUtil.formatFixedLength(this.trdUniKey, 20) +      // 거래고유번호 (20)
        StringUtil.formatFixedLength(this.termId, 10) +         // Terminal ID (10)
        StringUtil.formatFixedLength(this.businessNo, 10) +     // 사업자번호 (10)
        StringUtil.formatFixedLength(this.cardNo, 70) +         // 카드번호 or 바코드번호 (70)
        StringUtil.formatFixedLength(this.payGubun, 3) +        // 페이구분 (3)
        StringUtil.formatFixedLength(this.insMon, 2) +          // 할부개월 (2)
        StringUtil.formatFixedLength(this.trdAmtTot, 12) +      // 총승인금액 (12)
        StringUtil.formatFixedLength(this.svcAmt, 12) +         // 봉사료 (12)
        StringUtil.formatFixedLength(this.taxAmt, 12) +         // 세금 (12)
        StringUtil.formatFixedLength(this.amt1, 12) +           // 금액1 (12)
        StringUtil.formatFixedLength(this.amt2, 12) +           // 금액2 (12)
        StringUtil.formatFixedLength(this.auNo, 40) +           // 승인번호 (40)
        StringUtil.formatFixedLength(this.transDate, 8) +       // 전송일자 (8)
        StringUtil.formatFixedLength(this.transTime, 6) +       // 전송시간 (6)
        StringUtil.formatFixedLength(this.auDate, 8) +          // 원거래일자 (8)
        StringUtil.formatFixedLength(this.oriTrdUniKey, 20) +   // 원거래고유번호 (20)
        StringUtil.formatFixedLength(this.oriAuNo, 40) +        // 원승인번호 (40)
        StringUtil.formatFixedLength(this.cancelCd, 1) +        // 취소구분 (1)
        StringUtil.formatFixedLength(this.issCd, 4) +           // 발급사코드 (4)
        StringUtil.formatFixedLength(this.buyCd, 4) +           // 매입사코드 (4)
        StringUtil.formatFixedLength(this.ddcYn, 1) +           // DDC여부 / 개인법인구분 (1)
        StringUtil.formatFixedLength(this.swipe, 1) +           // Swipe구분 (1)
        StringUtil.formatFixedLength(this.mchNo, 16) +          // 가맹점번호 (16)
        StringUtil.formatFixedLength(this.mchData, 100) +       // 가맹점데이터 (100)
        StringUtil.formatFixedLength(this.checkYn, 1) +         // 체크카드여부 / 취소사유 / 캐시백여부 (1)
        StringUtil.formatFixedLength(this.foreignYn, 1) +       // 해외카드여부 / 우대가맹점구분 (1)
        StringUtil.formatFixedLength(this.dscYn, 1) +           // 전자서명여부 (1)
        StringUtil.formatFixedLength(this.vanCode, 2) +         // VAN사 코드 (2)
        StringUtil.formatFixedLength(this.filler, 159);           // 여유필드 (159)
  }

  public KocesMessage() {
  }

  public KocesMessage(KocesMessage message) {
    this.msgLength = message.getMsgLength();
    this.answerCd = message.getAnswerCd();
    this.svcType = message.getSvcType();
    this.trdType = message.getTrdType();
    String pref = "";
    if("D2".equals(trdType)) {
      pref = "-";
    }
    this.trdUniKey = message.getTrdUniKey();
    this.termId = message.getTermId();
    this.businessNo = message.getBusinessNo();
    this.cardNo = message.getCardNo();
    this.payGubun = message.getPayGubun();
    this.insMon = message.getInsMon();
    this.trdAmtTot = String.valueOf(Long.parseLong(pref + StringUtil.defaultVal(message.getTrdAmtTot(), "0")));
    this.svcAmt = String.valueOf(Long.parseLong(pref + StringUtil.defaultVal(message.getSvcAmt(), "0")));
    this.taxAmt = String.valueOf(Long.parseLong(pref + StringUtil.defaultVal(message.getTaxAmt(), "0")));
    this.amt1 = String.valueOf(Long.parseLong(pref + StringUtil.defaultVal(message.getAmt1(), "0")));
    this.amt2 = String.valueOf(Long.parseLong(pref + StringUtil.defaultVal(message.getAmt2(), "0")));
    this.auNo = message.getAuNo();
    this.transDate = message.getTransDate();
    this.transTime = message.getTransTime();
    this.auDate = message.getAuDate();
    this.oriTrdUniKey = message.getOriTrdUniKey();
    this.oriAuNo = message.getOriAuNo();
    this.cancelCd = message.getCancelCd();
    this.issCd = message.getIssCd();
    this.buyCd = message.getBuyCd();
    this.ddcYn = message.getDdcYn();
    this.swipe = message.getSwipe();
    this.mchNo = message.getMchNo();
    this.mchData = message.getMchData();
    this.checkYn = message.getCheckYn();
    this.foreignYn = message.getForeignYn();
    this.dscYn = message.getDscYn();
    this.vanCode = message.getVanCode();
    this.filler = message.getFiller();
  }
}
