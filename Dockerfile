FROM firefox7025/deeplearning_base
MAINTAINER Alexander Montgomery
RUN apt-get install zip -y
USER root
WORKDIR /opt/app/shared/
COPY title-classifier.zip /app
RUN cd /app && unzip /app/dist.zip
WORKDIR /app/title-classifier
CMD ./title-classifier