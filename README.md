# Twitter-Stream-Processing
## Project overview
Twitter Stream Processing Pipeline that is designed to stream, process, store, and visualize tweets. The pipeline handles the streaming of tweets, processes them for analysis, and stores the results in Elasticsearch. It also uses Kibana as a visualization tool that to create an interactive dashboard which enables users to search tweets by keywords, view them on a map, analyze trends over time, and assess sentiment distribution.

### Project Structure
- **Stream Ingestion**: Uses Apache Kafka to manage a continuous stream of tweets from a simulated tweet generator.
- **Data Processing**: Processes tweets, extracts hashtags, processes geo-coordinates, and analyzes tweet sentiment.
- **Data Storage**: Stores processed tweets in Elasticsearch with an appropriate mapping for spatial, temporal, and text-based queries.
- **Visualization**: Displays visualizations with maps, trend diagrams, and sentiment gauges.

---

## Installation and Setup Guide
### Prerequisites
- **Requirements**:
  - Java 8+
  - Spark 3.5+
  - Scala 2.12+
  - Apache Kafka 2.8+
  - Apache Spark 3.3+
  - Elasticsearch 8.0+
  - Kibana 8.0+
 
### How to run the project:
1. **Set Up Kafka**:
- Download and install Kafka. Write the following commands in the root Kafka folder:
   - Start Zookeeper:
     ```bash
     .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
     ```
   - Create the Kafka topic:
     ```bash
     .\bin\windows\kafka-server-start.bat .\config\server.properties
     ```
   - Create the Kafka topic:
     ```bash
     .\bin\windows\kafka-topics.bat --create --topic tweets --bootstrap-server localhost:9092
     ```
     
2. **Set Up Elasticsearch**:
   - Download and install Elasticsearch. Write the following command inside the bin folder:
   - Start Elasticsearch:
     ```bash
     elasticsearch.bat
     ```
   - Wait for it to load, then open `localhost:9200`. make sure you see the expected JSON output
   - Create the index and apply the mapping in Kibana DevTools, Postman or the like.
   -  Mapping is found inside `index-mapping.json` in the `Elasticsearch` directory.

3. **Create .env file**:
   - Create a new `.env` file inside `spark-streaming` directory. Add to it variables as shown in `.env.example` and give them values for your elasticsearch username, password and path to the http_ca.crt certificate file - like: "C:\elasticsearch-8.16.1\config\certs\http_ca.crt".

4. **Set up Kibana**:
   - Start Kibana. Write the following command inside the bin folder:
     ```bash
     kibana.bat
     ```
   - Wait for it to load, then open `localhost:5601`. It may take some time.
   - When open, enter your Elasticsearch username and password.

5. **Run the Producer and Consumer**:
   - Navigate to the `spark-streaming` directory and run both `Producer` and `Consumer` simultaneously using a compound run configuration. Here:
   - The producer reads tweets from `data/boulder_flood_geolocated_tweets.json` and sends them to the Kafka topic.
   - The consumer reads tweets from Kafka, processes them, and stores the results in Elasticsearch.

---

### Details of Each Component:
1. **Producer**:
   - Receives a continuous stream of tweets. 
   - Creates a Kafka topic for tweet ingestion. 
   - Stores the incoming tweet stream into the Kafka topic.

2. **Consumer**:
   - Reads incoming tweets from Kafka topic.
   - Process tweets so that they are searchable over text, time and space.
   - Extract hashtags and store them separately.
   - Preform Sentiment Analysis to on each tweet based on text using Spark NLP.
   - Writes the processed data to Elasticsearch.

3. **Elasticsearch**:
   - Use Elasticsearch as the storage engine for processed tweets. 
   - Design an appropriate mapping for geo-coordinates, timestamps, and text. 

4. **Kibana Dashboard**:
   - Visualizes tweets on a map, trends over time, and sentiment distribution.
   - Provide an input field asking a user to enter a keyword.
   - Use Kibana to create visualizations and dashboards for:
       -  Tweets containing that keyword displayed on a map based on geo-coordinates. 
       -  Temporal trend diagrams of tweets over time using aggregations (hourly and daily). 
       -  Sentiment analysis gauge reflecting average sentiment for tweets over a period of time. 

---
