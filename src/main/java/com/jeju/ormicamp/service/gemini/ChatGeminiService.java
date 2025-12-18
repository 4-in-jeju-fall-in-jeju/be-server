package com.jeju.ormicamp.service.gemini;

import com.jeju.ormicamp.common.exception.CustomException;
import com.jeju.ormicamp.common.exception.ErrorCode;
import com.jeju.ormicamp.infrastructure.repository.dynamoDB.ChatDynamoRepository;
import com.jeju.ormicamp.infrastructure.repository.planner.TravelInfoRepository;
import com.jeju.ormicamp.model.code.ChatRole;
import com.jeju.ormicamp.model.code.ChatType;
import com.jeju.ormicamp.model.code.TravelInfoSnapshot;
import com.jeju.ormicamp.model.domain.ChatEntity;
import com.jeju.ormicamp.model.domain.TravelInfo;
import com.jeju.ormicamp.model.dto.dynamodb.ChatConversationResDto;
import com.jeju.ormicamp.model.dto.dynamodb.ChatMessageResDto;
import com.jeju.ormicamp.model.dto.dynamodb.ChatReqDto;
import com.jeju.ormicamp.model.dto.dynamodb.ChatResDto;
import com.jeju.ormicamp.service.Bedrock.MakeJsonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static java.time.LocalTime.now;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatGeminiService {

    private final TravelInfoRepository travelInfoRepository;
    private final ChatDynamoRepository chatRepository;
    private final MakeJsonService makeJsonService;
    private final AiService aiService;

    public ChatResDto startChat(ChatReqDto req, Long userId) {

        String conversationId = UUID.randomUUID().toString();

        // 지금 이건 애초에 meta에 저장하기 위해서 한번 필요함\
        // 왜냐 userid기준으로 조회한 값이기 때문
        TravelInfo travelInfo = travelInfoRepository
                .findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        // 대화방 메타 데이터 초기 데이터 조회용
        ChatEntity meta = ChatEntity.builder()
                .conversationId(conversationId)
                .type(ChatType.PLAN_META)
                .chatTitle(req.getContent())
                .travelInfo(TravelInfoSnapshot.toSnapshot(travelInfo))
                .build();

        TravelInfoSnapshot response = TravelInfoSnapshot.toSnapshot(travelInfo);

        chatRepository.save(meta);

        chatRepository.save(
                ChatEntity.builder()
                        .conversationId(conversationId)
                        .type(ChatType.CHAT)
                        .role(ChatRole.USER)
                        .prompt(req.getContent())
                        .timestamp(now().toString())
                        .build()
        );

        String payload = makeJsonService.createJsonPayload(
                conversationId,
                req.getContent(),
                meta.getTravelInfo()
        );

        String agentResponse = aiService.invoke(conversationId, payload);

        chatRepository.save(
                ChatEntity.builder()
                        .conversationId(conversationId)
                        .type(ChatType.CHAT)
                        .role(ChatRole.AI)
                        .prompt(agentResponse)
                        .timestamp(now().toString())
                        .build()
        );

        return ChatResDto.builder()
                .conversationId(conversationId)
                .message(agentResponse)
                .build();
    }

    public ChatResDto sendMessage(String conversationId,String content, Long userId) {

        ChatEntity meta = chatRepository.findMeta(conversationId);

        chatRepository.save(
                ChatEntity.builder()
                        .conversationId(conversationId)
                        .type(ChatType.CHAT)
                        .role(ChatRole.USER)
                        .prompt(content)
                        .timestamp(now().toString())
                        .build()
        );

        String payload = makeJsonService.createJsonPayload(
                conversationId,
                content,
                meta.getTravelInfo()
        );

        // TODO : agent 연결 시 주석 해제
        // String agentResponse = agentService.sendDataToAgent(conversationId, payload).join();

        // 테스트용 더미
        String agentResponse = """
                [TEST MODE]
                제주 여행 일정 예시입니다.
                - 1일차: 성산일출봉
                - 2일차: 한라산
                - 3일차: 카페 투어
                """;

        chatRepository.save(
                ChatEntity.builder()
                        .conversationId(conversationId)
                        .type(ChatType.CHAT)
                        .role(ChatRole.AI)
                        .prompt(agentResponse)
                        .timestamp(now().toString())
                        .build()
        );

        return ChatResDto.builder()
                .conversationId(conversationId)
                .message(agentResponse)
                .build();
    }

    // 채팅방 정보 반환
    public ChatConversationResDto getConversation(String conversationId) {

        List<ChatEntity> items =
                chatRepository.findByConversationId(conversationId);

        if (items.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND);
        }

        ChatEntity meta = items.stream()
                .filter(i -> i.getType() == ChatType.PLAN_META)
                .findFirst()
                .orElseThrow();

        List<ChatMessageResDto> messages = items.stream()
                .filter(i -> i.getType() == ChatType.CHAT)
                .sorted(Comparator.comparing(ChatEntity::getTimestamp))
                .map(ChatMessageResDto::from)
                .toList();

        return ChatConversationResDto.builder()
                .conversationId(conversationId)
                .chatTitle(meta.getChatTitle())
                .travelInfo(meta.getTravelInfo())
                .messages(messages)
                .build();
    }

}