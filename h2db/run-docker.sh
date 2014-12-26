docker rm h2db
docker run -d --name h2db -P -v /var/h2data:/opt/h2-data zilvinas/h2-dockerfile
