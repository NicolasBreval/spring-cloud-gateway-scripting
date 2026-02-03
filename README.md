# Spring Cloud Gateway Scripting
Project to implement a custom Spring Cloud Gateway filter used to modify the input HTTP request using a custom script.

The main idea of this project is to allow developers to make a customized Spring Cloud Gateway for their projects and avoid to develop each customized filter required for each project. With this library, a developer can simply add the library to its gateway project and add, by configuration, an script which can modify the requests received by the gateway and take decisions based on the request content.

## Requirements
* Java: 25+
* Spring Boot: 3.5.x
* Spring Cloud: 2025.0.x

## Versioning
This library used the classic three-numbers version format *Major.Minor.Patch*. The major number indicates the new version has some changes that are incompatible with previous versions. The minor number indicates the version has some new features, but are compatible with previous one. The last patch numer indicates the version has some fixes without new features.

## Usage
This set of libraries are published using [jitpack](https://jitpack.io). Jitpack is an artifact repository for Java projects, free for public GitHub repositories. It's similar to maven repository, but requires manual actions to publish the libraries.

To use any library from this project, you must to add the jitpack repository in your repositories section, then, you can add the dependency of the library you must. There are some examples of how to import the library:

### For Maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.NicolasBreval</groupId>
	    <artifactId>Library</artifactId>
	    <version>Tag</version>
    </dependency>
</dependencies>
```

### For Gradle (Groovy)
```groovy
repositories {
    maven { url = 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.NicolasBreval.spring-cloud-gateway-scripting:Library:Tag'
}
```

### For Gradle (Kotlin)
```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.NicolasBreval.spring-cloud-gateway-scripting:Library:Tag")
}
```

When the library is added to your Spring Cloud Gateway project, you can to add a filter in your application.yml file. There are one filter by each scripting language implemented, but all implementations are all configured the same, using a name, that is different for each scripting language, and one argument called *scriptOrPath*, which can be a multi-line string containing the script to run, a path to a file on the system, or a classpath path to a file in the resource folder of the project. This set of examples shows the Groovy implementation, but is the same for the other languages:

#### Context
It's important to known the context of the script, this is, all elements injected to the script when it's running. The filter injects three variables that can be called inside the script:

* request: This is an object of type [RequestWrapper](./core/src/main/java/org/nbreval/spring/cloud/gateway/scripting/core/util/http/RequestWrapper.java), which is a wrapper type used to make easy modify some elements of the real request and also protects it by limiting the access. Whis this object you can:
    * Obtain all headers, as a multi-valued map, using *request.getHeaders()*
    * Obtain all values of a header, as a list, using *request.getHeader("X-MyHeader")*
    * Obtain the first value of a header, as a string, using *request.getFirstHeader("X-MyHeader")*
    * Add or update a header, with one or multiple values, using *request.setHeader("X-MyHeader", "value1", "value2")*
    * Remove a header, using *request.removeHeader("X-MyHeader")*
    * Obtain all query params, as a multi-valued map, using *request.getQueryParams()*
    * Obtain all values of a query param, as a list, using *request.getQueryParam("my_param")*
    * Obtain the first value of a query param, as a string, using *request.getFirstQueryParam("my_param")*
    * Add or update a query param, with one or multiple values, using *request.setQueryParam("my_param")*
    * Get all claims, if authorization header has a valid JWT token, as a map with string as keys and objects as values, using *request.getClaims()*
    * Get a claim by its path, if authorization header has a valid JWT token, as an object, using *request.getClaim("path.to.my.claim.value")*

* response: Is a consumer object used to stop the request processing and return a custom HTTP response with a specified HTTP code and message. To use it, you must to invoke the variable like this:

```groovy
response.consume(401, "Unauthorized")
```

* logger: Is a Logger object, used to show some information of the request, or make easy debug your script. The logger is generated using Spring's logging libraries, so the level of the log is limited by your application. To use it, simply invoke the logger object like in Java:

```groovy
logger.info("This is an info log")
logger.error("This is an error log")
```

### Example of inline configuration
```yml
spring.cloud.gateway.server.webflux.routes:
    - id: route_1
      predicates:
        - Path=/api/todos
      uri: https://jsonplaceholder.typicode.com
      filters:
        - RewritePath=/api/todos, /todos
        - name: GroovyScripting
          args: 
            scriptOrPath: |
                logger.info(request.getHeader("Authorization"))
                if (request.getFirstHeader("Authorization") != "MY_SECRET_KEY") {
                    response.consume(401, "Unauthorized")
                }
                request
```

### Example of path-based configuration
```yml
spring.cloud.gateway.server.webflux.routes:
    - id: route_1
      predicates:
        - Path=/api/todos
      uri: https://jsonplaceholder.typicode.com
      filters:
        - RewritePath=/api/todos, /todos
        - name: GroovyScripting
          args: 
            scriptOrPath: /route/to/your/script
```

### Example of classpath-based configuration
```yml
spring.cloud.gateway.server.webflux.routes:
    - id: route_1
      predicates:
        - Path=/api/todos
      uri: https://jsonplaceholder.typicode.com
      filters:
        - RewritePath=/api/todos, /todos
        - name: GroovyScripting
          args: 
            scriptOrPath: classpath:/route/to/your/script
```

## Implementations
This is the list of all implementations, each one with a different scripting language:

* [Groovy](./groovy/README.md)