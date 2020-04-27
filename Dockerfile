FROM docker.ultimaengineering.io:deeplearning_base:1.0.0
MAINTAINER Alexander Montgomery
RUN mkdir /app
RUN apt-get install zip
COPY /opt/app/shared/*.zip /app/dist.zip
RUN cd /app && unzip /app/dist.zip
WORKDIR /app/title-classifier
CMD ./title-classifier