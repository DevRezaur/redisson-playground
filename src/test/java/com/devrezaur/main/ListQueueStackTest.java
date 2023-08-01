package com.devrezaur.main;

import org.junit.jupiter.api.Test;
import org.redisson.api.RListReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.LongStream;

public class ListQueueStackTest extends BaseTestClass {

    @Test
    public void listTest1() {
        RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);

        // Clearing the list
        list.delete().block();

        // Set some list data in redis
        Flux<Boolean> flux = Flux.range(1, 5).map(Long::valueOf).flatMap(list::add);

        // Testing if the operation is successful
        StepVerifier
                .create(flux)
                .expectNext(true)
                .expectNext(true)
                .expectNext(true)
                .expectNext(true)
                .expectNext(true)
                .verifyComplete();

        // Verifying the list size
        StepVerifier
                .create(list.size())
                .expectNext(5)
                .verifyComplete();
    }

    @Test
    public void listTest2() {
        RListReactive<Long> list = this.client.getList("number-input", LongCodec.INSTANCE);

        // Clearing the list
        list.delete().block();

        // Set some list data in redis
        List<Long> longList = LongStream.range(1, 6).boxed().toList();
        Mono<Boolean> mono = list.addAll(longList);

        // Testing if the operation is successful
        StepVerifier
                .create(mono)
                .expectNext(true)
                .verifyComplete();

        // Verifying the list size
        StepVerifier
                .create(list.size())
                .expectNext(5)
                .verifyComplete();
    }
}
