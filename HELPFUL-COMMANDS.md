### Test Flink -> Kafka Connection

```bash
docker exec -it <jobmanager_container> /bin/bash

apt-get update && apt-get install -y netcat
nc -vz kafka 9092
```

### Copy Latest Jar to JobManager

```bash
docker cp target/core-0.1.jar sentinel-jobmanager-1:/opt/flink/

docker exec -it sentinel-jobmanager-1 flink run -d -c com.sentinel.DataStreamJob /opt/flink/core-0.1.jar
```
