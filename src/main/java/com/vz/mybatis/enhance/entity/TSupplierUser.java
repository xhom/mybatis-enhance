package com.vz.mybatis.enhance.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/24 14:43
 */
@Data
public class TSupplierUser {
    private Long id;

    private String userName;

    private String phone;

    private Integer status;

    private Long enterpriseId;

    private Date createDt;

    private String password;
}
