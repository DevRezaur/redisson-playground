package com.devrezaur.main;

import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapTest extends BaseTestClass {

    @Test
    public void mapTest1() {
        // Set some map data in redis
        RMapReactive<String, String> map = this.client.getMap("user:1", StringCodec.INSTANCE);
        Mono<String> name = map.put("name", "Rezaur Rahman");
        Mono<String> city = map.put("city", "Dhaka");

        // Test if the set method is executed successfully
        StepVerifier
                .create(name.concatWith(city).then())
                .verifyComplete();

        // Get map data from redis
        Mono<Map<String, String>> get = map.getAll(Set.of("name", "city"));

        // Test the retrieved values from redis
        StepVerifier
                .create(get)
                .expectNext(getUserMap())
                .verifyComplete();
    }

    @Test
    public void mapTest2() {
        // Set some map data in redis
        RMapReactive<String, String> map = this.client.getMap("user:1", StringCodec.INSTANCE);
        Map<String, String> javaMap = Map.of("name", "Rezaur Rahman", "city", "Dhaka");
        Mono<Void> set = map.putAll(javaMap);

        // Test if the set method is executed successfully
        StepVerifier
                .create(set)
                .verifyComplete();

        // Get map data from redis
        Mono<Map<String, String>> get = map.getAll(Set.of("name", "city"));

        // Test the retrieved values from redis
        StepVerifier
                .create(get)
                .expectNext(getUserMap())
                .verifyComplete();
    }

    private HashMap<String, String> getUserMap() {
        HashMap<String, String> user = new HashMap<>();
        user.put("name", "Rezaur Rahman");
        user.put("city", "Dhaka");
        return user;
    }
}
