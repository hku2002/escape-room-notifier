package kr.co.escape.api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "availability")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "availability_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "time_slot", nullable = false)
    private LocalTime timeSlot;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable;

    @CreationTimestamp
    @Column(name = "crawled_at", nullable = false, updatable = false)
    private LocalDateTime crawledAt;

    @OneToMany(mappedBy = "availability", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    public Availability(Theme theme, LocalDate date, LocalTime timeSlot, Boolean isAvailable) {
        this.theme = theme;
        this.date = date;
        this.timeSlot = timeSlot;
        this.isAvailable = isAvailable;
    }

    public void updateAvailability(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}