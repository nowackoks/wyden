# Wyden coding task

Solution developed by Michał Nowacki in java zulu 17.

## Starting the application

### How to run it from IJ

1. Press Alt + 1 to show project explorer
2. Navigate to org.example.Application
3. Run 'Application' from context menu (you can pass program argument in edit configuration window, to determine the initial streams, e.g. "ethusdt btcbusd" etc.)

### How to run it from console

1. Please clean and compile application with mvn clean package command invoked in parent directory.
2. Run it with command java -jar target/wydentask-1.1.jar arg1 arg2 etc. (where arg1 arg2 etc. means symbols, which should be automatically opened at start).
Please remember that args list can be empty (in such case, only http server will be started, and you should start with subscribe to any symbol with http request).

### How to run it dockerized:

1. Please build the image with command invoked in the parent directory: docker build -t wydentask .
2. Please run the command docker-compose up
3. By default, there will be two streams, listening to prices of symbols ethusdt and btcusdt. If you want to change it, please change the arguments in docker-compose.yml

## Subscribing to new symbols

curl --request POST 'http://localhost:59000/subscribe/{symbol}'

e.g.
curl --request POST 'http://localhost:59000/subscribe/btcusdt'

By default you can subscribe max to 20 symbols, it can be changed in application.properties

## Unsubscribing a symbol

curl --request DELETE 'http://localhost:59000/subscribe/{symbol}'

e.g.
curl --request DELETE 'http://localhost:59000/subscribe/btcusdt'

Unsubscribing the last symbol results with a graceful stopping the application.
