docker rm graylog2
docker run -d --name graylog2 -p 9000:9000 -p 12201:12201 -p 12201:12201/udp -v /var/telesales/logdata/data:/var/opt/graylog2/data -v /var/telesales/logdata/logs:/var/log/graylog2 graylog2/allinone
