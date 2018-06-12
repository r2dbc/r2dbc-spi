#!/usr/bin/env sh


[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

repository=$(pwd)/r2dbc-spi-artifactory

cd r2dbc-spi
./mvnw deploy -DaltDeploymentRepository=distribution::default::file://${repository}
