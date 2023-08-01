package com.devrezaur.main;

import com.devrezaur.main.model.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.List;
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

    @Test
    public void mapTest3() {
        // Set some map data in redis
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapReactive<Integer, Student> map = this.client.getMap("students", codec);

        Student student1 = new Student("Rezaur Rahman", 25, "Dhaka", List.of("Physics", "Biology"));
        Student student2 = new Student("Fahim Faysal", 35, "Rangpur", List.of("Math", "History"));

        Mono<Student> mono1 = map.put(1, student1);
        Mono<Student> mono2 = map.put(2, student2);

        // Test if the set method is executed successfully
        StepVerifier
                .create(mono1.concatWith(mono2))
                .expectNext(student1)
                .expectNext(student2)
                .verifyComplete();

        // Get map data from redis
        Mono<Map<Integer, Student>> get = map.getAll(Set.of(1, 2));

        // Test the retrieved values from redis
        StepVerifier
                .create(get)
                .expectNext(getStudentMap())
                .verifyComplete();
    }

    private HashMap<String, String> getUserMap() {
        HashMap<String, String> user = new HashMap<>();
        user.put("name", "Rezaur Rahman");
        user.put("city", "Dhaka");
        return user;
    }

    private HashMap<Integer, Student> getStudentMap() {
        HashMap<Integer, Student> studentMap = new HashMap<>();
        studentMap.put(1, new Student("Rezaur Rahman", 25, "Dhaka", List.of("Physics", "Biology")));
        studentMap.put(2, new Student("Fahim Faysal", 35, "Rangpur", List.of("Math", "History")));
        return studentMap;
    }
}
