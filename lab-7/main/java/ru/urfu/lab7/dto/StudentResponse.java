package ru.urfu.lab7.dto;

public record StudentResponse(
    Long id,
    String name,
    String surname,
    String faculty,
    int age,
    Long groupId,
    String groupCode) {}
