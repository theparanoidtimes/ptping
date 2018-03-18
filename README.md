# ptping

Version: 1.0-SNAPSHOT

**ptping** is a Java application designed as a "wrapper around the *ping* command".
This is not a Java ICMP implementation. Application executes *ping* command and
wraps the response in an object. This object can later be queried for any data
that is normally presented as a *ping* output.

## Usage

Java 8 or above is required and it can be run on Windows and Unix systems.

Commands are executed by `PingCommandRunner` class and `PingResult` is the result
object.

To execute a ping command:

```java
PingResult pingResult = PingCommandRunner.executePing("google.com");

System.out.println(pingResult.getCompleteOutput());

// Pinging google.com [172.217.16.110] with 32 bytes of data:
// Reply from 172.217.16.110: bytes=32 time=14ms TTL=57
// Reply from 172.217.16.110: bytes=32 time=14ms TTL=57
// Reply from 172.217.16.110: bytes=32 time=24ms TTL=57
// Reply from 172.217.16.110: bytes=32 time=11ms TTL=57
// Reply from 172.217.16.110: bytes=32 time=13ms TTL=57
// Ping statistics for 172.217.16.110:
// Packets: Sent = 5, Received = 5, Lost = 0 (0% loss),
// Approximate round trip times in milli-seconds:
// Minimum = 11ms, Maximum = 24ms, Average = 15ms
```

The resulted object can be queried for data later:

```java
System.out.println(pingResult.isSuccessful());
// true

System.out.println(pingResult.getPingTarget());
// google.com

System.out.println(pingResult.getTargetIp());
// 172.217.16.110
```

Each ping response line can be queried also:

```java
pingResult.getAttemptLines().forEach(attemptLine ->
                System.out.println(attemptLine.getTime()));
// 14.0
// 14.0
// 24.0
// ...
```

Also you can query multiple targets at the same time:

```java
List<PingResult> pingResults = PingCommandRunner.executePingToTargets("yahoo.com", "hotmail.com");
// query each file as needed...
```

Check out this [example test](/src/test/java/org/theparanoidtimes/ptping/testrunner/TestRunner.java) for more.

## License

MIT License

Copyright (c) 2017 Dejan Josifović, the paranoid times

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
