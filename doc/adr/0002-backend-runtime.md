# 2. backend runtime

Date: 2023-03-08

## Status

Accepted

## Context

As we're building a web-application we need to choose a runtime for our backend.

This project is meant to be used as a way to evaluate the usefulness of different technologies for avisi-apps by comparing them on several metrics. One of them is performance. As runtimes differ in performance, the choice for one influences the performance of the application as a whole. This project intends to measure the perfomance of other parts of the stack, so it's key we use the same runtime in all prototypes to get any meaningful results. 

## Considered

JVM, NodeJS

Clojures main-dialect runs on the JVM, the second most popular dialect is ClojureScript which compiles to JS so can target all js-runtimes (e.g. NodeJS).

Avisi-apps currently targets NodeJS for most apps. As a goal of this project is to show how well the technologies integrate with the current stack it makes sense to target NodeJS in this project aswell.

It's not guaranteed that all clojure related technologies can target all runtimes though. In this case one of the considered technologies, electric, is unable to run on NodeJS, although there are no fundamentals blocks so it might be supported at some point in the future (see [slack-archive](https://clojurians.slack.com/archives/C7Q9GSHFV/p1670936419811549)) 

## Decision

JVM

The goal of being able to meaningfully compare the performance of the technologies is considered more important than targeting the currently most-used runtime. 

## Consequences

By targeting the JVM the performance and resource-utilitations of the technologies can meaningfuly be compared, but the resulting setup is less representative for the currently most-used stack. 
