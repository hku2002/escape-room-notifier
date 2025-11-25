package kr.co.escape.api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alert_id", nullable = false)
    private UserAlert userAlert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "availability_id", nullable = false)
    private Availability availability;

    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    @Column(name = "notification_type", nullable = false)
    private String notificationType;

    @Column(name = "status", nullable = false)
    private String status;

    public Notification(User user, UserAlert userAlert, Availability availability,
                       String notificationType, String status) {
        this.user = user;
        this.userAlert = userAlert;
        this.availability = availability;
        this.notificationType = notificationType;
        this.status = status;
    }

    public void updateStatus(String status) {
        this.status = status;
    }
}