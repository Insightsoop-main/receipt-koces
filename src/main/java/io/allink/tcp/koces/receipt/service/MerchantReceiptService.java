package io.allink.tcp.koces.receipt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MerchantReceiptService {

  @Autowired
  private EntityManager entityManager;

  public MerchantReceiptService() {
  }

  /**
   * JPA Entity 로 할 경우 Json 관련 Type 으로 인해 오류 Type 무시 하고 직접 Insert 하는 방식으로 변경.
   *
   * @param merchantId
   * @param payload
   * @param deviceId
   * @param trxId
   */
  @Transactional
  public void insertWithJson(String merchantId, String payload, String deviceId, String trxId) {

    log.info("@@@@@ MerchantId: {}", merchantId);
    log.info("@@@@@ trxId: {}", trxId);
    log.info("@@@@@ payload: {}", payload);
    log.info("@@@@@ deviceId: {}", deviceId);

    String sql = "INSERT INTO merchant_receipt(receipt_uuid, reg_date, merchant_id, payload, device_id, trx_id) VALUES (uuid_generate_v4(), now(), ?, ?::json, ?, ?)";

    Query query = entityManager.createNativeQuery(sql);

    query.setParameter(1, merchantId);
    query.setParameter(2, payload);
    query.setParameter(3, deviceId);
    query.setParameter(4, "koces-" + trxId);
    query.executeUpdate();
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public boolean isExists(String trxId) {
    String sql = "select 1 from merchant_receipt where trx_id = ?";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter(1, "koces-" + trxId);
    try {
      query.getSingleResult();
    } catch (NoResultException e) {
      return false;
    }
    log.info("중복 전문 수신 trxId: {}", trxId);
    return true;
  }

  @org.springframework.transaction.annotation.Transactional(readOnly = true)
  public boolean isNotExistsMerchantTag(String merchantId, String deviceId) {
    String sql = "select 1 from merchant_tag mt where mt.device_id = ? and mt.merchant_id = ?";
    Query query = entityManager.createNativeQuery(sql);
    query.setParameter(1, deviceId);
    query.setParameter(2, merchantId);
    try {
      query.getSingleResult();
    } catch (NoResultException e) {
      log.info("등록되지 않은 요청 merchantId: {}, deviceId = {}", merchantId, deviceId);
      return true;
    }
    return false;
  }
}
