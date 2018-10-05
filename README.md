# PITT

![logo](Logo.png)


## Documents
watch `/Documents`

## Readings
Recommended Readings : Please add!
need to know : Socket, Thread, I/O, 
  + Non-blocking File I/O : 
    * https://en.wikipedia.org/wiki/Non-blocking_I/O_(Java)
    * http://tutorials.jenkov.com/java-nio/index.html
  + IO
    * http://tutorials.jenkov.com/java-io/index.html

[comment]: asdf

## Summary?

### Java NIO 
* currently reading overview
- Channel & Buffer : Data is always read from a channel into a buffer, or written from a buffer to a channel
- Selector : A selector is an object that can monitor multiple channels for events (like: connection opened, data arrived etc.). Thus, a single thread can monitor multiple channels for data. [Role of Event Queue?]
