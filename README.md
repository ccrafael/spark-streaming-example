# spark-streaming-example
A spark streaming example

This projects is used to test the effect of serialization on spark streaming.

Into the producer folder there is a simple Java project that serialize a pojo into a socket.

Into the scala folder there is a simple spark streaming scala project that load the pojos.

The max rate reached at the time of writing was about 220000 Pojos/s.
