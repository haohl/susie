# Content

## Table of Contents

- [About](#about)
- [Getting Started](#getting_started)
- [Usage](#usage)
- [Contributing](../CONTRIBUTING.md)

## About <a name = "about"></a>

**Content** is a module of the Susie framework, used to handle scenarios where things that "contain content" (such as images, contracts, etc.) need to be saved in the database for management.


## Getting Started <a name = "getting_started"></a>

Just need to add the Jar file to your classpath. You can do this using Maven or Gradle.

### Installing

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

To manage a 'content holder' in database, what you need to do is just implement the ```ContentHolder``` interface, add your own properties, and then you can use ContentService to persist it into database.
