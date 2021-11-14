## Volcano campsite microservice API performance document

### jmeter testing

Summary report:

![summary](summary.png)

Table result:

![table](table.png)



### Metrics with Prometheus

API metrics help to understand the operational performance of APIs. It can indicate digital business health and allow businesses to take immediate corrective action if an important app is down. It can help determine which new services are performing well and which aren’t.


Prometheus is an open-source monitoring system that was originally built by SoundCloud. It consists of the following core components:

A data scraper that pulls metrics data over HTTP periodically at a configured interval.

A time-series database to store all the metrics data.

A simple user interface where you can visualize, query, and monitor all the metrics.

- In the volcano campsite, it enabled light-4j micrometer-registry-prometheus dependency:

```yaml
        <dependency>
        <groupId>com.networknt</groupId>
        <artifactId>prometheus</artifactId>
        <version>${version.light-4j}</version>
        </dependency>
```
 
And enable prometheus middlewire handler on handler.yml file:

```text
handlers:
  - com.networknt.metrics.prometheus.PrometheusHandler@prometheus
  - com.networknt.metrics.prometheus.PrometheusGetHandler@getprometheus
  
chains:
  default:
    - exception
    #- metrics
    - prometheus  
  
  - path: '/prometheus'
    method: 'get'
    exec:
      - getprometheus  
```

The prometheus metrics enable on:

URL: http://localhost:8080/prometheus

