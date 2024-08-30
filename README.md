# Java Spring Boot

It is a Java Framework that allow developers to create stand-alone, productive based applications. It is easy to run.
The framework does not need to deploy WAR files mandatory.
"Starter" dependencies are provided to have simplified build configuration.
Spring Boot configures Spring automatically and other third party libraries.
There is no need for XML configuration and no code generation.

`https://start.spring.io` allow developers to use specified Spring Boot version and satisfy requirements.

## Project Creation

![Project.png](readme_images%2FProject.png)

Figure: `start.spring.io` interface for beginning of the project.

### Meaning of Project
The choice between Gradle and Maven depends on your project’s specific needs and your team’s preferences.
Maven is used for more structured project, and Gradle is an option for more flexible projects. I preferred maven in this project.

### Meaning of Language
I wanted to build a Spring Boot application with Java programming language, so my choice is Java.


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

As a developer which use Maven, you can use `Project Lombok` with adding it to pom.xml file just like this.

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
This libraries are required for JWT Token manipulation.


````properties
spring.datasource.url=jdbc:postgresql://localhost:5433/bites_crud
spring.datasource.username=postgres
spring.datasource.password=12345
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA and Hibernate configuration (if you're using Spring Data JPA)
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
# 1h in millisecond
security.jwt.expiration-time=3600000
# For making security key static
spring.security.user.password=Test12345_
````
``bash
node -e "console.log(require('crypto').randomBytes(32).toString('hex'))"
``
is used to create Secret key.
