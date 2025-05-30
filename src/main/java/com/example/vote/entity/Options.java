package com.example.vote.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter //클래스의 포함된 멤버 변수의 모든 getter 매서드를 생성
@Setter
@Builder // sql에 값 넣는것
@ToString // 객체의 값 확인
@AllArgsConstructor //생성자 자동 완성
@NoArgsConstructor //생성자 자동 완성
@EntityListeners(AuditingEntityListener.class)
@Entity(name="options")// class에 지정할 테이블명
public class Options {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK를 생성 전략 설정 GenerationType.SEQUENCE
    @Column(name = "option_id")
    private Long id;

    @Column(name = "option_number")
    private Long optionNumber;

    @ManyToOne
    @JoinColumn(name = "vote_id")
//    @Column(name = "vote_id")
    private Vote vote;

    @Column(name = "option_text")
    private String optionText;

    @OneToMany(mappedBy = "optionNumber")
    private List<VoteResult> voteResults;

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}