Below is the sample prometheus report:
```text
# HELP jvm_classes_loaded The number of classes that are currently loaded in the JVM
# TYPE jvm_classes_loaded gauge
jvm_classes_loaded 5201.0
# HELP jvm_classes_loaded_total The total number of classes that have been loaded since the JVM has started execution
# TYPE jvm_classes_loaded_total counter
jvm_classes_loaded_total 5201.0
# HELP jvm_classes_unloaded_total The total number of classes that have been unloaded since the JVM has started execution
# TYPE jvm_classes_unloaded_total counter
jvm_classes_unloaded_total 0.0
# HELP requests_total requests_total
# TYPE requests_total counter
requests_total{endpoint="/campsite@get",clientId="unknown",} 1.0
requests_total{endpoint="/campsite@post",clientId="unknown",} 1.0
# HELP process_cpu_seconds_total Total user and system CPU time spent in seconds.
# TYPE process_cpu_seconds_total counter
process_cpu_seconds_total 9.640625
# HELP process_start_time_seconds Start time of the process since unix epoch in seconds.
# TYPE process_start_time_seconds gauge
process_start_time_seconds 1.636848477162E9
# HELP jvm_gc_collection_seconds Time spent in a given JVM garbage collector in seconds.
# TYPE jvm_gc_collection_seconds summary
jvm_gc_collection_seconds_count{gc="G1 Young Generation",} 3.0
jvm_gc_collection_seconds_sum{gc="G1 Young Generation",} 0.034
jvm_gc_collection_seconds_count{gc="G1 Old Generation",} 0.0
jvm_gc_collection_seconds_sum{gc="G1 Old Generation",} 0.0
# HELP jvm_info JVM version info
# TYPE jvm_info gauge
jvm_info{version="11.0.11+9-LTS-194",vendor="Oracle Corporation",runtime="Java(TM) SE Runtime Environment",} 1.0
# HELP jvm_buffer_pool_used_bytes Used bytes of a given JVM buffer pool.
# TYPE jvm_buffer_pool_used_bytes gauge
jvm_buffer_pool_used_bytes{pool="mapped",} 1.38154803E8
jvm_buffer_pool_used_bytes{pool="direct",} 131072.0
# HELP jvm_buffer_pool_capacity_bytes Bytes capacity of a given JVM buffer pool.
# TYPE jvm_buffer_pool_capacity_bytes gauge
jvm_buffer_pool_capacity_bytes{pool="mapped",} 1.38154803E8
jvm_buffer_pool_capacity_bytes{pool="direct",} 131072.0
# HELP jvm_buffer_pool_used_buffers Used buffers of a given JVM buffer pool.
# TYPE jvm_buffer_pool_used_buffers gauge
jvm_buffer_pool_used_buffers{pool="mapped",} 1.0
jvm_buffer_pool_used_buffers{pool="direct",} 8.0
# HELP response_time_seconds response_time_seconds
# TYPE response_time_seconds summary
response_time_seconds_count{endpoint="/campsite@get",clientId="unknown",} 1.0
response_time_seconds_sum{endpoint="/campsite@get",clientId="unknown",} 0.1250344
response_time_seconds_count{endpoint="/campsite@post",clientId="unknown",} 1.0
response_time_seconds_sum{endpoint="/campsite@post",clientId="unknown",} 0.0829096
# HELP jvm_memory_bytes_used Used bytes of a given JVM memory area.
# TYPE jvm_memory_bytes_used gauge
jvm_memory_bytes_used{area="heap",} 7.477728E7
jvm_memory_bytes_used{area="nonheap",} 4.8414648E7
# HELP jvm_memory_bytes_committed Committed (bytes) of a given JVM memory area.
# TYPE jvm_memory_bytes_committed gauge
jvm_memory_bytes_committed{area="heap",} 4.00556032E8
jvm_memory_bytes_committed{area="nonheap",} 5.1970048E7
# HELP jvm_memory_bytes_max Max (bytes) of a given JVM memory area.
# TYPE jvm_memory_bytes_max gauge
jvm_memory_bytes_max{area="heap",} 6.400507904E9
jvm_memory_bytes_max{area="nonheap",} -1.0
# HELP jvm_memory_bytes_init Initial bytes of a given JVM memory area.
# TYPE jvm_memory_bytes_init gauge
jvm_memory_bytes_init{area="heap",} 4.00556032E8
jvm_memory_bytes_init{area="nonheap",} 7667712.0
# HELP jvm_memory_pool_bytes_used Used bytes of a given JVM memory pool.
# TYPE jvm_memory_pool_bytes_used gauge
jvm_memory_pool_bytes_used{pool="CodeHeap 'non-nmethods'",} 1243776.0
jvm_memory_pool_bytes_used{pool="Metaspace",} 3.5675432E7
jvm_memory_pool_bytes_used{pool="CodeHeap 'profiled nmethods'",} 6199552.0
jvm_memory_pool_bytes_used{pool="Compressed Class Space",} 3837840.0
jvm_memory_pool_bytes_used{pool="G1 Eden Space",} 6.815744E7
jvm_memory_pool_bytes_used{pool="G1 Old Gen",} 4522688.0
jvm_memory_pool_bytes_used{pool="G1 Survivor Space",} 2097152.0
jvm_memory_pool_bytes_used{pool="CodeHeap 'non-profiled nmethods'",} 1458048.0
# HELP jvm_memory_pool_bytes_committed Committed bytes of a given JVM memory pool.
# TYPE jvm_memory_pool_bytes_committed gauge
jvm_memory_pool_bytes_committed{pool="CodeHeap 'non-nmethods'",} 2555904.0
jvm_memory_pool_bytes_committed{pool="Metaspace",} 3.6438016E7
jvm_memory_pool_bytes_committed{pool="CodeHeap 'profiled nmethods'",} 6225920.0
jvm_memory_pool_bytes_committed{pool="Compressed Class Space",} 4194304.0
jvm_memory_pool_bytes_committed{pool="G1 Eden Space",} 7.4448896E7
jvm_memory_pool_bytes_committed{pool="G1 Old Gen",} 3.24009984E8
jvm_memory_pool_bytes_committed{pool="G1 Survivor Space",} 2097152.0
jvm_memory_pool_bytes_committed{pool="CodeHeap 'non-profiled nmethods'",} 2555904.0
# HELP jvm_memory_pool_bytes_max Max bytes of a given JVM memory pool.
# TYPE jvm_memory_pool_bytes_max gauge
jvm_memory_pool_bytes_max{pool="CodeHeap 'non-nmethods'",} 5898240.0
jvm_memory_pool_bytes_max{pool="Metaspace",} -1.0
jvm_memory_pool_bytes_max{pool="CodeHeap 'profiled nmethods'",} 1.2288E8
jvm_memory_pool_bytes_max{pool="Compressed Class Space",} 1.073741824E9
jvm_memory_pool_bytes_max{pool="G1 Eden Space",} -1.0
jvm_memory_pool_bytes_max{pool="G1 Old Gen",} 6.400507904E9
jvm_memory_pool_bytes_max{pool="G1 Survivor Space",} -1.0
jvm_memory_pool_bytes_max{pool="CodeHeap 'non-profiled nmethods'",} 1.2288E8
# HELP jvm_memory_pool_bytes_init Initial bytes of a given JVM memory pool.
# TYPE jvm_memory_pool_bytes_init gauge
jvm_memory_pool_bytes_init{pool="CodeHeap 'non-nmethods'",} 2555904.0
jvm_memory_pool_bytes_init{pool="Metaspace",} 0.0
jvm_memory_pool_bytes_init{pool="CodeHeap 'profiled nmethods'",} 2555904.0
jvm_memory_pool_bytes_init{pool="Compressed Class Space",} 0.0
jvm_memory_pool_bytes_init{pool="G1 Eden Space",} 2.7262976E7
jvm_memory_pool_bytes_init{pool="G1 Old Gen",} 3.73293056E8
jvm_memory_pool_bytes_init{pool="G1 Survivor Space",} 0.0
jvm_memory_pool_bytes_init{pool="CodeHeap 'non-profiled nmethods'",} 2555904.0
# HELP success_total success_total
# TYPE success_total counter
success_total{endpoint="/campsite@get",clientId="unknown",} 1.0
success_total{endpoint="/campsite@post",clientId="unknown",} 1.0
# HELP jvm_threads_current Current thread count of a JVM
# TYPE jvm_threads_current gauge
jvm_threads_current 27.0
# HELP jvm_threads_daemon Daemon thread count of a JVM
# TYPE jvm_threads_daemon gauge
jvm_threads_daemon 7.0
# HELP jvm_threads_peak Peak thread count of a JVM
# TYPE jvm_threads_peak gauge
jvm_threads_peak 27.0
# HELP jvm_threads_started_total Started thread count of a JVM
# TYPE jvm_threads_started_total counter
jvm_threads_started_total 31.0
# HELP jvm_threads_deadlocked Cycles of JVM-threads that are in deadlock waiting to acquire object monitors or ownable synchronizers
# TYPE jvm_threads_deadlocked gauge
jvm_threads_deadlocked 0.0
# HELP jvm_threads_deadlocked_monitor Cycles of JVM-threads that are in deadlock waiting to acquire object monitors
# TYPE jvm_threads_deadlocked_monitor gauge
jvm_threads_deadlocked_monitor 0.0
# HELP jvm_threads_state Current count of threads by state
# TYPE jvm_threads_state gauge
jvm_threads_state{state="RUNNABLE",} 23.0
jvm_threads_state{state="WAITING",} 1.0
jvm_threads_state{state="TERMINATED",} 0.0
jvm_threads_state{state="NEW",} 0.0
jvm_threads_state{state="BLOCKED",} 0.0
jvm_threads_state{state="TIMED_WAITING",} 3.0
# HELP jvm_memory_pool_allocated_bytes_total Total bytes allocated in a given JVM memory pool. Only updated after GC, not continuously.
# TYPE jvm_memory_pool_allocated_bytes_total counter
```

-  Setting up Prometheus and importing the metric:

From API root folder start Prometheus docker-compose:

```yaml
docker-compose -f   docker-compose-prometheus.yml up
```
The docker-compose will start three docker images:

- campsite service will start on port 8080

- Prometheus will start on 9090 port:
  
  http://localhost:9090/

- grafana start on 3000 port

We can start to run jmeter test and monitor the API by prometheus metrics.

For Example: CPU usage:

![cpu](cpu.png)

