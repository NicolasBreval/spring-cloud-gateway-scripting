# Spring Cloud Gateway Scripting
Project to implement a custom Spring Cloud Gateway filter used to modify the input HTTP request using a custom script.

The main idea of this project is to allow developers to make a customized Spring Cloud Gateway for their projects and avoid to develop each customized filter required for each project. With this library, a developer can simply add the library to its gateway project and add, by configuration, an script which can modify the requests received by the gateway and take decisions based on the request content.

## Requirements

Java: 25+
Spring Boot: 3.5.x
Spring Cloud: 2025.0.x

## Allowed scripting languages

All scripting languages have similar rules to use it:

1. Scripts have an input context, this is, a set of arguments that are mapped as global variables. This context only has a single argument called *request*, that has multiple methods which allows developer to modify the original request. To see the available methods, see the [RequestWrapper](./core/src/main/java/org/nbreval/spring/cloud/gateway/scripting/core/util/http/RequestWrapper.java).

2. The script must return a valid *RequestWrapper* object, because is the request to be returned by the filter. In all script languages it's sufficient to put the name of *request* argument at the end of the script.

3. With the arguments context, also is injected a consumer called response. This consumer allows to break the request processing flow and return a custom HTTP response, with a status code and a reason as a text.


### Groovy
This is the most efficient solution, because Groovy is a JVM-family language, so, it's like use native Java code.