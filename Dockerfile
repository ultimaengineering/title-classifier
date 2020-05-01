FROM firefox7025/deeplearning_base
MAINTAINER Alexander Montgomery
RUN apt-get install zip -y
USER root
ENV data = /data
ENV models = /models
ENV previousModels = ""
ENV batchSize = 32
ENV gpuWorkers = 0

COPY title-classifier.zip /app
RUN cd /app && unzip /app/title-classifier.zip
WORKDIR /app/title-classifier
CMD ./bin/title-classifier -d data -m models -o previousModels -b batchSize -g gpuWorkers