package ru.practicum.user.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service", fallback = UserClientFallback.class)
public interface UserClient extends UserOperations {
}
