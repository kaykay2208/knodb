# knodb

**Overview:**
This project presents a Java-based key-value store system that utilizes Apache Kafka as its storage engine. The system is designed to offer efficient storage and retrieval of key-value pairs while leveraging Kafka's log compaction feature for data retention. Communication between clients and the server is facilitated through gRPC, ensuring fast and reliable interaction.

**Key Components and Design:**

1. **Kafka Storage Engine:** Kafka serves as the underlying storage engine, leveraging its log compaction feature to efficiently store and manage key-value data. Log compaction ensures that only the latest value for each key is retained, reducing storage overhead and simplifying data retrieval.

2. **gRPC Communication:** gRPC is employed for communication between clients and the server. This choice offers several advantages, including:
    - **Efficiency:** gRPC uses Protocol Buffers for serialization, resulting in compact and efficient message exchange over the network.
    - **Bi-directional Streaming:** gRPC supports streaming RPCs, enabling real-time communication between clients and the server, which is beneficial for scenarios requiring continuous data updates.
    - **Strong Typing:** gRPC generates strongly-typed client and server code from the service definition, ensuring type safety and reducing the likelihood of communication errors.

3. **Offset Management:** The system stores Kafka offsets in memory along with the corresponding keys. This approach enhances performance by eliminating the need for frequent disk access during read operations, as offsets can be retrieved directly from memory.

4. **Worker Thread for Synchronization:** A dedicated worker thread is responsible for synchronizing in-memory data with disk storage. This ensures that changes made to the key-value store are persisted to disk in a timely manner, providing durability and fault tolerance.

5. **Server Restart Handling:** In the event of a server restart, the system synchronizes data from disk to memory, ensuring that the key-value store remains consistent across restarts.

**Request Handling:**
The key-value store supports both `GET` and `PUT` requests, allowing clients to retrieve the value associated with a specific key or update the value of an existing key, respectively. Requests are processed asynchronously to minimize latency and maximize throughput.

**Future Goals:**
In future iterations, the project aims to incorporate additional functionality, such as support for creating and altering tables. This expansion would enable more complex data modeling and manipulation within the key-value store, further enhancing its utility and flexibility.

**Building and Packaging:**

To build the project, run the following command:

```bash
./gradlew build
./gradlew createZip


