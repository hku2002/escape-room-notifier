package kr.co.escape.api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_alerts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(name = "date_start", nullable = false)
    private LocalDate dateStart;

    @Column(name = "date_end", nullable = false)
    private LocalDate dateEnd;

    @Column(name = "preferred_times", columnDefinition = "TEXT")
    private String preferredTimes;

    @Column(name = "num_people")
    private Integer numPeople;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "userAlert", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public UserAlert(User user, Theme theme, LocalDate dateStart, LocalDate dateEnd,
                     String preferredTimes, Integer numPeople) {
        this.user = user;
        this.theme = theme;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.preferredTimes = preferredTimes;
        this.numPeople = numPeople;
        this.isActive = true;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}