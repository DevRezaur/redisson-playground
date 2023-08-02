package com.devrezaur.main;

import org.junit.jupiter.api.Test;
import org.redisson.api.RDequeReactive;
import org.redisson.api.RListReactive;
import org.redisson.api.RQueueReactive;
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

    @Test
    public void queueTest() {
        RQueueReactive<Long> queue = this.client.getQueue("number-input", LongCodec.INSTANCE);

        // Clearing the queue
        queue.delete().block();

        // Set some queue data in redis
        List<Long> longList = LongStream.range(1, 6).boxed().toList();
        Mono<Boolean> mono = queue.addAll(longList);

        // Testing if the operation is successful
        StepVerifier
                .create(mono)
                .expectNext(true)
                .verifyComplete();

        // Verifying the queue size
        StepVerifier
                .create(queue.size())
                .expectNext(5)
                .verifyComplete();

        // Polling data from the queue
        Flux<Long> flux = queue.poll().repeat(4).doOnNext(System.out::println);

        // Verifying the polled data
        StepVerifier
                .create(flux)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .expectNext(5L)
                .verifyComplete();

        // Verifying the queue size again
        StepVerifier
                .create(queue.size())
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    public void stackTest() {
        RDequeReactive<Long> deque = this.client.getDeque("number-input", LongCodec.INSTANCE);

        // Clearing the deque
        deque.delete().block();

        // Set some deque data in redis
        List<Long> longList = LongStream.range(1, 6).boxed().toList();
        Mono<Boolean> mono = deque.addAll(longList);

        // Testing if the operation is successful
        StepVerifier
                .create(mono)
                .expectNext(true)
                .verifyComplete();

        // Verifying the deque size
        StepVerifier
                .create(deque.size())
                .expectNext(5)
                .verifyComplete();

        // Polling data from the deque
        Flux<Long> flux = deque.pollLast().repeat(4).doOnNext(System.out::println);

        // Verifying the polled data
        StepVerifier
                .create(flux)
                .expectNext(5L)
                .expectNext(4L)
                .expectNext(3L)
                .expectNext(2L)
                .expectNext(1L)
                .verifyComplete();

        // Verifying the deque size again
        StepVerifier
                .create(deque.size())
                .expectNext(0)
                .verifyComplete();
    }
}
