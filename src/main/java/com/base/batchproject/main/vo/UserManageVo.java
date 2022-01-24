package com.base.batchproject.main.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserManageVo {

    private Long id;
    private String username;
    private int grade; //등급
    private int totalAmt; //총 금액
    private String updateDate;
}
