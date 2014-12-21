docker rm telesales
docker run -d --name telesales -p 8080:8080 --link zookeeper:zookeeper polim/thewolf_telesales:0.0.1
