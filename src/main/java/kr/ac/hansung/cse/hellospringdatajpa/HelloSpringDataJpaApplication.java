package kr.ac.hansung.cse.hellospringdatajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class HelloSpringDataJpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloSpringDataJpaApplication.class, args);
    }

}
