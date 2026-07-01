package com.twitch.db.entity;

import com.twitch.model.ContactStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "user_contacts")
public class UserContact {
    @Id
    @GeneratedValue
    private Long id;
    private Long requesterId;
    private Long targetId;

    @Enumerated(EnumType.STRING)
    private ContactStatus status; // PENDING, ACCEPTED, REJECTED

    private String targetContactInfo;
    private String requesterContactInfo;
    private LocalDateTime updatedAt;
}
