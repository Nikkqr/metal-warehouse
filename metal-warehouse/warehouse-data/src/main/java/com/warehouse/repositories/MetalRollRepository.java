package com.warehouse.repositories;

import com.warehouse.entities.MetalRoll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MetalRollRepository extends JpaRepository<MetalRoll, Integer> {
    List<MetalRoll> findByDateAddedBetween(LocalDate addedFrom, LocalDate addedTo);

    List<MetalRoll> findByDateOfDeletionBetween(LocalDate removedFrom, LocalDate removedTo);

    @Query("SELECT r FROM MetalRoll r WHERE " +
            "r.dateAdded <= :end AND " +
            "(r.dateOfDeletion >= :start OR r.dateOfDeletion IS NULL)")
    List<MetalRoll> findActiveInPeriod(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
