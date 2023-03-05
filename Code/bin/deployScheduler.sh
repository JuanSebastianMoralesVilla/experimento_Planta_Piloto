#!/bin/bash

copy_files(){
    mkdir -p bin
    cp serverPlugin/build/libs/serverPlugin.jar bin/
    cp serverPlugin/src/main/resources/plugin.conf bin/
    cp Client/build/libs/Client.jar bin/
    cp Client/src/main/resources/schedule.properties bin/
}

run_app(){
    cd bin/
    java -cp "./*" icesi.plantapiloto.experimento.client_manager.ScheduleManager
}
echo HOLA
if [ "$1" = "build" ]
then
    echo "build arg"
    ./gradlew $1
    copy_files
elif [ "$1" = "run" ]
then
    run_app
fi
