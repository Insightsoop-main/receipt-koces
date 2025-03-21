package io.allink.tcp.koces.receipt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "koces_code_type")
public class CodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "cd_grp", nullable = false)
    private String cdGrp;

    @Column(name = "cd_type")
    private String cdType;

    @Column(name = "cd_val", nullable = false)
    private String cdVal;

    @Column(name = "cd_nm")
    private String cdNm;

    @Column(name = "cd_desc")
    private String cdDesc;

    @Column(name = "sort", nullable = false)
    private int sort;

    @Column(name = "stat_cd", nullable = false)
    private String statCd;

    @Column(name = "reg_tm", nullable = true)
    private java.sql.Timestamp regTm;
}
