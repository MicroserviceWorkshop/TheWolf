docker rm zookeeper
docker run -d -p 2181:2181 --name zookeeper jplock/zookeeper
