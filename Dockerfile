FROM ubuntu:latest
LABEL authors="sherl"

ENTRYPOINT ["top", "-b"]