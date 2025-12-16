package com.jeju.ormicamp.model.dto.dynamodb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // 기본 생성자 (JSON 파싱할 때 필요)
public class ChatReqDto {

    private String content; // "제주도 맛집 알려줘"

}