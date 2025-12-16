package com.jeju.ormicamp.service.dynamodb;

import com.jeju.ormicamp.common.exception.CustomException;
import com.jeju.ormicamp.common.exception.ErrorCode;
import com.jeju.ormicamp.infrastructure.repository.dynamoDB.ChatDynamoRepository;
import com.jeju.ormicamp.infrastructure.repository.planner.TravelInfoRepository;
import com.jeju.ormicamp.model.code.ChatRole;
import com.jeju.ormicamp.model.code.ChatType;
import com.jeju.ormicamp.model.code.TravelInfoSnapshot;
import com.jeju.ormicamp.model.domain.ChatEntity;
import com.jeju.ormicamp.model.domain.TravelInfo;
import com.jeju.ormicamp.model.dto.dynamodb.ChatReqDto;
import com.jeju.ormicamp.model.dto.dynamodb.ChatResDto;
import com.jeju.ormicamp.service.Bedrock.BedRockAgentService;
import com.jeju.ormicamp.service.Bedrock.MakeJsonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.time.LocalTime.now;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final TravelInfoRepository travelInfoRepository;
    private final ChatDynamoRepository chatRepository;
    private final BedRockAgentService agentService;
    private final MakeJsonService makeJsonService;

    /**
     * 처음 채팅을
     *
     * @param req 채팅 입력 시 정보를 받아옵니다.
     * @return agent를 거쳐 생성된 Response 값을 반환 합니다.
     */
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
                .chatTitle(req.getChatTitle())
                .travelInfo(TravelInfoSnapshot.toSnapshot(travelInfo))
                .build();

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

        // 5️⃣ Agent 호출
        String agentResponse =
                agentService.sendDataToAgent(conversationId, payload).join();

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

    public ChatResDto sendMessage(ChatReqDto req, Long userId) {

        String conversationId = req.getConversationId();

        ChatEntity meta = chatRepository.findMeta(conversationId);

        ChatEntity chat = ChatEntity.builder()
                .conversationId(conversationId)
                .type(ChatType.CHAT)
                .role(ChatRole.USER)
                .prompt(req.getContent())
                .timestamp(now().toString())
                .build();

        chatRepository.save(chat);

        String payload = makeJsonService.createJsonPayload(
                conversationId,
                req.getContent(),
                chat.getTravelInfo()
        );

        String agentResponse =
                agentService.sendDataToAgent(conversationId, payload).join();

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
}