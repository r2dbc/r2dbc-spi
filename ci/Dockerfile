FROM adoptopenjdk/openjdk8:latest

RUN apt-get update

# Get the tools for signing artifacts
RUN apt-get install -y gpg

RUN apt-get clean \
 && rm -rf /var/lib/apt/lists/*
