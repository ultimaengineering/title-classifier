FROM firefox7025/deeplearning_base
MAINTAINER Alexander Montgomery
RUN apt-get install zip -y
USER root
COPY /opt/app/shared/title-classifier.zip /app
RUN cd /app && unzip /app/title-classifier.zip
WORKDIR /app/title-classifier
CMD ./bin/title-classifier