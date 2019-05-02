# Authorization
User authorization

## Google Sign In
* [Web Sign In Help](https://developers.google.com/identity/sign-in/web/)
* [Credentials Manager](https://console.developers.google.com/apis/credentials)

## Running outside of K8s
```bash
./gradlew build
docker build . -t authorization
docker network create core-fantasy
docker run --name authorization -p 8080:8080 --network core-fantasy -e JWT_GENERATOR_SIGNATURE_SECRET=$(head -c 32 < /dev/zero | tr '\0' '\141') -e GOOGLE_ID=abcd authorization:latest
```

## To Login
```bash
curl -i XPOST http://localhost:8080/login \
-H 'Content-Type: application/json; charset=utf-8' \
-d $'{"username":"abcd","password":"test-test-test"}'
```