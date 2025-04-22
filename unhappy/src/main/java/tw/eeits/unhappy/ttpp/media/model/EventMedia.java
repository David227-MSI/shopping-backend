package tw.eeits.unhappy.ttpp.media.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import tw.eeits.unhappy.ttpp.event.model.Event;
import tw.eeits.unhappy.ttpp.media.enums.MediaType;

@Entity
@Table(name = "event_media")
@Data
public class EventMedia {

    // fk_event_media_event
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    @NotNull(message = "event 不可為空值")
    private Event event;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "mediaData 不可為空值")
    @Column(name = "media_data", nullable = false)
    private byte[] mediaData;

    @NotNull(message = "mediaType 不可為空值")
    @Column(name = "media_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;




    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
