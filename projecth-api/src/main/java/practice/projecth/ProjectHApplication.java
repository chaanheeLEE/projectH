package practice.projecth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = "practice.projecth")
@EntityScan(basePackages = "practice.projecth.infrastructure.persistence")
@EnableJpaRepositories(basePackages = "practice.projecth.infrastructure.persistence")
@EnableRetry
@EnableAsync
public class ProjectHApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectHApplication.class, args);
    }
}
