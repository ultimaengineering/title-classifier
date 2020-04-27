FROM firefox7025/deeplearning_base
MAINTAINER Alexander Montgomery
RUN apt-get install zip
COPY /opt/app/shared/*.zip /app/dist.zip
RUN cd /app && unzip /app/dist.zip
WORKDIR /app/title-classifier
CMD ./title-classifier