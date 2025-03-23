package com.example.intumit_project.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context // Correct import for Thymeleaf Context
import org.thymeleaf.spring6.SpringTemplateEngine // Ensure you're using the Spring-specific template engine

@Service
class GroovyService {

    Map<String, String> getGreetingMessage() {
        Map<String, String> data = new HashMap<>()
        data.put("name", "John Doe")
        data.put("message", "Hello, welcome to the Groovy Spring Boot application!")
        return data
    }
}