package com.kir138.model.entity;

import com.kir138.enumStatus.OutboxStatus;
import com.kir138.model.dto.ProductValidationResponse;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "outbox_product")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OutboxProduct {

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
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb", nullable = false)
    private ProductValidationResponse payload;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status; // PENDING, SENT, FAILED

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OutboxProduct that = (OutboxProduct) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}