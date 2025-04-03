package com.kir138.reposity;

import com.kir138.enumStatus.OutboxStatus;
import com.kir138.model.entity.OutboxProduct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxProductRepository extends JpaRepository<OutboxProduct, Long> {

    List<OutboxProduct> findAllByStatus(OutboxStatus status, Pageable pageable);
}
