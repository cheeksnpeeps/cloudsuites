package com.cloudsuites.framework.services.announcements.entities;

import com.cloudsuites.framework.modules.common.utils.IdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "announcements")
public class Announcement {

    private static final Logger logger = LoggerFactory.getLogger(Announcement.class);

    @Id
    @Column(name = "announcement_id", unique = true, nullable = false)
    private String announcementId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @ElementCollection
    @CollectionTable(name = "announcement_attachments", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "attachment_url")
    private List<String> attachments;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AnnouncementType type;

    @ElementCollection
    @CollectionTable(name = "announcement_tags", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "tag")
    private List<String> tags;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PriorityLevel priority;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @ElementCollection
    @CollectionTable(name = "announcement_audience", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "audience_detail")
    private List<String> audienceDetails;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private Long views = 0L;

    @ElementCollection
    @CollectionTable(name = "announcement_acknowledgments", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "acknowledgment_user_id")
    private List<String> acknowledgments;

    @PrePersist
    protected void onCreate() {
        this.announcementId = IdGenerator.generateULID("ANN-");
        logger.debug("Generated addressId: {}", this.announcementId);
    }
}
