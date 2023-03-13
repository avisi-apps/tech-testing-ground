# 1. clojure-htmx-wrapper

Date: 2023-03-08

## Status

Pending

## Context

HTMX isn't a clojure specific technology, but it mainly works by adding tags to regular html and making sure the expected end-points exist on the back-end (see [htmx in a nutshell](https://htmx.org/docs/#introduction), this can be done from any stack. A clojure-wrapper exists which aims to make working with htmx more convenient by doing the heavy-lifting regarding the generation of the routes and correct id's etc. (see [ctmx basic usage](https://github.com/whamtet/ctmx#usage)).

## Considered

Pure htmx, ctmx (clojure-wrapper)

## Considerations

The impression is that htmx needs a lot of endpoints to work properly, as it uses these to enable incremental updates in a hyper-media-driven way. Creating and maintaining these endpoints and making sure all the html-elements have and communicate the correct id's could be time-consuming and tedious. ctmx seems realy convenient for this and integrates well with the language idioms. 

On the other hand could using a wrapper obfuscate what work is done by the wrapper and what by htmx itself. As the main goal of this project is gaining insight into the technologies it should be considered to use it in it's pure form even if it's just for educational-purposes. Then again, part of the goal of gaing insight into the technologies is about finding out how well they integrate with the current stack, which would include looking for usefull clojure-wrappers.

## Decision

ctmx

## Consequences

By using ctmx as a clojure-wrapper around htmx we have a convenient setup that does a lot of the heavy-lifting for working with htmx in a clojure-idiomatic way, which gives a realistic idea of what working with it in a real project would be like. The wrapper doesn't seem to obfuscate the inner-workings of htmx too much, so using it won't interfer with the educational goals of this project.
