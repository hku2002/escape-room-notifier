package kr.co.escape.api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "themes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private Long id;

    @Column(name = "cafe_name", nullable = false)
    private String cafeName;

    @Column(name = "theme_name", nullable = false)
    private String themeName;

    @Column(name = "branch")
    private String branch;

    @Column(name = "difficulty")
    private String difficulty;

    @Column(name = "genre")
    private String genre;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "price")
    private Integer price;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAlert> userAlerts = new ArrayList<>();

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Availability> availabilities = new ArrayList<>();

    public Theme(String cafeName, String themeName, String branch, String difficulty,
                 String genre, Integer duration, Integer price) {
        this.cafeName = cafeName;
        this.themeName = themeName;
        this.branch = branch;
        this.difficulty = difficulty;
        this.genre = genre;
        this.duration = duration;
        this.price = price;
    }
}
