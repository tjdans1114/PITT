# PITT

![logo](Logo.png)


## Documents
watch `/Documents`

## Readings
Refer to Issue #1

Need to study Thread...

## TODOs : tentative
* Step 1
  - ~~implementing basic server-client model~~
  - ~~Event Queue~~
  - ~~HTTP parser : until 10/22~~
* Step 2
  - ~~Event definition: until 10/24~~
  - HTTP response composition & Interpreter
  - Event Loop Implementation
  - Main server execution?
* Step 3
  - Cacheing
  - Benchmark

## Implementation TODO

## Summary
* `HTTPParser` : parse HTTP request string(buffer) into `Event`. preprocess into `IO` or `NON_IO`
* `Event` : `IO` or `NON_IO`.
* HTTPResponse : interpret parsed HTTP Request & return HTTP Response
  - contained in main thread, and supplementary Thread Pool
  - return HTTP Response to the client

* `EventLoop` : Event Loop Architecture. exploits `HTTPParser`, `HTTPResponse`
  - contains EventQueue.
    + EventQueue : simple Non-blocking Queue (ADT) that contains `Event`
  - if dequeued NON_IO `Event`
    + dispatch the Event to the Thread Pool (Worker Thread).
    + HTTP Response is returned to client by Worker Thread.
  - if dequeued IO `Event`,
    + main thread processes HTTP Response to the client.
