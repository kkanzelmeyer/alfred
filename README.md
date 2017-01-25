# Alfred 

![Alfred Logo](https://github.com/kkanzelmeyer/alfred/raw/master/src/test/resources/alfred.png)

## A Raspberry Pi Motion Sensing Webcam experimental project

## Building Requirements
- Java 8+
- Maven 3+

## Hardware Requirements
- Raspberry Pi
- USB  Webcam

## Installation Instructions
Clone this repo.
Install with `mvn clean install`

## Installing to The Pi
### Method 1
- Clone the repo directly onto the pi and install using the installation instructions
- Create an Alfred directory somewhere on your Pi with the following structure:
```
Alfred
+-- bin
+-- config
+-- img
+-- logs
```
- Copy the config file from `src/test/resources` into the `config` directory, and modify the config as necessary
- Copy the logback file from `src/test/resources` into the `config` directory and modify as necessary
- Copy the alfred jar from `target` into the `bin` directory

### Method 2
- Complete the Method 1 installation instructions on your development machine
- Copy the Alfred directory to the Pi using `scp`. For example:
  ```
  scp -r ~/Alfred <username>@<piIpAddress>:~/Alfred
  ```

## Running the Program
To run the Alfred app we need to tell Java two things - the location of the jar file, and the location of our config items. To accomplish this we tell Java to add the `config` directory to the classpath, and then we tell Java where the Jar is and which class to run:
```
java -classpath config/ -cp /bin com.github.kkanzelmeyer.alfred.App
```

Java will automatically search for a `main` method in the App class.
