package com.xebia.xup.repository;

import com.xebia.xup.models.Poll;
import com.xebia.xup.models.Status;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {

	List<Poll> findAllByMonitor_MonitorName(String monitorName, Pageable pageAndSort);

	@Query(value = "SELECT AVG(p.latency) from Poll p where p.monitor.monitorName = ?1")
	Double findAverageLatency(String monitorName);

	List<Poll> findByMonitor_MonitorNameAndStatus(String monitorName, Status status, Pageable pageAndSort);

	default List<Poll> findMonitorByNameAndStatus(String monitorName, Status status, Pageable pageAndSort) {
		return findByMonitor_MonitorNameAndStatus(monitorName, status, pageAndSort);
	}

	List<Poll> findByMonitor_MonitorNameAndTimeIsAfterAndStatus(String monitorName, LocalDateTime time,
	                                                            Status status, Pageable pageAndSort);


	default List<Poll> findMonitorByNameAndTimeAfterAndStatus(String monitorName, LocalDateTime time,
	                                                          Status status, Pageable pageAndSort) {
		return findByMonitor_MonitorNameAndTimeIsAfterAndStatus(monitorName, time, status, pageAndSort);
	}

}
