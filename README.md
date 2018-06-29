# spark-streaming-example
A spark streaming example

This projects is used to test the effect of serialization on spark streaming.

Into the producer folder there is a simple Java maven project that serialize a pojo into a socket.

Into the scala folder there is a simple spark streaming scala sbt  project that load the pojos.

The pojos are encapsulated into an object that is also serialized. I do that to try to deserialize the final object using the workers. The first serialization could be optimized just writing the length of the payload before the payload. Or just using a real queue like Kafka, rabbitMQ, or another message service.

The max rate reached at the time of writing was about 220000 Pojos/s using protostuff.
