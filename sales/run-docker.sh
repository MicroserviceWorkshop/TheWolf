docker rm sales$1
docker run -d --name sales$1 -P --link zookeeper:zookeeper --link h2db:db polim/thewolf_sales:0.0.1
