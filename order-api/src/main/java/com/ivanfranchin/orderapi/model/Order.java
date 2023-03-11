package com.ivanfranchin.orderapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private ZonedDateTime createdAt;

    public Order(String description) {
        this.description = description;
    }

    @PrePersist
    public void onPrePersist() {
        createdAt = ZonedDateTime.now();
    }
}
