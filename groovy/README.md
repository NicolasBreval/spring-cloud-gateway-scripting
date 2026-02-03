# Groovy Scripting Filter implementation
This is the most native implementation of the scripting filter, because Groovy is a JVM-compatible language, so, the Java types are fully compatible with this language. The configuration of this type of filter is like explained on the project's README.

## Example
This is an example of how to configure the filter for Groovy implementation:

```yml
x-groovy-filter: &groovy-filter
  name: GroovyScripting
  args:
    scriptOrPath: |
      logger.info(request.getHeader("Authorization"))
      if (request.getFirstHeader("Authorization") != "MY_SECRET_KEY") {
        response.consume(401, "Unauthorized")
      }
      request


spring:
  application.name: demo
  cloud.gateway.server.webflux.routes:
    - id: route_1
      predicates:
        - Path=/api/todos
      uri: https://jsonplaceholder.typicode.com
      filters:
        - RewritePath=/api/todos, /todos
        - *groovy-filter
```

In the example, the user checks if the request has the required Authorization header with "MY_SECRET_KEY" value.