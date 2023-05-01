package hello.jdbc_cozil.domain;

import lombok.Data;

@Data
public class Member {
    private String memberId;
    private int money;

    //기본 생성자
    public Member(){

    }
    //파라미터를 받는 생성자
    public Member(String memberId,int money){
        this.memberId = memberId;
        this.money = money;
    }
}
