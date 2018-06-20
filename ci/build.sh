#!/usr/bin/env sh


[[ -d $PWD/maven && ! -d $HOME/.m2 ]] && ln -s $PWD/maven $HOME/.m2

repository=$(pwd)/r2dbc-spi-artifactory

rm -rf $HOME/.m2/repository/io/r2dbc

cd r2dbc-spi
./mvnw deploy -DaltDeploymentRepository=distribution::default::file://${repository}
