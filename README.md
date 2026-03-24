Explanation of the docker-compose.yml File Components
This docker-compose.yml file defines a Docker Compose configuration for setting up a multi-broker Apache Kafka cluster using KRaft mode (Kafka's built-in consensus protocol, replacing ZooKeeper). It creates three Kafka brokers (kafka1, kafka2, kafka3) that act as both brokers and controllers, forming a resilient, distributed cluster. Below, I'll break down each component of the file, explaining its purpose, structure, and key configurations.
1. Version
   version: '3.8'
   Purpose: Specifies the version of the Docker Compose file format. Version 3.8 is a stable release that supports most features for container orchestration.
   Why it's used: Ensures compatibility with Docker Compose features like networks and environment variables. The version field is optional in newer Docker Compose versions but is included here for clarity.
2. Services
   Purpose: Defines the containers (services) that Docker Compose will manage. Each service represents a Kafka broker instance.
   Structure: There are three services (kafka1, kafka2, kafka3), each configured identically except for unique identifiers (e.g., node IDs, ports, and listener configurations).
   Key Sub-components:
   image: confluentinc/cp-kafka:latest
   Specifies the Docker image to use. This is Confluent's official Kafka image, which includes Kafka binaries and is optimized for KRaft mode.
   ports:
   Maps host machine ports to container ports, allowing external access to the Kafka brokers.
   Example for kafka1: "9092:9092" (host port 9092 → container port 9092 for external client connections) and "9093:9093" (host port 9093 → container port 9093 for controller quorum).
   This setup exposes brokers on ports 9092, 9094, and 9096 for clients, and controller ports 9093, 9095, 9097 for internal quorum communication.
   networks:
   Assigns the service to the kafka-network network, enabling inter-container communication (e.g., brokers can reach each other by container names like kafka1).
   environment:
   A list of environment variables passed to the Kafka container, configuring its behavior. These are Kafka-specific settings for KRaft mode.
   Key variables explained below (common across all services unless noted):
   KAFKA_PROCESS_ROLES: broker,controller
   Defines the node's role. Each node acts as both a broker (handles client requests) and a controller (manages metadata and quorum).
   KAFKA_NODE_ID: 1 (unique per service: 1, 2, 3)
   A unique identifier for each node in the cluster.
   KAFKA_CLUSTER_ID: 4L6g3nShT-eMCtK--X86sw
   A shared UUID for the entire cluster. Required for KRaft to initialize and identify the cluster.
   KAFKA_CONTROLLER_QUORUM_VOTERS: 1@kafka1:9093,2@kafka2:9095,3@kafka3:9097
   Lists all controller nodes and their endpoints. Used for Raft consensus (quorum voting) among controllers.
   KAFKA_LISTENERS: INTERNAL://0.0.0.0:29092,EXTERNAL://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
   Defines the network interfaces and ports the broker listens on.
   INTERNAL: For inter-broker communication (port 29092).
   EXTERNAL: For client connections (port 9092).
   CONTROLLER: For controller quorum communication (port 9093).
   0.0.0.0 binds to all interfaces inside the container.
   KAFKA_ADVERTISED_LISTENERS: INTERNAL://kafka1:29092,EXTERNAL://localhost:9092,CONTROLLER://kafka1:9093
   Specifies what addresses the broker advertises to clients and other brokers.
   INTERNAL: Uses container name (e.g., kafka1) for inter-broker traffic.
   EXTERNAL: Uses localhost and host ports for external clients.
   CONTROLLER: Uses container name for quorum.
   This ensures proper routing in a multi-container setup.
   KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,CONTROLLER:PLAINTEXT
   Maps each listener to a security protocol. All use PLAINTEXT (no encryption/authentication for simplicity).
   KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
   Specifies which listener is used for controller communication.
   KAFKA_INTER_BROKER_LISTENER_NAME: INTERNAL
   Defines the listener for inter-broker communication (uses the INTERNAL listener).
   KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
   Sets replication factor for the __consumer_offsets topic (stores consumer group offsets). Set to 3 for high availability.
   KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 2
   Minimum in-sync replicas for transaction state logs.
   KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 3
   Replication factor for transaction state logs.
   KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
   Delays initial consumer group rebalancing (set to 0 for faster startup).
3. Networks
   networks:
   kafka-network:
   driver: bridge
   Purpose: Defines a custom Docker network for the services.
   driver: bridge: Uses Docker's bridge network driver, which allows containers to communicate via their names (e.g., kafka1 resolves to the container's IP).
   Why it's used: Enables secure, isolated inter-container communication without exposing internal ports to the host. Essential for multi-broker setups where brokers need to talk to each other.
   Overall Architecture and Benefits
   KRaft Mode: Eliminates the need for ZooKeeper by using Kafka's built-in Raft consensus for metadata management. Controllers handle leader election and cluster state.
   Multi-Broker Setup: Three brokers provide fault tolerance (can survive the loss of one broker) and load distribution.
   Listener Separation: Uses INTERNAL for inter-broker traffic (efficient, container-to-container) and EXTERNAL for clients (accessible from the host).
   Replication and ISR: Configurations ensure topics are replicated across all brokers, with minimum in-sync replicas for durability.
   Scalability: Easily add more brokers by copying a service block and adjusting IDs/ports.
   This setup creates a production-ready Kafka cluster for development/testing purposes, demonstrating best practices for KRaft mode and multi-broker configurations.
