FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-21@sha256:acc858298f6437cece9f964eaf3cbfd1b17aa3fcfd43ae3d35fbb3e67518d317
ENV TZ="Europe/Oslo"
COPY build/libs/app.jar app.jar
CMD ["-jar","app.jar"]