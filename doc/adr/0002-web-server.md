# 2. web-server

Date: 2023-03-08

## Status

Accepted

## Context

As we're building a web-application we need to chose a web-server to serve our assests from. 

This project is meant to be used as a way to evaluate the usefulness of different technologies for avisi-apps by comparing them on several metrics. One of them is performance. As web-servers differ in performance, the choice for one influences the performance of the application as a whole. This project intends to measure the perfomance of other parts of the stack, so it's key we use the same web-server in all prototypes to get any meaningful results. 

## Considered

Ring-jetty

## Decision

There really only was one sensical choice. At the time of writing one of the considered technologies, electric, requires jetty (see [slack-archive] (https://clojurians.slack.com/archives/C7Q9GSHFV/p1676710002591129?thread_ts=1676703159.659469&cid=C7Q9GSHFV)). In theory it would be possible to use jetty without the ring-wrapper via direct interop, but ring is very convenient and widely recommended as a Clojure web-server even if there are no extra constraints like in this project (e.g. see [Eric Nordmand blog-post on Clojure web-servers](https://ericnormand.me/mini-guide/clojure-web-servers)).

## Consequences

By chosing ring-jetty as a web-server we have a convenient setup that can be use for all considered technologies, enabeling us to make meaningful measurements regarding performance.
