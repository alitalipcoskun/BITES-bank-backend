# Java Spring Boot

It is a Java Framework that allow developers to create stand-alone, productive based applications. It is easy to run.
The framework does not need to deploy WAR files mandatory.
"Starter" dependencies are provided to have simplified build configuration.
Spring Boot configures Spring automatically and other third party libraries.
There is no need for XML configuration and no code generation.

`https://start.spring.io` allow developers to use specified Spring Boot version and satisfy requirements.

## Future Features
Does the `password` should be sent encrypted to the `AuthenticationService`?
Transaction algorithm? -> The luhn algorithm. may be implemented for checking purposes and creation of accNo.

## Project Creation
![Project.png](readme_images%2FProject.png)

Figure: `start.spring.io` interface for beginning of the project.

### Meaning of Project
The choice between Gradle and Maven depends on your project’s specific needs and your team’s preferences.
Maven is used for more structured project, and Gradle is an option for more flexible projects. I preferred maven in this project.

### Meaning of Language
I wanted to build a Spring Boot application with Java programming language, so my choice is Java because of that.
Java is commonly used in backend applications thanks to Spring Boot and its community. There are various tools for
backend service that must satisfy specific requirements.


### Meaning of Keywords for Spring Boot Versions
- "GA" means general availability
- "CURRENT" means most recent "GA" release that recommended for new projects
- "PRE" pre release versions, but it is for developers to test the features for upcoming "GA".
- "SNAPSHOT" Replaces earlier snapshot of the same version. It can be said to be same like "PRE"

My choice is `3.3.3` because of stability.

### Project Metadata
In this section, you customize your project with your requirements. I wanted to create a project with Java version 22 since it is preferred by other intern that started before me.

### Dependencies
It is optional. Developers decides which dependencies are required. I preferred the dependencies which are located at below for CRUD application.

- PostgreSQL Driver: It is for connecting to MS SQL Database.
- Lombok: It is a Java library that help developers. It generates know pattern of codes and reduce the boilerplate code which defines the piece of code repeats over and over again.
- Spring Web: It is used to create RESTful API for Web application. 
- Spring Data JPA: Persistently store data with classes and methods with respect to the Spring Data and Hibernate.
- Spring Security: It is for authentication and autherization.
  
#### Lombok 
Java is one of the great programming languages in software development. However, you may end up give too much effort on even
smaller goals such as getters and setters. In addition, Java sometimes repeats itself while implementing some business logic. Instead of focusing on `the boiler plate code`, Lombok
allow developer to focus on the business logic instead of these repetitive code snippets.

```xml
<dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <version>1.18.20</version>
</dependency>
```

As a developer which use Maven, you can use `Project Lombok` with adding it to `pom.xml` file just like this.

```java

import lombok.Getter;


class Test {
  @Setter @Getter
  private int a;

  public Test() {

  }
}
```

For private variable `a`, these annotations `@Getter, @Setter` allow
Java to define its Getters and Setters.

Note that, `@Data` is all together a shortcut for @Getter, @Setter, @EqualsAndHashCode, @ToString, @RequiredArgsConstructor...
This annotation generates all the boilerplate that is normally associated with simple POJOs (Plain Old Java Objects).
However, it does not use annotations 'callSuper, includeFieldNames, exclude'. All generated getters and setters are set as public access.

#### Transaction Annotation
The annotation allows backend service to roll back to process if it is unable to satisfy the requirements such as
stock control. If the process does not have conditions to be satisfied or crashes eventually, then rollback starts.

```java
@Transactional
```

It is used with only `public` methods. When it is used on public method, behind the scenes a proxy class is created. The
public method got wrapped up with additional functionality. It acts like a guard. When the function starts to run,
then it opens the transaction. The transaction gets closed when the function gets executed successfully. If it receives an
error, then the function got roll back, and this avoids business logic errors, and the function works more precisely.

````xml
<dependencies>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
    </dependency>
</dependencies>
````
These libraries are required for JWT Token manipulation.


```bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
```

This one line code is used to create Secret key.

## Quick Reminder
In the case of API services, we should also document our “contracts.” This can be accomplished using Swagger/Openapi/Postman collections and contract tests,
according to the Trendyol Tech.

## 4 Common mistakes while building Java Spring Boot applications
Spring is very good at showing error. Think about the use cases of these annotations. Do not overuse them.
Inefficient management of application.yml.
Exception handling. Using specific exceptions will be helpful for debugging and giving feedback to the user.
@GlobalExceptionHandling is another approach, service by service expection handling is good thinking for bigger
projects.

Normal Throw error structure is not good for the frontend hence it is 500 coded for every error.

Request goes to the controller, then goes to the service, and the service makes the transformations for
specified endpoint that is executed by the frontend. So, the exception is handled on service and goes to 
the controller again.


### Exception Handling
Exception handler is kind of listener and detects whether we have an exception on them for the controller or controllers.
It is not mandatory to catch errors. We have to capture the handled exceptions. When it receives exception,
it gives feedback to the user. Instead of giving report, it returns comprehensive error message.

```java
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler{
    
}
```

This annotation will help us to catch all of the exceptions thrown and send necesarry feedbacks to the frontend for
every service.