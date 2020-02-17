Shows how fast you can transmit data between client and server using different protocols:

- Apache HttpClient, blocking (a)
- HTTP, reactive way (h)
- Event Stream using HTTP, aka Server-Sent Events (e)
- RSocket (r)

Implemented in Java with Spring Framework and Project Reactor.

## Preparation

Start the server:
```
cd rsocket-server
./gradlew build
java -jar build/libs/rsocket-server.jar
```

See different formats:

'traditional' HTTP with JSON
```
curl http://localhost:8080/all?max=2
```

Server-Sent Events
```
curl 'http://localhost:8080/all-stream?delay=500&max=10'
```

RSocket (download https://github.com/making/rsc)
```
./rsc tcp://localhost:9898 --stream --route count.all.0.5  --debug
./rsc tcp://localhost:9898 --stream --route count.all.0.5 --dmt application/cbor
```

## Observing interactions

```
cd rsocket-client
./gradlew build
java -jar build/libs/rsocket-client.jar [protocol] [delay] [server delay] [scenario]
```

### Passing elements as fast as possible

- Reactive HTTP just as slow as blocking HTTP.
- Faster: HTTP event stream and RSocket.

```
java -jar build/libs/rsocket-client.jar h 70 100
a 70 100
e 70 100
r 70 100
```

Uncomment `limitRate` in `CountProcessor` in client to see how RSocket passes demand over network.

### Finding

- Non-streaming HTTP: we wait until server generates everything.
- RSocket: live results, cancels server processing when enough.

```
h 0 50 find
r 0 50 find
```

### Aggregating vs. reducing

If you collect all results before you do your work, non-streaming HTTP and RSocket are equally slow.
With a better algorithm (reduction) you begin to see a difference.

```
h 100 100 aggre
r 100 100 aggre
h 100 100 reduc
r 100 100 reduc
```

## Performance

[Results and charts](https://docs.google.com/spreadsheets/d/1qbOcMCGDOWLkRAWcLlnecCV8FHJBOnmdQNTIVw31HE0/edit?usp=sharing)

Restart server after each client run.

```
java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:+AlwaysPreTouch -Xms2g -Xmx2g -Dspring.profiles.active=default,performance -jar build/libs/rsocket-server.jar
java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:+AlwaysPreTouch -Xms1g -Xmx1g -Dspring.profiles.active=default,performance -jar build/libs/rsocket-client.jar h | grep --invert-match 'warmup' | grep 'Finished in' | awk '{print $4}'
java -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC -XX:+AlwaysPreTouch -Xms1g -Xmx1g -Dspring.profiles.active=default,performance -jar build/libs/rsocket-client.jar h | grep --invert-match 'warmup' | grep 'First element' | awk '{print $5}'
```
