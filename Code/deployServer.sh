#!/bin/bash

copy_files(){
    mkdir -p Experiment/client/resources
    mkdir -p Experiment/server/resources
    mkdir -p Experiment/data
    ###########################################################

    cp serverPlugin/build/libs/serverPlugin.jar Experiment/client
    cp Client/build/libs/Client.jar Experiment/client

    cp Client/src/main/resources/* Experiment/client/resources
    ###########################################################
    cp common/build/libs/common.jar Experiment/client
    cp common/build/libs/controlLayerCommon.jar Experiment/client
    ###########################################################
    cp Server/build/libs/Server.jar Experiment/server

    cp Server/src/main/resources/* Experiment/server/resources

    cp common/build/libs/common.jar Experiment/client
    cp common/build/libs/controlLayerCommon.jar Experiment/server
}

run_app(){
    cd Experiment/server
    java -cp "./*" icesi.plantapiloto.experimento.server_manager.Server
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
