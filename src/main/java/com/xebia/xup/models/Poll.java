package com.xebia.xup.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Poll {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "status")
	private Status status;

	@Column(name = "time")
	private LocalDateTime time;

	// latency in millis
	@Column(name = "latency")
	private Long latency;

	@ManyToOne(fetch = FetchType.LAZY)
	private XupURLMonitor monitor;

	public Poll(Status status, LocalDateTime time, Long latency, XupURLMonitor monitor) {
		this.status = status;
		this.time = time;
		this.monitor = monitor;
		this.latency = latency;
	}

	public String toString() {
		return "Poll(id=" + this.getId() + ", status=" + this.getStatus() + ", time=" + this.getTime() + ", latency=" + this.getLatency() + ")";
	}
}
