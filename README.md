# ![Javalin Commons](javalin-logo.png)
> ### Javalin Commons
Built with **Kotlin** 
# How it works
The classes were made to help javalin development, built with:
  - [Kotlin](https://github.com/JetBrains/kotlin) as programming language
  - [Javalin](https://github.com/tipsy/javalin) as web framework
  - [Gson](https://github.com/google/gson) serialization/deserialization library
  - [Exposed](https://github.com/JetBrains/Exposed) as Sql framework to persistence layer

#### Structure
      + i18n/
        + internationalization
      + utils/
          Extension functions
          ResultHandlers class - Success(T) where T is a class representation of the value or  Failure(errMsg, err) with errMsg as error message and err as throwable
          Pageable class - pagination class
          GsonUtils - serialization/deserialization helper
          HttpFuel - Http client 

# Getting started

You need just JVM installed. (`jdk8`)

You can improve the content and test locally with:
> mvn clean install


```
Made by @felipewom
```