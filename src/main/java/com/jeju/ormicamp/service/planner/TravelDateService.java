package com.jeju.ormicamp.service.planner;

import com.jeju.ormicamp.common.exception.ErrorCode;
import com.jeju.ormicamp.infrastructure.repository.planner.TravelDateRepository;
import com.jeju.ormicamp.model.domain.TravelDate;
import com.jeju.ormicamp.model.dto.planner.TravelDateReqDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TravelDateService {

    private final TravelDateRepository travelDateRepository;


    public Long saveDate(TravelDateReqDto dto) {

        // TODO : user 예외처리 확인

        // TODO : endDate > startDate 검증
        // TODO : 메서드화
        TravelDate travelDate = TravelDate.builder()
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .build();
        TravelDate result = travelDateRepository.save(travelDate);

        return result.getId();
    }

    public void updateDate(Long travelDateId, TravelDateReqDto dto) {

        // TODO : 에러 가시화
        TravelDate update = travelDateRepository.findById(travelDateId)
                .orElseThrow(() -> new IllegalStateException(ErrorCode.UNKNOWN_ERROR.getMessage()));

        update.updateDate(dto.getStartDate(), dto.getEndDate());

        // TODO : 변경된 TravleDate 값 반환

    }
}
