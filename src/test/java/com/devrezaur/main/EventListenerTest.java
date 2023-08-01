package com.devrezaur.main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.redisson.api.DeletedObjectListener;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Due to CPU usage issue ExpiredObjectListener is disabled by default.
 * To enable this, run this comment in redis-cli "config set notify-keyspace-events AKE"
 */
public class EventListenerTest extends BaseTestClass {

    @Test
    public void expiredEventTest() {
        // Set data in redis
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Fahim Faysal", 2, TimeUnit.SECONDS);

        AtomicReference<String> message = new AtomicReference<>();

        // Add event listener for key expiry event
        Mono<Void> eventMono =
                bucket.addListener((ExpiredObjectListener) name -> message.set("Expired key: " + name)).then();

        // Testing the expiry event
        StepVerifier
                .create(set.then(eventMono))
                .verifyComplete();

        // Sleeping for 3 seconds
        sleep(3000);

        // Checking the expiry message
        Assertions.assertEquals("Expired key: user:1:name", message.get());
    }

    @Test
    public void deleteEventTest() {
        // Set data in redis
        RBucketReactive<String> bucket = this.client.getBucket("user:1:name", StringCodec.INSTANCE);
        Mono<Void> set = bucket.set("Fahim Faysal");
        Mono<Boolean> delete = bucket.delete();

        AtomicReference<String> message = new AtomicReference<>();

        // Add event listener for delete key event
        Mono<Void> eventMono =
                bucket.addListener((DeletedObjectListener) name -> message.set("Deleted key: " + name)).then();

        // Testing the delete event
        StepVerifier
                .create(set.then(eventMono).then(delete))
                .expectNext(true)
                .verifyComplete();

        // Checking the deleted message
        Assertions.assertEquals("Deleted key: user:1:name", message.get());
    }
}
