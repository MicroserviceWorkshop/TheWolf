docker rm telesales
docker run -d --name telesales -p 8080:8080 --link zookeeper:zookeeper --link graylog2:graylog2 polim/thewolf_telesales:0.0.1
