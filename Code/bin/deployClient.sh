#!/bin/bash

copy_files(){
    mkdir -p jars/client/resources

    cp serverPlugin/build/libs/serverPlugin.jar jars/client
    cp Client/build/libs/Client.jar jars/client

    cp Client/src/main/resources/* jars/client/resources

    cp common/build/libs/common.jar jars/client
    cp common/build/libs/controlLayerCommon.jar jars/client
}

run_app(){
    cd jars/client
    java -cp "./*" icesi.plantapiloto.experimento.client_manager.ScheduleManager
}

java -version
if [ "$1" = "build" ]
then
    echo "building..."
    ./gradlew $1
    copy_files
elif [ "$1" = "run" ]
then
    echo "running..."
    run_app
fi
