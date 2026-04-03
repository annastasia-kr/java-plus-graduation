package ru.practicum.service.event;

import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.impl.StatsClient;
import ru.practicum.StatsDto;
import ru.practicum.event.enums.StateActionAdmin;
import ru.practicum.event.enums.StateActionUser;
import ru.practicum.exception.*;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.LocationMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.request.client.RequestClient;
import ru.practicum.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.enums.Sort;
import ru.practicum.event.enums.StateEvent;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.user.client.UserClient;
import ru.practicum.user.dto.UserDto;

import java.lang.IllegalStateException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private static final String URI = "/events/";

    private final EventRepository eventRepository;
    private final UserClient userClient;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EntityManager entityManager;
    private final StatsClient statsClient;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final RequestClient requestClient;

    @Override
    public Collection<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        findUserById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));

        Pageable page = PageRequest.of(from / size, size);
        return eventRepository.findAllByInitiator(userId, page).stream()
                .map(eventMapper::toEventShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventDto createEvent(Long userId, NewEventDto newEventDto) {
        UserDto findedUser = findUserById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));
        Category findedCategory = categoryRepository.findById(newEventDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category not found"));
        if (!newEventDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("The event date must exceed the current timestamp + 2H");
        }
        Event createdEvent = eventMapper.toEvent(newEventDto, findedCategory, findedUser,
                getEventLocation(newEventDto.getLocation()));
        return eventMapper.toEventDto(eventRepository.save(createdEvent), 0L, 0L);

    }

    @Override
    public EventDto getEventById(Long userId, Long eventId) {
        findUserById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found"));
        Long confirmedRequests = requestClient.findAllByEventId(eventId)
                .stream()
                .filter(e -> e.getStatus().equals(RequestStatus.CONFIRMED))
                .count();

        return eventMapper.toEventDto(event, confirmedRequests, 0L);

    }

    @Override
    @Transactional
    public EventDto updateEventByUser(Long userId, Long eventId, UpdateEventDtoUserRequest updateEventDtoUserRequest) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found"));
        if (event.getState() == StateEvent.PUBLISHED) {
            throw new DataConflictException("Operation is not permitted for a published event");
        }

        if (updateEventDtoUserRequest.getStateAction() != null) {
            if (updateEventDtoUserRequest.getStateAction() == StateActionUser.SEND_TO_REVIEW) {
                event.setState(StateEvent.PENDING);
            } else if (updateEventDtoUserRequest.getStateAction() == StateActionUser.CANCEL_REVIEW) {
                event.setState(StateEvent.CANCELED);
            }
        }

        if (updateEventDtoUserRequest.getAnnotation() != null && !updateEventDtoUserRequest.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventDtoUserRequest.getAnnotation());
        }
        if (updateEventDtoUserRequest.getDescription() != null
                && !updateEventDtoUserRequest.getDescription().isBlank()) {
            event.setDescription(updateEventDtoUserRequest.getDescription());
        }
        if (updateEventDtoUserRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventDtoUserRequest.getCategory()).orElseThrow(
                    () -> new NotFoundException("Category not found")));
        }
        if (updateEventDtoUserRequest.getEventDate() != null) {
            if (!updateEventDtoUserRequest.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("The event date must exceed the current timestamp + 2H");
            }
            event.setEventDate(updateEventDtoUserRequest.getEventDate());
        }
        if (updateEventDtoUserRequest.getLocation() != null && updateEventDtoUserRequest.getLocation().getLat() != null
                && updateEventDtoUserRequest.getLocation().getLon() != null) {
            event.setLocation(getEventLocation(updateEventDtoUserRequest.getLocation()));
        }
        if (updateEventDtoUserRequest.getPaid() != null) {
            event.setPaid(updateEventDtoUserRequest.getPaid());
        }
        if (updateEventDtoUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDtoUserRequest.getParticipantLimit());
        }
        if (updateEventDtoUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDtoUserRequest.getRequestModeration());
        }
        if (updateEventDtoUserRequest.getTitle() != null) {
            event.setTitle(updateEventDtoUserRequest.getTitle());
        }
        Long confirmedRequests = requestClient.findAllByEventId(eventId)
                .stream()
                .filter(e -> e.getStatus().equals(RequestStatus.CONFIRMED))
                .count();

        return eventMapper.toEventDto(eventRepository.save(event), confirmedRequests, 0L);
    }

    @Override
    public Collection<RequestDto> getRequestsByUserIdAndEventId(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found"));
        if (!event.getInitiator().equals(userId)) {
            throw new AccessDeniedForUserException("Access denied: User is not an initiator");
        }
        return requestClient.findAllByEventId(eventId).stream()
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateDto eventRequestStatusUpdateDto) {
        findUserById(userId).orElseThrow(
                () -> new NotFoundException("User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found"));
        if (!event.getInitiator().equals(userId)) {
            throw new AccessDeniedForUserException("Access denied: User is not an initiator");
        }
        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            throw new IllegalStateException("Application confirmation is not required");
        }

        return requestClient.updateRequestStatus(userId, eventId, eventRequestStatusUpdateDto);
    }

    @Override
    public Collection<EventDto> getEventsByAdmin(List<Long> users, List<StateEvent> states, List<Long> categories,
                                                 LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("RangeStart is not earlier than rangeEnd");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = cb.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();
        applyUserFilter(predicates, root, users);
        applyStateFilter(predicates, root, states);
        applyCategoryFilter(predicates, root, categories);
        applyDateRangeFilter(predicates, cb, root, rangeStart, rangeEnd);

        if (!predicates.isEmpty()) {
            criteriaQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        List<Event> events = typedQuery.getResultList();
        log.error("!!!Size: " + events.size());
        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        log.error("!!!eventIds Size: " + eventIds.size());
        for (Long l : eventIds) {
            log.error("!!!l: " + l);
        }
        Map<Long, Long> confirmedRequestsMap = requestClient
                .countByEventIdsAndStatusMap(eventIds, RequestStatus.CONFIRMED);
        for (Map.Entry<Long, Long> entry : confirmedRequestsMap.entrySet()) {
            log.error("!!!Event ID: " + entry.getKey() + ", Confirmed requests: " + entry.getValue());
        }
        return events.stream()
                .map(event -> {
                    Long confirmedRequests = confirmedRequestsMap != null
                            ? confirmedRequestsMap.getOrDefault(event.getId(), 0L)
                            : 0L;
                    log.error("!!!Long confirmedRequests: " + confirmedRequests);
                    return eventMapper.toEventDto(event, confirmedRequests, 0L);
                })
                .toList();
    }

    @Override
    @Transactional
    public EventDto updateEventByAdmin(Long eventId, UpdateEventDtoAdminRequest updateEventDtoAdminRequest) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found"));

        if (event.getState() != StateEvent.PENDING) {
            throw new DataConflictException("To publish an event, it must first be in the pending publication status");
        }

        if (updateEventDtoAdminRequest.getStateAction() != null) {
            if (updateEventDtoAdminRequest.getStateAction().equals(StateActionAdmin.PUBLISH_EVENT)) {
                event.setState(StateEvent.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (updateEventDtoAdminRequest.getStateAction().equals(StateActionAdmin.REJECT_EVENT)) {
                if (event.getState().equals(StateEvent.PUBLISHED)) {
                    throw new DataConflictException("An event can only be rejected if it has not yet been published");
                }
                event.setState(StateEvent.CANCELED);
            }
        }

        if (updateEventDtoAdminRequest.getEventDate() != null) {
            if (updateEventDtoAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("The event date must exceed the current timestamp + 1H");
            } else {
                event.setEventDate(updateEventDtoAdminRequest.getEventDate());
            }
        }
        if (updateEventDtoAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventDtoAdminRequest.getAnnotation());
        }
        if (updateEventDtoAdminRequest.getDescription() != null) {
            event.setDescription(updateEventDtoAdminRequest.getDescription());
        }
        if (updateEventDtoAdminRequest.getPaid() != null) {
            event.setPaid(updateEventDtoAdminRequest.getPaid());
        }
        if (updateEventDtoAdminRequest.getTitle() != null) {
            event.setTitle(updateEventDtoAdminRequest.getTitle());
        }
        if (updateEventDtoAdminRequest.getCategory() != null) {
            event.setCategory(categoryRepository.findById(updateEventDtoAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category not found")));
        }
        if (updateEventDtoAdminRequest.getLocation() != null) {
            event.setLocation(getEventLocation(updateEventDtoAdminRequest.getLocation()));
        }
        if (updateEventDtoAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDtoAdminRequest.getParticipantLimit());
        }
        if (updateEventDtoAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDtoAdminRequest.getRequestModeration());
        }
        try {
            Long confirmedRequests = requestClient.findAllByEventId(eventId)
                    .stream()
                    .filter(e -> e.getStatus().equals(RequestStatus.CONFIRMED))
                    .count();
            return eventMapper.toEventDto(eventRepository.save(event), confirmedRequests, 0L);
        } catch (FeignException e) {
            throw new ConflictException("FException " + e.getMessage());
        }
    }

    @Override
    public Collection<EventDto> getEventsPublic(String text, List<Long> categories, Boolean paid,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Boolean onlyAvailable, Sort sort, Integer from, Integer size, HttpServletRequest httpServletRequest) {

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("RangeStart is not earlier than rangeEnd");
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = cb.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        applyTextFilter(predicates, cb, root, text);
        applyCategoryFilter(predicates, root, categories);
        applyPaidFilter(predicates, cb, root, paid);
        applyDateRangeFilter(predicates, cb, root, rangeStart, rangeEnd);
        applyStateFilter(predicates, root, List.of(StateEvent.PUBLISHED));

        if (!predicates.isEmpty()) {
            criteriaQuery.where(cb.and(predicates.toArray(new Predicate[0])));
        }
        if (sort != null) {
            applySortValue(criteriaQuery, cb, root, sort);
        }

        TypedQuery<Event> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);

        List<Event> events = typedQuery.getResultList();

        statsClient.saveHit(httpServletRequest);

        if (events.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> confirmedRequestsMap = requestClient
                .countByEventIdsAndStatusMap(eventIds, RequestStatus.CONFIRMED);

        LocalDateTime minStartDate = events.stream()
                .map(Event::getPublishedOn)
                .min(LocalDateTime::compareTo) // минимум
                .orElse(LocalDateTime.now());

        // статистика для каждого ивента
        List<StatsDto> statistics = statsClient.getStats(minStartDate, LocalDateTime.now(),
                        (eventIds.stream()
                                .map(id -> URI + id)
                                .toList()), true).stream()
                .toList();

        Map<String, Long> hits = statistics.stream()
                .collect(Collectors.toMap(
                        StatsDto::getUri,
                        StatsDto::getHits));

        Map<Long, Long> eventsIdWithHits = eventIds.stream()
                .collect(Collectors.toMap(
                        num -> num,
                        num -> hits.getOrDefault(URI + num, 0L)));

        return events.stream()
                .filter(event -> !onlyAvailable ||
                        event.getParticipantLimit() == 0 ||
                        ((confirmedRequestsMap != null) ? confirmedRequestsMap.getOrDefault(event.getId(), 0L)
                                : 0L) < event.getParticipantLimit())
                .map(event -> {
                    Long confirmedRequests = (confirmedRequestsMap != null)
                            ? confirmedRequestsMap.getOrDefault(event.getId(), 0L)
                            : 0L;
                    Long views = eventsIdWithHits.getOrDefault(event.getId(), 0L);

                    return eventMapper.toEventDto(event, confirmedRequests, views);
                })
                .toList();
    }


    @Override
    public List<EventDto> getEvents(List<Long> eventIds) {
        return eventRepository.findAllById(eventIds).stream()
                .map(event -> {
                    // Получаем количество подтверждённых запросов для текущего события
                    Long confirmedRequests = requestClient.findAllByEventId(event.getId())
                            .stream()
                            .filter(e -> e.getStatus().equals(RequestStatus.CONFIRMED))
                            .count();
                    LocalDateTime start = event.getPublishedOn() == null ? event.getCreatedOn() : event.getPublishedOn();

                    // Получаем просмотры для текущего события
                    // Здесь нужно подставить корректные значения для start и end
                    Long views = getEventViews(start, event.getId());

                    // Преобразуем сущность в DTO, передавая дополнительные данные
                    return eventMapper.toEventDto(event, confirmedRequests, views);
                })
                .collect(Collectors.toList());
    }

    @Override
    public EventDto getEvent(Long eventId, HttpServletRequest httpServletRequest) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event not found"));
        if (!event.getState().equals(StateEvent.PUBLISHED)) {
            throw new NotFoundException("Event must be published");
        }
        statsClient.saveHit(httpServletRequest);

        LocalDateTime start = event.getPublishedOn() == null ? event.getCreatedOn() : event.getPublishedOn();

        eventRepository.save(event);
        Long confirmedRequests = requestClient.findAllByEventId(eventId)
                .stream()
                .filter(e -> e.getStatus().equals(RequestStatus.CONFIRMED))
                .count();
        Long views = getEventViews(start, event.getId());
        return eventMapper.toEventDto(event, confirmedRequests, views);
    }

    @Override
    public Optional<EventDto> getEvent(Long id) {
        return eventRepository.findById(id).map(e ->
                eventMapper.toEventDto(e,
                        Objects.nonNull(e.getConfirmedRequests()) ? e.getConfirmedRequests() : 0L, 0L));

    }

    private Location getEventLocation(LocationDto locationDto) {
        Optional<Location> location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return location.orElseGet(() -> locationRepository.save(locationMapper.toLocation(locationDto, 0L)));
    }

    private void applyDateRangeFilter(List<Predicate> predicates, CriteriaBuilder cb, Root<Event> root,
                                      LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"),
                Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now)));
        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
    }

    private void applyCategoryFilter(List<Predicate> predicates, Root<Event> root, List<Long> categories) {
        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }
    }

    private void applyStateFilter(List<Predicate> predicates, Root<Event> root,
                                  List<StateEvent> states) {
        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in(states));
        }
    }

    private void applyUserFilter(List<Predicate> predicates, Root<Event> root, List<Long> users) {
        if (users != null && !users.isEmpty()) {
            predicates.add(root.get("initiator").in(users));
        }
    }

    private void applyTextFilter(List<Predicate> predicates, CriteriaBuilder cb, Root<Event> root, String text) {
        if (text != null && !text.isBlank()) {
            String searchText = "%" + text.toLowerCase() + "%";
            predicates.add(cb.or(cb.like(cb.lower(root.get("annotation")), searchText),
                    cb.like(cb.lower(root.get("description")), searchText)));
        }
    }

    private void applyPaidFilter(List<Predicate> predicates, CriteriaBuilder cb, Root<Event> root, Boolean paid) {
        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }
    }

    private void applySortValue(CriteriaQuery<Event> query, CriteriaBuilder cb, Root<Event> root, Sort sort) {
        if (sort != null) {
            query.orderBy(cb.desc(root.get(sort.getParamName())));
        }
    }

    private Long getEventViews(LocalDateTime createdOn, Long eventId) {
        log.warn("Method getEventViews {} {}, ", createdOn, eventId);
        List<StatsDto> stat = statsClient.getStats(createdOn, LocalDateTime.now(),
                List.of(URI + eventId), true);
        if (stat.isEmpty()) {
            return 0L;
        }
        return stat.get(0).getHits();
    }

    private Optional<UserDto> findUserById(Long userId) {
        Optional<UserDto> user = userClient.getUserById(userId);
        return user.isEmpty() ? Optional.empty() : user;
    }
}
