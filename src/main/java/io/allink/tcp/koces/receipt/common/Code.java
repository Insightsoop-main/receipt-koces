package io.allink.tcp.koces.receipt.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Package: io.allink.tcp.koces.receipt.common Created: Devonshin Date: 29/03/2025
 */

public class Code {

  public static final Map<String, String> TRD_TYPE_MAP = new HashMap<>() {{
    putAll(Map.of(
        "CCD1", "승인",
        "CCD2", "취소",
        "CCJ1", "전화승인",
        "CCJ2", "전화승인취소",
        "CBH1", "승인",
        "CBH2", "취소",
        "CIC1", "승인",
        "CIC2", "취소",
        "PAP1", "승인",
        "PAP2", "취소"
    ));
  }};

  public static final Map<String, String> PAY_TYPE_MAP = new HashMap<>() {{
    putAll(Map.of(
        "0", "일반",
        "ZRP", "제로페이",
        "SSP", "삼성페이",
        "KKM", "카카오페이",
        "KKO", "카카오페이",
        "TSM", "토스페이",
        "TSO", "토스페이"
    ));
  }};

  public static final Map<String, String> SVC_TYPE_MAP = new HashMap<>() {{
    putAll(Map.of(
        "CC", "신용거래",
        "CB", "현금영수증",
        "CI", "현금IC",
        "PA", "페이",
        "0000", "정상",
        "ER01", "중복데이터",
        "ER02", "미등록가맹점",
        "ER03", "내부시스템장애",
        "ER04", "재전송요청"
    ));
  }};

  public static final Map<String, String> SWIPE_MAP = new HashMap<>() {{
    putAll(Map.of(
        "1", "IC",
        "S", "Swipe",
        "K", "Key-in",
        "B", "Barcode",
        "Q", "QRCode"
    ));
  }};

  public static final Map<String, String> FOREIGN_YN_MAP = new HashMap<>() {{
    putAll(Map.of(
        "CCY", "해외카드",
        "CCN", "국내카드",
        "CIN", "일반가맹점",
        "CIP", "공공가맹점",
        "CIS", "영세가맹점"
    ));
  }};

  public static final Map<String, String> DDC_YN_MAP = new HashMap<>() {{
    putAll(Map.of(
        "CB0", "개인",
        "CB1", "법인",
        "CC1", "DDC",
        "CC0", "해당없음"
    ));
  }};

  public static final Map<String, String> CHECK_YN_MAP = new HashMap<>() {{
    putAll(Map.of(
        "CCY", "체크카드",
        "CCN", "신용카드",
        "CB1", "거래취소",
        "CB2", "오류발급",
        "CB3", "기타",
        "CIY", "캐쉬백 O",
        "CIN", "캐쉬백 X"
    ));
  }};

  public static final Map<String, String> CANCEL_TYPE_MAP = new HashMap<>() {{
    putAll(Map.of(
        "0", "일반취소",
        "1", "망취소",
        "2", "자동취소",
        "a", "거래고유번호취소"
    ));
  }};

  /*public static final Map<String, String> BANK_MAP = new HashMap<>() {{
    putAll(Map.of(
        "002",	"산업은행",
        "003",	"기업은행",
        "004",	"국민은행",
        "007",	"수협중앙",
        "011",	"농협은행",
        "012",	"지역농축",
        "020",	"우리은행",
        "023",	"한국SC"
    ));
    putAll(Map.of(
        "027",	"한국씨티",
        "029",	"국민은행",
        "031",	"대구은행",
        "032",	"부산은행",
        "034",	"광주은행",
        "035",	"제주은행",
        "037",	"전북은행",
        "039",	"경남은행",
        "045",	"새마을",
        "048",	"신협중앙"
    ));
    putAll(Map.of(
        "064",	"산림조합",
        "081",	"하나은행",
        "088",	"신한은행"
    ));
  }};*/
/*
  public static final Map<String, String> NUMBER_TYPE_MAP = new HashMap<>() {{
    putAll(Map.of(
        "CC", "카드번호",
        "CB", "카드번호 or 식별번호",
        "CI", "IC일련번호",
        "PA", "바코드 or QR"
    ));
  }};
*/

  public static final Map<String, String> CARD_COMPANY_MAP = new HashMap<>() {{
    putAll(Map.of(
        "1101", "국민카드",
        "1102", "현대카드",
        "1103", "롯데카드",
        "1104", "삼성카드",
        "1105", "하나카드",
        "1106", "비씨카드",
        "1107", "신한카드",
        "1113", "지드림카드",
        "1180", "해외 VISA",
        "1181", "해외 MASTER"
    ));
    putAll(Map.of(
        "1182", "해외 JCB",
        "2207", "수협",
        "2209", "신협",
        "2211", "NH카드",
        "2227", "한미은행",
        "2234", "광주은행",
        "2235", "제주은행",
        "2237", "전북은행",
        "2281", "하나카드",
        "3305", "롯데카드"
    ));
    putAll(Map.of(
        "3308", "코나아이",
        "3315", "카카오페이",
        "3323", "토스페이"
    ));
  }};


}
