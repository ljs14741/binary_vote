package com.example.vote.repository;

import com.example.vote.entity.Vote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    @Query("SELECT v FROM vote v ORDER BY v.createdDate DESC")
    List<Vote> findAllOrderByCreatedDateDesc();

    // 공개 투표만 조회
    @Query("SELECT v FROM vote v WHERE v.voteType = 'PUBLIC' ORDER BY v.createdDate DESC")
    List<Vote> findAllPublicVotesOrderByCreatedDateDesc();

    @Query("SELECT v FROM vote v WHERE v.voteType = 'Private' ORDER BY v.createdDate DESC")
    List<Vote> findAllPrivateVotesOrderByCreatedDateDesc();

    // 특정 모임 ID와 비공개 투표를 필터링하여 조회
    @Query("SELECT v FROM vote v WHERE v.voteType = 'PRIVATE' AND v.meet.id = :meetId ORDER BY v.createdDate DESC")
    List<Vote> findPrivateVotesByMeetId(@Param("meetId") Long meetId);

    // ✅ [추가] 공개 투표 목록 (페이징 + 정렬)
    // 인기순 정렬을 위해 LEFT JOIN 후 GROUP BY하여 카운트
    @Query(value = "SELECT v FROM vote v " +
            "LEFT JOIN vote_result r ON v.id = r.vote.id " +
            "WHERE v.voteType = :voteType " +
            "GROUP BY v.id " +
            "ORDER BY COUNT(r.id) DESC, v.createdDate DESC",
            countQuery = "SELECT COUNT(v) FROM vote v WHERE v.voteType = :voteType")
    Page<Vote> findByVoteTypeOrderByPopularity(@Param("voteType") Vote.VoteType voteType, Pageable pageable);

    List<Vote> findByMeetId(Long meetId);

    Page<Vote> findByVoteType(Vote.VoteType voteType, Pageable pageable);

}
