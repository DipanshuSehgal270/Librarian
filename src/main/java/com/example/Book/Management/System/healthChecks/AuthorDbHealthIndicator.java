//package com.example.Book.Management.System.healthChecks;
//
//import com.example.Book.Management.System.repository.AuthorRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.health.Health;
//import org.springframework.boot.actuate.health.HealthIndicator;
//import org.springframework.stereotype.Component;
//
//@Component
//public class AuthorDbHealthIndicator implements HealthIndicator {
//
//    @Autowired
//    private AuthorRepository authorRepository;
//
//    @Override
//    public Health health() {
//        try{
//            Long authorCount = authorRepository.count();
//
//            return Health.up().withDetail("Author Count" , authorCount)
//                    .withDetail("repositoryStatus" , "Connected and Operational")
//                    .build();
//
//        } catch (Exception e) {
//
//            return Health.down().withDetail("repositoryStatus" , "Connection Failure")
//                    .withDetail("Error" , e.getMessage()).build();
//        }
//    }
//}
