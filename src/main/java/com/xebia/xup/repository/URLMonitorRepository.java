package com.xebia.xup.repository;


import com.xebia.xup.models.XupURLMonitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface URLMonitorRepository extends JpaRepository<XupURLMonitor, String> {
}
