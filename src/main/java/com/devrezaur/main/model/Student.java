package com.devrezaur.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Student {

    private String name;
    private Integer age;
    private String city;
    private List<String> courses;

}
