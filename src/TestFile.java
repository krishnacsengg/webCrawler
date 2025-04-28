# Install UTF-8 support
RUN yum install -y glibc-langpack-en

# Set system-wide UTF-8 locale
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL=en_US.UTF-8
