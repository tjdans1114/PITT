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
  * interpret가 끝나면 socket 을 writable로 바꿔줘야함

## Remark
  * Read Request : from client socket
  * Parse Request : HTTPParser
  * Find Resource : for file request, (In Thread?), determine location of the resource
  * Read File : in Thread, File IO
  * Make Response : after interpreting request, construct response
  * Write Response : HTTP Response to client socket?

## 연구원님 질문거리
  * (file) IO 와 non-IO 를 어떻게 구분할 것인가?
  * Thread에서 client socket으로 (directly) response를 날릴 수 있는가?
  * Threading을 어떻게 하는지???

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
