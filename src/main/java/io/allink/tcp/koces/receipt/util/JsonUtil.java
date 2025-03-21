package io.allink.tcp.koces.receipt.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.allink.tcp.koces.receipt.model.ReceiptEntity;
import io.allink.tcp.koces.receipt.model.StoreEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(Object mert, Object object) {

        StoreEntity storeEntity = (StoreEntity) mert;
        ReceiptEntity jsonReceiptEntity = (ReceiptEntity) object;

        String payType1;
        String payAttr;

        String trxDate = jsonReceiptEntity.getTransDate() + jsonReceiptEntity.getTransTime();
        trxDate = DateUtil.getYMD(trxDate);

        // ■ 결제 유형
        switch (jsonReceiptEntity.getSvcType()) {
            case "CC": payType1 = "CARD"; break;
            case "CI": payType1 = "CASH";break;
            case "PA": payType1 = "SPAY"; break;
            default: payType1 = "CARD";
        }

        // ■ 결제 속성
        switch (jsonReceiptEntity.getPayGubun().replaceAll(" ", "")) {
            case "승인": payAttr = "PAY"; break;
            case "취소": payAttr = "CNL"; break;
            case "전화승인": payAttr = "PAY"; break;
            case "전화취소": payAttr = "CNL"; break;
            default: payAttr = "PAY";
        }


        try {
            // ■ Json 전문 생성
            Receipt jsonReceipt = new Receipt();
            Pay jsonPay = new Pay();
            PayInfo jsonPayInfo = new PayInfo();
            Mert jsonMert = new Mert();
            MertInfo jsonMertInfo = new MertInfo();

            jsonPay.setPayType1(payType1);  // CARD, CASH
            jsonPay.setPayAttr(payAttr);    // 결제(PAY), 결제취소(CNL)


            // ■ Pay (신용카드) Data Setter
            if (payType1.equals("CARD")) {
                jsonPayInfo.setCardType(getOrDefault(jsonReceiptEntity.getIssCd()));
                jsonPayInfo.setCardNo(getOrDefault(jsonReceiptEntity.getCardNo()));
                jsonPayInfo.setAuthNo(getOrDefault(jsonReceiptEntity.getAuNo()));
                jsonPayInfo.setPayAmt(getOrDefault(String.valueOf(jsonReceiptEntity.getTrdAmtTot())));
                jsonPayInfo.setTrdType(getOrDefault(jsonReceiptEntity.getTrdType()));
                jsonPayInfo.setInsMon(getOrDefault(jsonReceiptEntity.getInsMon()));
                jsonPayInfo.setTaxAmt(getOrDefault(String.valueOf(jsonReceiptEntity.getTaxAmt())));
                jsonPayInfo.setTrxDate(getOrDefault(trxDate));
                jsonPayInfo.setBuyCd(getOrDefault(jsonReceiptEntity.getBuyCd()));
            }


            // ■ Pay (현금) Data Setter
            if (payType1.equals("CASH")) {
                jsonPayInfo.setCashAmt(getOrDefault(String.valueOf(jsonReceiptEntity.getTrdAmtTot())));
            }


            jsonPay.setPayInfo(Arrays.asList(jsonPayInfo));


            // ■ Mert Data Setter
            jsonMert.setMertNm(storeEntity.getStoreName());
            jsonMert.setMertAddr1(storeEntity.getAddr1());
            jsonMert.setMertAddr2(storeEntity.getAddr2());
            jsonMert.setMertCeoNm(storeEntity.getCeoName());
            jsonMert.setMertBizNo(storeEntity.getBusinessNo());
            jsonMert.setMertPhoneNo(storeEntity.getMobile());


            // ■ Json 전문 생성
            jsonReceipt.setPdct(Arrays.asList(new Pdct()));  // 빈 리스트 추가
            jsonReceipt.setHeader(Arrays.asList(new Header())); // 빈 리스트 추가
            jsonReceipt.setEtc(Arrays.asList(new Etc())); // 빈 리스트 추가
            jsonReceipt.setPay(Arrays.asList(jsonPay));
            jsonReceipt.setMert(Arrays.asList(jsonMert));


            // JSON 출력
            String jsonOutput = objectMapper.writeValueAsString(jsonReceipt);

            System.out.println("@@@@@@ jsonOutput = " + jsonOutput);

            return jsonOutput;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 변환 오류", e);
        }

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Receipt {
        private List<Pdct> Pdct;
        private List<Header> Header;
        private List<Etc> Etc;
        private List<Pay> Pay;
        private List<Mert> Mert;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Pdct {
        private String PcptNo;
        private String TaxAmt;
        private String Vat;
        private String TotAmt;
        private String PurcTm;
        private List<PdctItem> PdctList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class PdctItem {
        private String PdctNm;
        private String PdctNo;
        private String PdctUntPrt;
        private String PdctCnt;
        private String PdctTotAmt;
        private String PdctDcAmt;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Header {
        private String TokenKey;
        private String MertId;
        private String PosNo;
        private String TrxId;
        private String CntryCd;
        private String CharSet;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Etc {
        private String EtcType;
        private List<EtcInfo> EtcInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class EtcInfo {
        private String Data;
    }

    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Pay {
        private String PayType1;
        private String PayType2;
        private String PayAttr;
        private List<PayInfo> PayInfo;
    }

    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class PayInfo {
        private String CardType;
        private String CardNo;
        private String AuthNo;
        private String PayAmt;
        private String CashAmt;
        private String PontAmt;
        private String Balance;
        private String CpnAmt;
        private String BarcdAmt;
        private String SpayAmt;

        private String TrdType; // 승인, 취소
        private String InsMon;  // 할부 개월
        private String TaxAmt;  // 부가세
        private String TrxDate; // 거래 일시
        private String BuyCd;   // 매입사

    }

    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Mert {
        private String MertNm;
        private String MertAddr1;
        private String MertAddr2;
        private String MertCeoNm;
        private String MertBizNo;
        private String MertPhoneNo;
        private List<MertInfo> MertInfo;
    }

    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class MertInfo {
        private String Title;
        private String Value;
    }

    public String getOrDefault(String value) {
        return (value == null || value.isEmpty()) ? "0" : value;
    }
}