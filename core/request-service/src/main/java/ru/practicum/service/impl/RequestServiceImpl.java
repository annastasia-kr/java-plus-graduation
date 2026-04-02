package ru.practicum.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.client.EventClient;
import ru.practicum.event.dto.EventDto;
import ru.practicum.event.dto.EventResult;
import ru.practicum.event.enums.StateEvent;
import ru.practicum.exception.DataConflictException;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Request;
import ru.practicum.repository.RequestRepository;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.service.RequestService;
import ru.practicum.user.client.UserClient;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final RequestMapper requestMapper;

    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        findUserById(userId).orElseThrow(
                () -> new NotFoundException("User (id = " + userId + " not found"));

        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) {

        UserDto user = findUserById(userId).orElseThrow(
                () -> new NotFoundException("User (id = " + userId + " not found"));
        EventDto event = findEventById(eventId).orElseThrow(() ->
                new NotFoundException("Event (id = " + eventId + " not found"));

        validateRequestCreation(userId, event);

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Запрос на участие уже существует");
        }

        // Создаем запрос и сразу устанавливаем дату создания
        Request request = new Request();
        request.setEventId(event.getId());
        request.setRequesterId(user.getId());
        request.setStatus(RequestStatus.PENDING);
        request.setCreatedDate(LocalDateTime.now());

        // Проверяем условия для авто-подтверждения
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        Request savedRequest = requestRepository.save(request);
        log.info("Запрос на участие создан с ID: {}", savedRequest.getId());

        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отмена запроса: userId={}, requestId={}", userId, requestId);

        findUserById(userId).orElseThrow(
                () -> new NotFoundException("User (id = " + userId + " not found"));

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на участие с id=" + requestId + " не найден"));

        if (!request.getRequesterId().equals(userId)) {
            throw new ConflictException("Запрос не принадлежит пользователю");
        }

        request.setStatus(RequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        log.info("Запрос на участие с ID {} отменен", requestId);

        return requestMapper.toRequestDto(updatedRequest);
    }

    @Override
    public List<RequestDto> getRequestsByEventId(Long eventId) {

        return requestRepository.findAllByEventId(eventId)
                .stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {

        EventDto event = findEventById(eventId).orElseThrow(() ->
                new NotFoundException("Event (id = " + eventId + " not found"));

        List<Request> requestsToStatusUpdate = requestRepository
                .findAllByIdIn(eventRequestStatusUpdateDto.getRequestIds());
        if (requestsToStatusUpdate.stream()
                .anyMatch(request -> !request.getStatus().equals(RequestStatus.PENDING))) {
            throw new DataConflictException("Applications must be in status \"PENDING\"");
        }

        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        RequestStatus updateStatus = eventRequestStatusUpdateDto.getStatus();

        // Проверяем, не превысит ли подтверждение лимит участников
        if (updateStatus == RequestStatus.CONFIRMED) {
            Long confirmedRequestsCount = requestRepository.findAllByEventId(eventId)
                    .stream()
                    .filter(e -> e.getStatus().equals(RequestStatus.CONFIRMED))
                    .count();
            if (confirmedRequestsCount + requestsToStatusUpdate.size() > event.getParticipantLimit()) {
                throw new DataConflictException("Application limit exceeded - confirmation not allowed");
            }
        }

        if (updateStatus == RequestStatus.REJECTED) {
            for (Request request : requestsToStatusUpdate) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        } else if (updateStatus == RequestStatus.CONFIRMED) {
            for (Request request : requestsToStatusUpdate) {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
            }

            // Если после подтверждения будет достигнут лимит, отклоняем все остальные
            // pending заявки
            Long confirmedRequestsCount = requestRepository.findAllByEventId(eventId)
                    .stream()
                    .filter(e -> e.getStatus().equals(RequestStatus.CONFIRMED))
                    .count();
            if (confirmedRequestsCount + requestsToStatusUpdate.size() >= event.getParticipantLimit()) {
                List<Request> pendingRequests = requestRepository.findAllByEventIdAndStatus(eventId,
                        RequestStatus.PENDING);
                for (Request pendingRequest : pendingRequests) {
                    pendingRequest.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(pendingRequest);
                }
            }
        }

        requestRepository.saveAll(confirmedRequests);
        requestRepository.saveAll(rejectedRequests);

        return new EventRequestStatusUpdateResult(
                confirmedRequests.stream()
                        .map(requestMapper::toRequestDto)
                        .toList(),
                rejectedRequests.stream()
                        .map(requestMapper::toRequestDto)
                        .toList());
    }

    @Override
    public List<RequestDto> findAllByEventId(Long id) {
        return requestRepository.findAllByEventId(id)
                .stream()
                .map(requestMapper::toRequestDto)
                .toList();
    }

    @Override
    public Map<Long, Long> countRequestsForEvents(List<Long> eventIds) {
        return requestRepository
                .countByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED)
                .stream()
                .collect(Collectors.toMap(
                        EventResult::getEventId,
                        EventResult::getCount));
    }

    private void validateRequestCreation(Long userId, EventDto event) {
        // Проверяем, что инициатор не подает заявку на свое же событие
        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("Инициатор события не может подать заявку на участие");
        }

        // Проверяем, что событие опубликовано
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        // Проверяем лимит участников
        if (event.getParticipantLimit() > 0) {
            Long confirmedRequests = requestRepository.countConfirmedRequests(event.getId());
            if (confirmedRequests != null && confirmedRequests >= event.getParticipantLimit()) {
                throw new ConflictException("Достигнут лимит участников события");
            }
        }
    }

    private Optional<UserDto> findUserById(Long userId) {
        List<UserDto> userDtos = userClient.getUsers(List.of(userId));
        return userDtos.isEmpty() ? Optional.empty() : Optional.of(userDtos.getFirst());
    }

    private Optional<EventDto> findEventById(Long eventId) {
        List<EventDto> eventDtos = eventClient.getEvents(List.of(eventId));
        return eventDtos.isEmpty() ? Optional.empty() : Optional.of(eventDtos.getFirst());
    }

}