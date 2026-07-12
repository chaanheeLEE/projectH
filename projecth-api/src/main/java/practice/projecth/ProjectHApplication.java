package practice.projecth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "practice.projecth")
@EntityScan(basePackages = "practice.projecth.infrastructure.persistence")
@EnableJpaRepositories(basePackages = "practice.projecth.infrastructure.persistence")
public class ProjectHApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectHApplication.class, args);
    }
}
