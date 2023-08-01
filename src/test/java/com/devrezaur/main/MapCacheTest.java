package com.devrezaur.main;

import com.devrezaur.main.model.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MapCacheTest extends BaseTestClass {

    @Test
    public void mapCacheTest() {
        // Set some map data in redis
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapCacheReactive<Integer, Student> map = this.client.getMapCache("students:cache", codec);

        Student student1 = new Student("Rezaur Rahman", 25, "Dhaka", List.of("Physics", "Biology"));
        Student student2 = new Student("Fahim Faysal", 35, "Rangpur", List.of("Math", "History"));

        Mono<Student> mono1 = map.put(1, student1, 3, TimeUnit.SECONDS);
        Mono<Student> mono2 = map.put(2, student2, 5, TimeUnit.SECONDS);

        // Test if the set method is executed successfully
        StepVerifier
                .create(mono1.concatWith(mono2))
                .verifyComplete();

        // Get map data from redis after 2 seconds
        sleep(2000);

        Mono<Map<Integer, Student>> get = map.getAll(Set.of(1, 2));

        StepVerifier
                .create(get)
                .expectNext(getStudentMap())
                .verifyComplete();

        // Get map data from redis after 4 seconds
        sleep(2000);

        get = map.getAll(Set.of(1, 2));

        StepVerifier
                .create(get)
                .expectNext(Map.of(2, new Student("Fahim Faysal", 35, "Rangpur", List.of("Math", "History"))))
                .verifyComplete();
    }

    private HashMap<Integer, Student> getStudentMap() {
        HashMap<Integer, Student> studentMap = new HashMap<>();
        studentMap.put(1, new Student("Rezaur Rahman", 25, "Dhaka", List.of("Physics", "Biology")));
        studentMap.put(2, new Student("Fahim Faysal", 35, "Rangpur", List.of("Math", "History")));
        return studentMap;
    }

}
