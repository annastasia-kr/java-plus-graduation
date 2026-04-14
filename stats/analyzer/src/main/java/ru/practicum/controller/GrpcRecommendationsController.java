package ru.practicum.controller;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.ewm.stats.proto.*;
import ru.practicum.service.RecommendationService;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@GrpcService
@Slf4j
public class GrpcRecommendationsController extends RecommendationsControllerGrpc.RecommendationsControllerImplBase {

    private final RecommendationService service;

    @Override
    public void getRecommendationsForUser(UserPredictionsRequestProto request,
                                          StreamObserver<RecommendedEventProto> responseObserver) {
        executeOperation(
                "getRecommendationsForUser",
                () -> service.getRecommendationsForUser(request),
                responseObserver
        );
    }

    @Override
    public void getSimilarEvents(SimilarEventsRequestProto request,
                                 StreamObserver<RecommendedEventProto> responseObserver) {
        executeOperation(
                "getSimilarEvents",
                () -> service.getSimilarEvents(request),
                responseObserver
        );
    }

    @Override
    public void getInteractionsCount(InteractionsCountRequestProto request,
                                     StreamObserver<RecommendedEventProto> responseObserver) {
        executeOperation(
                "getInteractionsCount",
                () -> service.getInteractionsCount(request),
                responseObserver
        );
    }

    private void executeOperation(
            String operationName,
            Supplier<List<RecommendedEventProto>> serviceCall,
            StreamObserver<RecommendedEventProto> responseObserver) {
        try {
            serviceCall.get().forEach(responseObserver::onNext);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Operation execution error {}: {}", operationName, e.getMessage(), e);
            responseObserver.onError(e);
        }
    }
}
