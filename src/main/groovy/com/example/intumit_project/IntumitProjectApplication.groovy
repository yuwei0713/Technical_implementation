package com.example.intumit_project

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration

@SpringBootApplication(exclude = [HibernateJpaAutoConfiguration])
class IntumitProjectApplication {
    static void main(String[] args) {
        SpringApplication.run(IntumitProjectApplication, args)
    }
}