package com.example.vote.service;

import com.example.vote.dto.MeetDTO;
import com.example.vote.entity.Meet;
import com.example.vote.entity.Options;
import com.example.vote.entity.Vote;
import com.example.vote.repository.MeetRepository;
import com.example.vote.repository.OptionRepository;
import com.example.vote.repository.VoteRepository;
import com.example.vote.repository.VoteResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MeetService {

    @Autowired
    private MeetRepository meetRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private VoteResultRepository voteResultRepository;

    public MeetDTO createMeet(MeetDTO meetDTO) {
        Meet meet = Meet.builder()
                .meetName(meetDTO.getMeetName())
                .createdPassword(meetDTO.getCreatedPassword())
                .meetPassword(meetDTO.getMeetPassword())
                .endDateTime(meetDTO.getEndDateTime())
                .build();
        meetRepository.save(meet);
        meetDTO.setId(meet.getId());
        meetDTO.setCreatedDate(meet.getCreatedDate());
        meetDTO.setUpdatedDate(meet.getUpdatedDate());
        return meetDTO;
    }

    private MeetDTO toDto(Meet meet) {
        MeetDTO meetDTO = new MeetDTO();
        meetDTO.setId(meet.getId());
        meetDTO.setMeetName(meet.getMeetName());
        meetDTO.setCreatedPassword(meet.getCreatedPassword());
        meetDTO.setMeetPassword(meet.getMeetPassword());
        meetDTO.setEndDateTime(meet.getEndDateTime());
        meetDTO.setCreatedDate(meet.getCreatedDate());
        meetDTO.setUpdatedDate(meet.getUpdatedDate());
        return meetDTO;
    }

    public List<MeetDTO> getAllMeets() {
        return meetRepository.findAllByOrderByCreatedDateDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Page<MeetDTO> getPagedMeets(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = size <= 0 ? 10 : size;
        Pageable pageable = PageRequest.of(safePage, safeSize);
        return meetRepository.findAllByOrderByCreatedDateDesc(pageable).map(this::toDto);
    }

    public Optional<MeetDTO> getMeetById(Long id) {
        return meetRepository.findById(id).map(this::toDto);
    }

    public void updateMeet(Long id, MeetDTO meetDTO) {
        meetRepository.findById(id).ifPresent(meet -> {
            meet.setMeetName(meetDTO.getMeetName());
            meet.setCreatedPassword(meetDTO.getCreatedPassword());
            meet.setMeetPassword(meetDTO.getMeetPassword());
            meet.setEndDateTime(meetDTO.getEndDateTime());
            meetRepository.save(meet);
        });
    }

    @Transactional
    public void deleteMeet(Long id) {
        List<Vote> votes = voteRepository.findByMeetId(id);

        for (Vote vote : votes) {
            voteResultRepository.deleteByVoteId(vote.getId());

            List<Options> options = optionRepository.findByVoteId(vote.getId());
            for (Options option : options) {
                optionRepository.delete(option);
            }

            voteRepository.delete(vote);
        }

        meetRepository.deleteById(id);
    }

    @Transactional
    public void deleteExpiredMeets() {
        LocalDateTime now = LocalDateTime.now();
        List<Meet> expiredMeets = meetRepository.findByEndDateTimeBefore(now);
        for (Meet meet : expiredMeets) {
            deleteMeet(meet.getId());
        }
    }
}
