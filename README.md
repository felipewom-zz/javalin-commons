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
          ResultHandlers<T> class - Success(T) where T is a class representation of the value or  Failure(errMsg, err) with errMsg as error message and err as throwable
          Pageable class - pagination class
          GsonUtils - serialization/deserialization helper
          HttpFuel - Http client 

# Getting started

You need add dependency. 

```
<dependency>
    <groupId>com.github.felipewom</groupId>
    <artifactId>javalin-commons</artifactId>
    <version>1.0.0−RC2</version>
</dependency>
```

You can improve the content and test locally with:
> mvn clean install

Promote version and deploy
> make deploy version=1.0.0−RC6

```
Made by @felipewom
```