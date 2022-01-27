package com.base.batchproject.main.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

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

    //upgrade 할 대상여부 출력
    public boolean targetUpgrade() {
        return Level.targetUpgrade(this.getGrade(), this.getTotalamt());
    }

    //등급 상향
    public Level upGrade(){
        Level nextGrade = Level.getNextGrade(this.getTotalamt());

        this.grade = nextGrade;
        this.updatedate = LocalDate.now();

        return nextGrade;
    }

    public enum Level{
        VIP(500_000,null),GOLD(500_000,VIP),SILVER(300_000,GOLD),NORMAL(200_000,SILVER);

        private final int nextAmt;
        private final Level nextGrade;

        Level(int nextAmt, Level nextGrade){
            this.nextAmt = nextAmt;
            this.nextGrade = nextGrade;
        }

        //등급 비교
        public static boolean targetUpgrade(Level level, int amt) {
            if(Objects.isNull(level)){ //level이 없는 경우
                return false;
            }
            if(Objects.isNull(level.nextGrade)){ //vip경우
                return false;
            }

            return amt >= level.nextAmt;
        }

        private static Level getNextGrade(int amt) {
            if(amt >= Level.VIP.nextAmt){ //VIP
                return VIP;
            }
            if(amt >= Level.GOLD.nextAmt){ //GOLD->VIP
                return GOLD.nextGrade;
            }
            if(amt >= Level.SILVER.nextAmt){ //SILVER->GOLD
                return SILVER.nextGrade;
            }
            if(amt >= Level.NORMAL.nextAmt){ //NORMAL->SILVER
                return NORMAL.nextGrade;
            }

            return NORMAL;
        }
    }
}
