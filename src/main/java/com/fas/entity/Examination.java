package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "Examination")
public class Examination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Examination_ID")
    private Long examinationId;; // معرف الفحص (ID)[cite: 8]

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "session_id",
            nullable = false
    )
    @JsonIgnore
    private Session session;    // معرف الجلسة المرتبطة[cite: 8]

    @Column(name = "Examination_Name", nullable = false, length = 100)
    private String examinationName;


    @Column(length = 500)
    private String examinationImage;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime uploadDate;

    @UpdateTimestamp
    private LocalDateTime modificationDate;

    @Column(name = "Notes", length = 500)
    private String notes;

    public Examination() {}

    public Examination(String examinationName, String examinationImage, String notes) {
        this.examinationName = examinationName;
        this.examinationImage = examinationImage;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getExaminationId() { return examinationId; }
    public void setExaminationId(Long examinationId) { this.examinationId = examinationId; }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getExaminationName() { return examinationName; }
    public void setExaminationName(String examinationName) { this.examinationName = examinationName; }

    public String getExaminationImage() { return examinationImage; }
    public void setExaminationImage(String examinationImage) { this.examinationImage = examinationImage; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public LocalDateTime getModificationDate() { return modificationDate; }
    public void setModificationDate(LocalDateTime modificationDate) { this.modificationDate = modificationDate; }
}
