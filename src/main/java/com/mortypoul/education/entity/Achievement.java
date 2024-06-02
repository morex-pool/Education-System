package com.mortypoul.education.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @ManyToMany
    @JoinTable(name = "achievement_appuser",
            joinColumns = @JoinColumn(name = "achievement_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> appUsers;

}

