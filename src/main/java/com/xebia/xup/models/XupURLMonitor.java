package com.xebia.xup.models;

import java.time.Duration;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.URL;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class XupURLMonitor {

    @Id
    private String monitorName;

    @URL
    private String uri;

    private Duration delay;

}
