package io.allink.tcp.koces.receipt.model;

import io.allink.tcp.koces.receipt.protocol.pReceipt;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "koces_receipt")
@Getter
@Setter
@NoArgsConstructor
public class ReceiptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq; // 기본 키 (자동 증가)

    @Column(name = "msg_length", length = 4)
    private String msgLength;

    @Column(name = "answer_cd", length = 4)
    private String answerCd;

    @Column(name = "svc_type", length = 2)
    private String svcType;

    @Column(name = "trd_type", length = 2)
    private String trdType;

    @Column(name = "trd_uni_key", length = 20)
    private String trdUniKey;

    @Column(name = "term_id", length = 10)
    private String termId;

    @Column(name = "business_no", length = 10)
    private String businessNo;

    @Column(name = "card_no", length = 70)
    private String cardNo;

    @Column(name = "pay_gubun", length = 3)
    private String payGubun;

    @Column(name = "ins_mon", length = 2)
    private String insMon;

    @Column(name = "trd_amt_tot")
    private Integer trdAmtTot;

    @Column(name = "svc_amt")
    private Integer svcAmt;

    @Column(name = "tax_amt")
    private Integer taxAmt;

    @Column(name = "amt1")
    private Integer amt1;

    @Column(name = "amt2")
    private Integer amt2;

    @Column(name = "au_no", length = 40)
    private String auNo;

    @Column(name = "trans_date", length = 8)
    private String transDate;

    @Column(name = "trans_time", length = 6)
    private String transTime;

    @Column(name = "au_date", length = 8)
    private String auDate;

    @Column(name = "ori_trd_uni_key", length = 20)
    private String oriTrdUniKey;

    @Column(name = "ori_au_no", length = 40)
    private String oriAuNo;

    @Column(name = "cancel_cd", length = 1)
    private String cancelCd;

    @Column(name = "iss_cd", length = 4)
    private String issCd;

    @Column(name = "buy_cd", length = 4)
    private String buyCd;

    @Column(name = "ddc_yn", length = 1)
    private String ddcYn;

    @Column(name = "swipe", length = 1)
    private String swipe;

    @Column(name = "mch_no", length = 16)
    private String mchNo;

    @Column(name = "mch_data", length = 100)
    private String mchData;

    @Column(name = "check_yn", length = 1)
    private String checkYn;

    @Column(name = "foreign_yn", length = 1)
    private String foreignYn;

    @Column(name = "dsc_yn", length = 1)
    private String dscYn;

    @Column(name = "van_code", length = 2)
    private String vanCode;

    @Column(name = "filler", length = 159)
    private String filler;

    @Column(name = "reg_date", nullable = false, updatable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime regDate = LocalDateTime.now();

    @Column(name = "mod_date")
    private LocalDateTime modDate;

    public void setReceiptEntity(pReceipt receipt) {
        this.msgLength = receipt.getMsgLength().trim();
        this.answerCd = receipt.getAnswerCd().trim();
        this.svcType = receipt.getSvcType().trim();
        this.trdType = receipt.getTrdType().trim();
        this.trdUniKey = receipt.getTrdUniKey().trim();
        this.termId = receipt.getTermId().trim();
        this.businessNo = receipt.getBusinessNo().trim();
        this.cardNo = receipt.getCardNo().trim();
        this.payGubun = receipt.getPayGubun().trim();
        this.insMon = receipt.getInsMon().trim();
        this.trdAmtTot = Integer.valueOf(receipt.getTrdAmtTot().trim());
        this.svcAmt = Integer.valueOf(receipt.getSvcAmt().trim());
        this.taxAmt = Integer.valueOf(receipt.getTaxAmt().trim());
        this.amt1 = receipt.getAmt1().trim().isEmpty() ? 0 : Integer.valueOf(receipt.getAmt1().trim());
        this.amt2 = receipt.getAmt2().trim().isEmpty() ? 0 : Integer.valueOf(receipt.getAmt2().trim());
        this.auNo = receipt.getAuNo().trim();
        this.transDate = receipt.getTransDate().trim();
        this.transTime = receipt.getTransTime().trim();
        this.auDate = receipt.getAuDate().trim();
        this.oriTrdUniKey = receipt.getOriTrdUniKey().trim();
        this.oriAuNo = receipt.getOriAuNo().trim();
        this.cancelCd = receipt.getCancelCd().trim();
        this.issCd = receipt.getIssCd().trim();
        this.buyCd = receipt.getBuyCd().trim();
        this.ddcYn = receipt.getDdcYn().trim();
        this.swipe = receipt.getSwipe().trim();
        this.mchNo = receipt.getMchNo().trim();
        this.mchData = receipt.getMchData().trim();
        this.checkYn = receipt.getCheckYn().trim();
        this.foreignYn = receipt.getForeignYn().trim();
        this.dscYn = receipt.getDscYn().trim();
        this.vanCode = receipt.getVanCode().trim();
        this.filler = receipt.getFiller().trim();
    }
}
