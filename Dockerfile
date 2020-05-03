FROM firefox7025/deeplearning_base
MAINTAINER Alexander Montgomery
RUN apt-get install zip -y
USER root
ENV data = /data
ENV models = /models
ENV previousModels = ""
ARG batchSize
ARG gpuWorkers
COPY title-classifier.zip /app
RUN unzip /app/title-classifier.zip  && rm /app/title-classifier.zip
WORKDIR /app/title-classifier
CMD ./bin/title-classifier -d data -m models -o previousModels -b batchSize -g gpuWorkers