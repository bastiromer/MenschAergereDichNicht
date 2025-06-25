FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
RUN mkdir toybrokersludo
WORKDIR /toybrokersludo
ADD . /toybrokersludo
CMD sbt run
