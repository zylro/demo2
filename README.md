## Air Traffic Control App

air traffic control system, for enqueue and dequeue aircrafts by priority

aircrafts and queue persisted in mongodb

### install

install with maven
```
mvn clean -U install
```
### deployment

[heroku endpoint](http://zylro-atc.herokuapp.com)

example cURL
```
curl -X GET \
  http://zylro-atc.herokuapp.com/atc/queue \
  -H 'Authorization: api_key bloop' \
  -H 'Content-Type: application/json' 
}'
```
