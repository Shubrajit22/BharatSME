FROM jenkins/jenkins:lts
USER root

# Install the actual Linux Docker CLI (No mounting required)
RUN apt-get update && apt-get install -y lsb-release curl gpg && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" > /etc/apt/sources.list.d/docker.list && \
    apt-get update && apt-get install -y docker-ce-cli docker-buildx-plugin

# Stay as root to talk to the socket
USER root