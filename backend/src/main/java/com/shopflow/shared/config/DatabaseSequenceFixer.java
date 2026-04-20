package com.shopflow.shared.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Postgres-only safety net: if data was inserted manually (or restored),
 * sequences/identity generators can get out of sync, causing duplicate PK errors.
 *
 * This aligns the products.id sequence to MAX(products.id)+1 at startup.
 */
@Slf4j
@Component
public class DatabaseSequenceFixer {

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void fixProductIdSequenceIfNeeded() {
        try {
            entityManager
                    .createNativeQuery("""
                        select setval(
                          pg_get_serial_sequence('products', 'id'),
                          coalesce((select max(id) from products), 0) + 1,
                          false
                        )
                        """)
                    .getSingleResult();
            log.info("Aligned products.id sequence with current max(id).");
        } catch (Exception e) {
            // If DB isn't Postgres or the sequence doesn't exist, ignore.
            log.debug("Skipping products.id sequence alignment: {}", e.getMessage());
        }
    }
}

