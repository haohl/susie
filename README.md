# Susie

## Table of Contents

- [About](#about)
- [Getting Started](#getting_started)
- [Usage](#usage)
- [Contributing](../CONTRIBUTING.md)

## About <a name = "about"></a>

**Susie** is a framework-oriented project for JavaEE. In this project, I present commonly used, system-level functionalities in a modular format. Each module in this project corresponds to a system-level functional domain and can be used independently, thereby accelerating the development process of JavaEE projects.

This project also serves as a summary and accumulation of my own development experience. I hope it will help me go further in areas such as architecture and module design.

- [ ] auth 
- [x] content
- [ ] notification
- [ ] rbac


## Getting Started <a name = "getting_started"></a>

Each module is a Java library, except for the 'app' module, which is a demo application. Therefore, to use it, you just need to add the Jar file to your classpath. You can do this using Maven or Gradle.

### Installing

For example, to include the 'content' module, you can do as follows:

If you are using Maven, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>xyz.haofamily.susie</groupId>
    <artifactId>content</artifactId>
    <version>0.0.1</version>
</dependency>
```


If you are using Gradle, add the following to your build.gradle:

```gradle
dependencies {
    implementation 'xyz.haofamily.susie:content:0.0.1'
}
```

## Usage <a name = "usage"></a>

Please refer to the README file in each module.
