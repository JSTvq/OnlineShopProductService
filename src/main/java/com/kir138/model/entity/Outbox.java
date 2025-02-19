package com.kir138.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outbox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String aggregateType;
    private Long aggregateId;

    // Можно сохранить топик, в который должно быть отправлено сообщение
    private String topic;

    @Column(name = "type")
    private String type;

    // Сериализованный JSON-пейлоуд, который будет отправлен
    @Lob
    private String payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // PENDING, SENT, FAILED

    private LocalDateTime createdAt;
}