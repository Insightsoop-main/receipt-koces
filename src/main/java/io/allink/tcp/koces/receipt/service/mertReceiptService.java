package io.allink.tcp.koces.receipt.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class mertReceiptService {
    
    @Autowired
    private EntityManager entityManager;

    public mertReceiptService(){};

    /**
     * JPA Entity 로 할 경우 Json 관련 Type 으로 인해 오류
     * Type 무시 하고 직접 Insert 하는 방식으로 변경.
     * @param merchantId
     * @param payload
     * @param posNo
     * @param trxId
     */

    @Transactional
    public void insertWithJson(String merchantId, String payload, String posNo, String trxId){

        log.info("@@@@@ MerchantId: {}",merchantId);
        log.info("@@@@@ trxId: {}",trxId);
        log.info("@@@@@ payload: {}",payload);
        log.info("@@@@@ PosNo: {}",posNo);

        String sql = "INSERT INTO merchant_receipt(id, created_at, merchant_id, payload, pos_no, trx_id) VALUES (uuid_generate_v4(), now(), ?, ?::json, ?, ?)";

        Query query = entityManager.createNativeQuery(sql);

        query.setParameter(1, merchantId);
        query.setParameter(2, payload);
        query.setParameter(3, posNo);
        query.setParameter(4, trxId);
        query.executeUpdate();
    }
}
