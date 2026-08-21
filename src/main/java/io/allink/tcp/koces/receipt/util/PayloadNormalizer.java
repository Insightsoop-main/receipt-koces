package io.allink.tcp.koces.receipt.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.allink.tcp.koces.receipt.model.Store;
import io.allink.tcp.koces.receipt.protocol.KocesMessage;
import lombok.Data;

/**
 * KOCES 영수증 → 표준 normalized_payload(JSONB) 변환기.
 *
 * 입력 약속:
 *   - p (KocesMessage): payload JSON 의 PayInfos[0] 와 동일한 객체.
 *       이미 한글화/마스킹이 적용된 상태 (svcType/trdType/issCd/buyCd/cardNo/insMon 등).
 *   - store (Store):    가맹점 정보 (storeName/addr1/addr2/businessNo 등).
 *
 * 매핑 규칙은 외부 명세에 따른다.
 * 예외로 INSERT 를 막지 않는다 — 누락 시 빈 문자열("") 또는 0.
 */
public final class PayloadNormalizer {

    private static final ObjectMapper OM = new ObjectMapper();
    private static final String VAN_TYPE = "KOCES";
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter ISO_KST =
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    private PayloadNormalizer() {}

    public static String toNormalizedJson(Store store, KocesMessage p) {
        try {
            Normalized n = new Normalized();
            n.vanType           = VAN_TYPE;
            n.storeName         = store != null ? safe(store.getStoreName()) : "";
            n.storeAddress      = joinAddress(store);
            n.businessNumber    = firstNonEmpty(
                p != null ? p.getBusinessNo() : null,
                store != null ? store.getBusinessNo() : null
            );
            n.cardNumber        = safe(p != null ? p.getCardNo()   : null);
            n.cardType          = safe(p != null ? p.getIssCd()    : null); // KOCES: 카드상품명 없음 → 발급사명 사용
            n.issuer            = safe(p != null ? p.getIssCd()    : null);
            n.acquirer          = safe(p != null ? p.getBuyCd()    : null);
            n.approvalNumber    = safe(p != null ? p.getAuNo()     : null);
            n.installment       = isBlank(p != null ? p.getInsMon() : null) ? "일시불"
                                  : p.getInsMon();
            n.terminalNumber    = maskTerminal(p != null ? p.getTermId() : null);
            n.merchantNumber    = safe(p != null ? p.getMchNo()    : null);
            n.vanMerchantNumber = safe(p != null ? p.getMchNo()    : null);
            n.total             = toInt(p != null ? p.getTrdAmtTot() : null);
            n.tax               = toInt(p != null ? p.getTaxAmt()    : null);
            n.serviceAmount     = toInt(p != null ? p.getSvcAmt()    : null);
            n.amount            = n.total - n.tax - n.serviceAmount;
            // 거래구분(승인/취소)은 취소 영수증을 승인과 구분하는 유일한 근거다.
            // 취소는 원거래의 승인번호를 그대로 실어오므로, 이 값이 없으면
            // 소비 측에서 "같은 승인번호 = 중복"으로 오판해 취소 영수증이 사라진다.
            n.trdType           = safe(p != null ? p.getTrdType()  : null);
            n.cancelCode        = safe(p != null ? p.getCancelCd() : null);
            n.transactionDate   = p != null
                ? toKstIso(p.getTransDate(), p.getTransTime())
                : "";
            return OM.writeValueAsString(n);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("normalized_payload 변환 오류", e);
        }
    }

    // --- helpers --------------------------------------------------

    private static String safe(String s) { return s == null ? "" : s; }
    private static boolean isBlank(String s) { return s == null || s.isEmpty(); }

    private static String firstNonEmpty(String... vals) {
        if (vals == null) return "";
        for (String v : vals) {
            if (!isBlank(v)) return v;
        }
        return "";
    }

    /** addr1 과 addr2 를 공백 1칸으로 연결 (빈값 제외) */
    private static String joinAddress(Store store) {
        if (store == null) return "";
        String a1 = store.getAddr1();
        String a2 = store.getAddr2();
        StringBuilder sb = new StringBuilder();
        if (!isBlank(a1)) sb.append(a1);
        if (!isBlank(a2)) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(a2);
        }
        return sb.toString();
    }

    /** "5712420291" → "**12420291". 길이 <= 2 면 "**". 비면 "" */
    private static String maskTerminal(String termId) {
        if (isBlank(termId)) return "";
        if (termId.length() <= 2) return "**";
        return "**" + termId.substring(2);
    }

    /** null/공백→0, 콤마 제거 후 parseInt, NaN→0 */
    private static int toInt(String s) {
        if (isBlank(s)) return 0;
        try {
            return Integer.parseInt(s.replace(",", "").trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * d="YYYYMMDD" 또는 "YYMMDD", t="HHMMSS" → "YYYY-MM-DDTHH:mm:ss+09:00".
     * 이미 오프셋/Z 가 포함된 ISO 입력은 그대로 반환. 파싱 실패 시 빈 문자열.
     */
    private static String toKstIso(String date, String time) {
        if (isBlank(date)) return "";
        String d = date.trim();
        // 이미 오프셋/Z 포함된 ISO 형태면 그대로 반환 (예: 2026-05-26T11:40:35+09:00 / ...Z)
        if (d.length() >= 10 && d.charAt(4) == '-') {
            return d;
        }
        try {
            if (d.length() == 6) d = "20" + d; // YYMMDD → 20YYMMDD
            if (d.length() != 8) return "";
            String t = isBlank(time) ? "000000" : time.trim();
            if (t.length() != 6) return "";
            int y  = Integer.parseInt(d.substring(0, 4));
            int mo = Integer.parseInt(d.substring(4, 6));
            int dd = Integer.parseInt(d.substring(6, 8));
            int hh = Integer.parseInt(t.substring(0, 2));
            int mm = Integer.parseInt(t.substring(2, 4));
            int ss = Integer.parseInt(t.substring(4, 6));
            LocalDateTime ldt = LocalDateTime.of(y, mo, dd, hh, mm, ss);
            ZonedDateTime zdt = ldt.atZone(KST);
            return zdt.format(ISO_KST);
        } catch (Exception e) {
            return "";
        }
    }

    // --- DTO ------------------------------------------------------

    @Data
    @JsonInclude(JsonInclude.Include.ALWAYS)
    public static class Normalized {
        @JsonProperty("van_type")            public String vanType;
        @JsonProperty("store_name")          public String storeName;
        @JsonProperty("store_address")       public String storeAddress;
        @JsonProperty("business_number")     public String businessNumber;
        @JsonProperty("card_number")         public String cardNumber;
        @JsonProperty("card_type")           public String cardType;
        @JsonProperty("issuer")              public String issuer;
        @JsonProperty("acquirer")            public String acquirer;
        @JsonProperty("approval_number")     public String approvalNumber;
        @JsonProperty("installment")         public String installment;
        @JsonProperty("terminal_number")     public String terminalNumber;
        @JsonProperty("merchant_number")     public String merchantNumber;
        @JsonProperty("van_merchant_number") public String vanMerchantNumber;
        @JsonProperty("total")               public int total;
        @JsonProperty("tax")                 public int tax;
        @JsonProperty("service_amount")      public int serviceAmount;
        @JsonProperty("amount")              public int amount;
        @JsonProperty("trd_type")            public String trdType;      // 승인 | 취소
        @JsonProperty("cancel_code")         public String cancelCode;   // 일반취소 | 망취소 | …
        @JsonProperty("transaction_date")    public String transactionDate;
    }
}
