package com.fas.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Session_Record")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Session_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Client_ID", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Client client;

    @CreationTimestamp
    @Column(name = "Session_Date", updatable = false, nullable = false)
    private LocalDateTime uploadTime;



    @Column(name = "Duration", length = 30)
    private String duration;

    @Column(
            precision = 10,
            scale = 2
    )
    private BigDecimal price;

    @Column(name = "Notes", length = 1000)
    private String notes;

    @OneToOne(mappedBy = "session",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)

    private NutritionPlan nutritionPlan;

    @OneToOne(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    private BodyData bodyData;

    @OneToMany(
            mappedBy = "session",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Examination> examinations = new ArrayList<>();

    public Session() {}

    public Session(
            Client client,
            String duration,
            BigDecimal price,
            String notes
    ){
        this.client = client;
        this.duration = duration;
        this.price = price;
        this.notes = notes;
    }



    public Session(Long sessionId, Client client, String duration, BigDecimal price, String notes) {
        this.id = sessionId;
        this.client = client;
        this.duration = duration;
        this.price = price;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public LocalDateTime getUploadTime() { return uploadTime; }
    public void setUploadTime(LocalDateTime uploadTime) { this.uploadTime = uploadTime; }


    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public NutritionPlan getNutritionPlan() { return nutritionPlan; }
    public void setNutritionPlan(NutritionPlan nutritionPlan) { this.nutritionPlan = nutritionPlan; }

    public BodyData getBodyData() { return bodyData; }
    public void setBodyData(BodyData bodyData) { this.bodyData = bodyData; }

    public List<Examination> getExaminations() { return examinations; }
    public void setExaminations(List<Examination> examinations) {
        if (examinations != null) {
            this.examinations = examinations;
        }
    }

    public void addExamination(Examination examination) {
        if (this.examinations == null) {
            this.examinations = new ArrayList<>();
        }
        if (examination != null) {
            this.examinations.add(examination);
        }
    }
}
