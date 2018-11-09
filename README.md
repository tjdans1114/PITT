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
  - Event Loop Implementation : until 11/9
  - HTTP response composition & Interpreter : until 11/16
  - Main server execution? : until 11/23
  - debugging : until 11/30
* Step 3
  - Cacheing : until 11/30?
  - Benchmark : until 12/??

## Implementation TODO
  * request parsing도 여러 step에 거쳐서 할 수 있게...
  * interpret가 끝나면 socket 을 writable로 바꿔줘야함
  * Event의 구성 :
    * unprocessed
      * NON_IO : 바로 return 해줄 수 있는 것
      * IO : 바로 return 해줄 수 없는 것
    * on process :
      * CONTINUATION
    * finished
      * FINISHED


## headers
if-modified-since : req header
modified-since : resp header

## todo
404, 등 error response body는 처음 server 실행할 때 static하게 file에서 열어놓을것!
THREADING 할것!!!

## Remark
  * Read Request : from client socket
  * Parse Request : HTTPParser
  * Find Resource : for file request, (In Thread?), determine location of the resource
  * Read File : in Thread, File IO
  * Make Response : after interpreting request, construct response
  * Write Response : HTTP Response to client socket?

## FLOW
in main(event loop),
if readable : parse string. enqueue that into event
if writable : ???
dequeued event case, run HTTPInterpreter. create_response -> respond : socket.write occurs in respond
NON_IO : direct response message return!
IO : open file, etc... then response message. may result in continuation
continuation : file is already open. just write body message. may result in another continuation
finished : done. nothing to process more

MainServer : only reads inputs from client sockets (then enQ to EQ). calls Eventloop.start().
EventLoop : Thread-using class. process events from EQ.dQ


## Summary
* `HTTPParser` : parse HTTP request string(buffer) into `Event`. preprocess into `IO` or `NON_IO`
* `Event` : `IO` or `NON_IO`.
* HTTPInterpreter : interpret parsed HTTP Request & return HTTP Response
  - contained in main thread, and supplementary Thread Pool
  - return HTTP Response to the client

* `EventLoop` : Event Loop Architecture. exploits `HTTPParser`, `HTTPInterpreter`
  - contains EventQueue.
    + EventQueue : simple Non-blocking Queue (ADT) that contains `Event`
  - if dequeued NON_IO `Event`
    + dispatch the Event to the Thread Pool (Worker Thread).
    + HTTP Response is returned to client by Worker Thread.
  - if dequeued IO `Event`,
    + main thread processes HTTP Response to the client.

## Remark
  * DMA : file이 커야 효율 나옴