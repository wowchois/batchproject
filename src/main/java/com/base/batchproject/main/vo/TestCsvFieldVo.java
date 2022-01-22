package com.base.batchproject.main.vo;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TestCsvFieldVo {

    private int id;
    private String name;
    private String address;

    public TestCsvFieldVo(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
}
