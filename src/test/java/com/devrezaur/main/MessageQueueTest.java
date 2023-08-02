package com.devrezaur.main;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

/**
 * Run "consumer1" method first. It will start listening from the message queue.
 * Then run "producer" method. It will start adding numbers to the message queue.
 * Monitor the console log for "consumer1" method.
 * --
 * Now run "consumer2". And observe the console log for "consumer1" & "consumer2"
 */
public class MessageQueueTest extends BaseTestClass {

    private RBlockingDequeReactive<Long> messageQueue;

    @BeforeAll
    public void setupQueue() {
        this.messageQueue = this.client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    @AfterAll
    public void shutDown() {
    }

    @Test
    public void consumer1() {
        // Get elements from the message queue
        this.messageQueue
                .takeElements()
                .doOnNext(i -> System.out.println("Consumer 1: " + 1))
                .doOnError(System.out::println).subscribe();

        sleep(100000);
    }

    @Test
    public void consumer2() {
        // Get elements from the message queue
        this.messageQueue
                .takeElements()
                .doOnNext(i -> System.out.println("Consumer 2: " + 1))
                .doOnError(System.out::println).subscribe();

        sleep(100000);
    }

    @Test
    public void producer() {
        // Add elements to the message queue
        Mono<Void> mono = Flux
                            .range(1, 100)
                            .delayElements(Duration.ofMillis(500))
                            .doOnNext(i -> System.out.println("Producer going to add: " + i))
                            .flatMap(i -> this.messageQueue.add(Long.valueOf(i)))
                            .then();

        // Verify the execution is successful or not
        StepVerifier.create(mono).verifyComplete();
    }
}
