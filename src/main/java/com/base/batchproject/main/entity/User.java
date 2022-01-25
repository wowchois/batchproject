package com.base.batchproject.main.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Enumerated(EnumType.STRING)
    private Level grade = Level.NORMAL; //등급

    private int totalamt; //총 금액

    private LocalDate updatedate;

    @Builder
    private User(String username,int totalamt){
        this.username = username;
        this.totalamt = totalamt;
    }

    public enum Level{
        VIP(500_000,null),COLD(500_000,VIP),SILVER(300_000,COLD),NORMAL(200_000,SILVER);

        private final int nextAmt;
        private final Level nextLevel;

        Level(int nextAmt, Level nextLevel){
            this.nextAmt = nextAmt;
            this.nextLevel = nextLevel;
        }

    }
}
