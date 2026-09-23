package io.allink.tcp.koces.receipt.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.allink.tcp.koces.receipt.model.Store;
import io.allink.tcp.koces.receipt.protocol.KocesMessage;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class JsonUtil {
  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String toJson(Store store, KocesMessage message) {

    try {
      // ■ Json 전문 생성
      Receipt receipt = new Receipt();
      Mert merchant = new Mert();
      // 미등록 가맹점도 받아 쌓으므로 store가 null일 수 있다.
      // 가맹점 정보 없이 거래 데이터만 저장한다 — 사후 등록 후 채우면 된다.
      if (store != null) {
        merchant.setMertNm(store.getStoreName());
        merchant.setMertAddr1(store.getAddr1());
        merchant.setMertAddr2(store.getAddr2());
        merchant.setMertCeoNm(store.getCeoName());
        merchant.setMertBizNo(store.getBusinessNo());
        merchant.setMertPhoneNo(store.getMobile());
      }
      merchant.setMertData(message.getMchData());

      receipt.setPayInfos(List.of(message));
      receipt.setMert(merchant);

      return objectMapper.writeValueAsString(receipt);

    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 변환 오류", e);
    }

  }

  @Data
  @JsonInclude(JsonInclude.Include.ALWAYS)
  public static class Receipt {

    @JsonProperty("PayInfos")
    private List<KocesMessage> payInfos;

    @JsonProperty("Mert")
    private Mert mert;
  }


  @Data
  @Setter
  @Getter
  @JsonInclude(JsonInclude.Include.ALWAYS)
  public static class Mert {
    @JsonProperty("MertNm")
    private String mertNm;

    @JsonProperty("MertAddr1")
    private String mertAddr1;

    @JsonProperty("MertAddr2")
    private String mertAddr2;

    @JsonProperty("MertCeoNm")
    private String mertCeoNm;

    @JsonProperty("MertBizNo")
    private String mertBizNo;

    @JsonProperty("MertPhoneNo")
    private String mertPhoneNo;

    @JsonProperty("MertData")
    private String mertData;

  }

  public String getOrDefault(String value) {
    return (value == null || value.isEmpty()) ? "0" : value;
  }
}