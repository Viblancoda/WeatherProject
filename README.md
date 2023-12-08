# WeatherProject  
Subject: Data Science Applications Development  
Course: 2023/24  
Degree: Data Science and Engineering  
School: Higher Technical School of Computer Engineering  
University: University of Las Palmas de Gran Canaria  

## __Prediction Provider Module__
## Summary of Functionality
The Prediction Provider module is responsible for retrieving weather data from the OpenWeatherMap API, processing it, and sending predictions to a message broker using Java Message Service (JMS). It consists of the following components:

**OpenWeatherMapProvider Class**: Implements the WeatherProvider interface and fetches weather data from the OpenWeatherMap API based on location and timestamp.

**JMSWeatherStore Class**: Implements the WeatherSender interface and sends weather data to a specified JMS topic using the ActiveMQ message broker.

**WeatherController Class**: Orchestrates the interaction between the OpenWeatherMapProvider and JMSWeatherStore. It retrieves weather data for predefined locations and timestamps, prints the data to the console, and sends it to the JMS topic.

**MainSender Class**: Contains the main method to instantiate a WeatherController and schedule periodic weather data retrieval and sending tasks.

## Design
### Responsibilities:

**OpenWeatherMapProvider**: Fetches weather data from the OpenWeatherMap API and parses the JSON response.
**JMSWeatherStore**: Sends weather data to a JMS topic using the ActiveMQ message broker.
**WeatherController**: Coordinates the retrieval and sending of weather data.

### Collaboration:

WeatherController collaborates with OpenWeatherMapProvider and JMSWeatherStore to fetch and send weather data, respectively.

### Design Principles:

Follows the Single Responsibility Principle (SRP) by assigning specific responsibilities to each class.
Utilizes dependency injection in WeatherController to facilitate testing and flexibility.


## __Event Store Builder Module__

## Summary of Functionality
The Event Store Builder module, part of the broader system, subscribes to a JMS topic, receives weather predictions, and stores them in a file-based event store. It includes the following components:

**AMQTopicSubscriber Class**: Implements the Subscriber interface and connects to ActiveMQ as a durable topic subscriber. It listens for weather predictions and delegates processing to a provided Listener.

**FileEventStoreBuilder Class**: Implements the Listener interface and processes incoming messages, extracting relevant data, and storing them in files based on location and timestamp.

**MainReceiver Class**: Contains the main method to instantiate an AMQTopicSubscriber and a FileEventStoreBuilder, specifying the ActiveMQ broker URL and directory for storing events.

## Design
### Responsibilities:

**AMQTopicSubscriber**: Connects to ActiveMQ, subscribes to a JMS topic, and delegates message processing to a provided Listener.
**FileEventStoreBuilder**: Processes incoming messages, extracts data, organizes events into directories, and persists them in files.
**MainReceiver**: Orchestrates the instantiation of the subscriber and listener and initiates the subscription.

### Collaboration:

AMQTopicSubscriber collaborates with the provided Listener to handle incoming messages.
FileEventStoreBuilder uses Gson for JSON parsing and file I/O operations to store events.

### Design Principles and Patterns:

Adheres to the Single Responsibility Principle (SRP) and Dependency Inversion Principle (DIP).
Implements the Observer Pattern in AMQTopicSubscriber for asynchronous message processing.

## Conclusion
The Prediction Provider and Event Store Builder modules work together to fetch, process, and store weather predictions. The modular design adheres to design principles, promoting flexibility, maintainability, and testability. The collaboration between components allows for easy extension or replacement, enhancing the overall robustness of the system.
  
### Prediction Provider Class Diagram
![image](https://github.com/Viblancoda/WeatherProject/assets/145458834/ea654b28-16a4-4c89-8685-e422e1411626)

### Event Store Builder Class Diagram
![img_1.png](img_1.png)