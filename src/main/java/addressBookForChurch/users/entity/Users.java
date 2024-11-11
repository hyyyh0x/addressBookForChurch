package addressBookForChurch.users.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    @Pattern(regexp = "010-?\\d{4}-?\\d{4}")
    private String phone;

    @Lob
    private byte[] picture;

    @Column(columnDefinition = "TEXT")
    private String prayerNote;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Users(String name, String phone, String prayerNote, byte[] picture){
        this.name = name;
        this.phone = phone;
        this.prayerNote = prayerNote;
        this.picture = picture;
    }

    public void update(String name, String phone, String prayerNote, byte[] picture){
        this.name = name;
        this.phone = phone;
        this.prayerNote = prayerNote;
        this.picture = picture;
    }
}
