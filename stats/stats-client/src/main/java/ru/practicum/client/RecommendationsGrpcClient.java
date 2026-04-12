package ru.practicum.client;

import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.*;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class RecommendationsGrpcClient {

    @GrpcClient("analyzer")
    private RecommendationsControllerGrpc.RecommendationsControllerBlockingStub blockingStub;

    @Retryable(
            retryFor = {StatusRuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000)
    )
    public Stream<RecommendedEventProto> getRecommendationsForUser(long userId, int maxResults) {
        UserPredictionsRequestProto request = UserPredictionsRequestProto.newBuilder()
                .setUserId(userId)
                .setMaxResult(maxResults)
                .build();

        log.info("Calling gRPC GetRecommendationsForUser: {}", request);

        try {
            Iterator<RecommendedEventProto> iterator = blockingStub.getRecommendationsForUser(request);
            return collectStream(iterator);
        } catch (Exception e) {
            log.error("getRecommendationsForUser failed : {}", e.getMessage());
        }

        return Stream.empty();
    }

    private Stream<RecommendedEventProto> collectStream(Iterator<RecommendedEventProto> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED),
                false
        );
    }

}