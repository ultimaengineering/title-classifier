FROM firefox7025/deeplearning_base
MAINTAINER Alexander Montgomery
RUN apt-get install zip -y
USER root
COPY title-classifier.zip /app
RUN unzip /app/title-classifier.zip  && rm /app/title-classifier.zip
WORKDIR /app/title-classifier
CMD ./bin/title-classifier