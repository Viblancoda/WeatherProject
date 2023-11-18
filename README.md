# WeatherProject
Asignatura: Desarrollo de Aplicaciones para Ciencia de Datos  
Curso: 2023/24  
Titulación: Ciencia e Ingeniería de Datos  
Escuela: Escuela Técnica Superior de Ingeniería Informática  
Universidad: Universidad de Las Palmas de Gran Canaria  

## Resumen de la Funcionalidad
La práctica tiene como objetivo desarrollar una aplicación cliente-servidor que obtiene datos meteorológicos de diferentes ubicaciones, en este caso de las Islas Canarias, y los almacena en una base de datos SQLite. La aplicación sigue un enfoque de programación orientada a objetos en Java, haciendo uso de patrones y principios de diseño para lograr un código modular y mantenible.  
La funcionalidad se divide en tres componentes principales:  
1. WeatherController: Esta clase coordina la ejecución general del programa. Utiliza un temporizador para ejecutar periódicamente la obtención y procesamiento de datos meteorológicos.  
2. OpenWeatherMapProvider: Implementa la interfaz WeatherProvider y se encarga de obtener datos meteorológicos de la API de OpenWeatherMap. Utiliza la biblioteca Jsoup para realizar peticiones web y Gson para analizar la respuesta JSON.  
3. SQLiteWeatherStore: Implementa la interfaz WeatherStore y gestiona el almacenamiento de datos meteorológicos en una base de datos SQLite. Utiliza JDBC para interactuar con la base de datos y garantizar la persistencia de la información.  
  
## Recursos Utilizados
Entornos de Desarrollo:  
El proyecto está desarrollado en IntelliJ.  
Otros recursos que se utilizan son Jsoup (para realizar peticiones web) y SQLite (para la base de datos).  
  
Herramientas de Control de Versiones:  
Git  
  
Herramientas de Documentación:  
Markdown  
  
## Diseño
Patrones y Principios de Diseño Utilizados:  
La aplicación sigue el principio de responsabilidad única y utiliza el patrón de diseño de controlador. El código está estructurado en dos paquetes principales:  
dacd.blanco.control: Contiene las clases relacionadas con el control y la lógica de la aplicación. También contiene las interfaces y la implementación para el almacenamiento de datos meteorológicos en una base de datos SQLite.  
dacd.blanco.model: Define las clases de modelo que representan la información meteorológica y la ubicación.  

![image](https://github.com/Viblancoda/WeatherProject/assets/145458834/ea654b28-16a4-4c89-8685-e422e1411626)
