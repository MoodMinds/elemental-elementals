# Implementation of [Elemental](https://github.com/MoodMinds/elemental) interfaces

This project provides complete base implementations of the [Elemental](https://github.com/MoodMinds/elemental) interfaces.

## Key Notes

Provided direct **Container** and **Collection** implementations, such as **HashContainer** and **HashCollection**
(with elements' order preserved via **LinkHashContainer** and **LinkHashCollection**), **TreeContainer** and **TreeCollection**
as **NavigableContainer** and **NavigableCollection** and handling duplicate elements effectively.

Mutable interfaces are implemented by wrapping their counterparts from the Java Development Kit (JDK) - **WrapSet**,
**WrapSortedSet**, **WrapNavigableSet**, **WrapQueue**, **WrapDeque**, **WrapBlockingQueue**, **WrapBlockingDeque**,
**WrapList**, **WrapMap**, **WrapSortedMap**, **WrapNavigableMap**, **WrapConcurrentMap**, **WrapConcurrentNavigableMap**,
as well as the direct **Association** immutable implementations - **WrapAssociation**, **WrapSortedAssociation**,
**WrapNavigableAssociation**. They all utilize the power of the existing Java Collection Framework.

Notably, the wrapping implementation of the **Map** interface (**WrapMap**) exhibits such a behaviour in its `values()` method
when it returns instances of **Collection**, which can be equal to those from another **Map**s if they genuinely contain the
same values in the same quantity.

The structural implementations of the **Tuple*** interfaces are present as well, utilizing arrays internally to efficiently store values.

## Getting Started

Include **Elementals** in your project by adding the dependency.

## Maven configuration

Artifacts can be found on [Maven Central](https://search.maven.org/) after publication.

```xml
<dependency>
    <groupId>org.moodminds.elemental</groupId>
    <artifactId>elemental-elementals</artifactId>
    <version>${version}</version>
</dependency>
```

## Building from Source

You may need to build from source to use **Elementals** (until it is in Maven Central) with Maven and JDK 1.8 at least.

## License
This project is going to be released under version 2.0 of the [Apache License][l].

[l]: https://www.apache.org/licenses/LICENSE-2.0