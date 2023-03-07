#!/bin/bash

copy_files(){
    mkdir -p jars/server/resources

    cp Server/build/libs/Server.jar jars/server

    cp Server/src/main/resources/* jars/server/resources

    cp common/build/libs/common.jar jars/client
    cp common/build/libs/controlLayerCommon.jar jars/server
}

run_app(){
    cd jars/server
    java -cp "./*" icesi.plantapiloto.experimento.server_manager.Server
}

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
