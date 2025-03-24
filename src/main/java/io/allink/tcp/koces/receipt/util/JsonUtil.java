package io.allink.tcp.koces.receipt.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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

        String payType;
        String payAttr;

        String trxDate = jsonReceiptEntity.getTransDate() + jsonReceiptEntity.getTransTime();
        trxDate = DateUtil.getYMD(trxDate);

        // ■ 결제 유형
        switch (jsonReceiptEntity.getSvcType()) {
            case "CC": payType = "CARD"; break;
            case "CI": payType = "CASH";break;
            case "PA": payType = "SPAY"; break;
            default: payType = "CARD";
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
            PayInfos jsonPayInfos = new PayInfos();
            Mert jsonMert = new Mert();


            jsonPayInfos.setPayType(payType);  // CARD, CASH
            jsonPayInfos.setPayAttr(payAttr);    // 결제(PAY), 결제취소(CNL)


            // ■ Pay (신용카드) Data Setter
            if (payType.equals("CARD")) {
                jsonPayInfos.setCardType(getOrDefault(jsonReceiptEntity.getIssCd()));
                jsonPayInfos.setCardNo(getOrDefault(jsonReceiptEntity.getCardNo()));
                jsonPayInfos.setAuthNo(getOrDefault(jsonReceiptEntity.getAuNo()));
                jsonPayInfos.setPayAmt(getOrDefault(String.valueOf(jsonReceiptEntity.getTrdAmtTot())));
                jsonPayInfos.setTrdType(getOrDefault(jsonReceiptEntity.getTrdType()));
                jsonPayInfos.setInsMon(getOrDefault(jsonReceiptEntity.getInsMon()));
                jsonPayInfos.setTaxAmt(getOrDefault(String.valueOf(jsonReceiptEntity.getTaxAmt())));
                jsonPayInfos.setTrxDate(getOrDefault(trxDate));
                jsonPayInfos.setBuyCd(getOrDefault(jsonReceiptEntity.getBuyCd()));
            }


            // ■ Pay (현금) Data Setter
            if (payType.equals("CASH")) {
                jsonPayInfos.setCashAmt(getOrDefault(String.valueOf(jsonReceiptEntity.getTrdAmtTot())));
            }


            jsonPay.setPayInfos(Arrays.asList(jsonPayInfos));


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
//            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
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
        @JsonProperty("Pdct")
        private List<Pdct> Pdct;

        @JsonProperty("Header")
        private List<Header> Header;

        @JsonProperty("Etc")
        private List<Etc> Etc;

        @JsonProperty("Pay")
        private List<Pay> Pay;

        @JsonProperty("Mert")
        private List<Mert> Mert;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Pdct {
        @JsonProperty("PcptNo")
        private String PcptNo;

        @JsonProperty("TaxAmt")
        private String TaxAmt;

        @JsonProperty("Vat")
        private String Vat;

        @JsonProperty("TotAmt")
        private String TotAmt;

        @JsonProperty("PurcTm")
        private String PurcTm;

        @JsonProperty("PdctList")
        private List<PdctItem> PdctList;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class PdctItem {
        @JsonProperty("PdctNm")
        private String PdctNm;

        @JsonProperty("PdctNo")
        private String PdctNo;

        @JsonProperty("PdctUntPrt")
        private String PdctUntPrt;

        @JsonProperty("PdctCnt")
        private String PdctCnt;

        @JsonProperty("PdctTotAmt")
        private String PdctTotAmt;

        @JsonProperty("PdctDcAmt")
        private String PdctDcAmt;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Header {
        @JsonProperty("TokenKey")
        private String TokenKey;

        @JsonProperty("MertId")
        private String MertId;

        @JsonProperty("PosNo")
        private String PosNo;

        @JsonProperty("TrxId")
        private String TrxId;

        @JsonProperty("CntryCd")
        private String CntryCd;

        @JsonProperty("CharSet")
        private String CharSet;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Etc {
        @JsonProperty("EtcType")
        private String EtcType;

        @JsonProperty("EtcInfo")
        private List<EtcInfo> EtcInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class EtcInfo {
        @JsonProperty("Data")
        private String Data;
    }


    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Pay {
        @JsonProperty("PayInfos")
        private List<PayInfos> PayInfos;
    }


    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class PayInfos {
        @JsonProperty("PayType")
        private String PayType;

        @JsonProperty("PayAttr")
        private String PayAttr;

        @JsonProperty("CardType")
        private String CardType;

        @JsonProperty("CardNo")
        private String CardNo;

        @JsonProperty("AuthNo")
        private String AuthNo;

        @JsonProperty("PayAmt")
        private String PayAmt;

        @JsonProperty("CashAmt")
        private String CashAmt;

        @JsonProperty("PontAmt")
        private String PontAmt;

        @JsonProperty("Balance")
        private String Balance;

        @JsonProperty("CpnAmt")
        private String CpnAmt;

        @JsonProperty("BarcdAmt")
        private String BarcdAmt;

        @JsonProperty("SpayAmt")
        private String SpayAmt;

        @JsonProperty("TrdType")
        private String TrdType; // 승인, 취소

        @JsonProperty("InsMon")
        private String InsMon;  // 할부 개월

        @JsonProperty("TaxAmt")
        private String TaxAmt;  // 부가세

        @JsonProperty("TrxDate")
        private String TrxDate; // 거래 일시

        @JsonProperty("BuyCd")
        private String BuyCd;   // 매입사

    }


    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class Mert {
        @JsonProperty("MertNm")
        private String MertNm;

        @JsonProperty("MertAddr1")
        private String MertAddr1;

        @JsonProperty("MertAddr2")
        private String MertAddr2;

        @JsonProperty("MertCeoNm")
        private String MertCeoNm;

        @JsonProperty("MertBizNo")
        private String MertBizNo;

        @JsonProperty("MertPhoneNo")
        private String MertPhoneNo;

        @JsonProperty("MertInfo")
        private List<MertInfo> MertInfo;
    }

    @Data
    @Setter
    @Getter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public class MertInfo {
        @JsonProperty("Title")
        private String Title;

        @JsonProperty("Value")
        private String Value;
    }

    public String getOrDefault(String value) {
        return (value == null || value.isEmpty()) ? "0" : value;
    }
}