# springboot-react-jwt-token

The goal of this project is to implement an application called `order-app` to manage orders. For it, we will implement a back-end application called `order-api` using [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) framework and a font-end application called `order-ui` using [ReactJS](https://reactjs.org/). Besides, we will use [`JWT Authentication`](https://en.wikipedia.org/wiki/JSON_Web_Token) to secure both applications.

## Applications

- **order-api**

  `Spring Boot` Web Java backend application that exposes a Rest API to manage orders. Its secured endpoints can just be accessed if a valid JWT Token is provided. `order-api` stores its data in [`MySQL`](https://www.mysql.com/) database.

  `order-api` has the following endpoints

  | Endpoint                                                 | Authenticated | Roles           |
  | -------------------------------------------------------- | ------------- | --------------- |
  | `POST /auth/token`                                       | No            |                 |
  | `GET /public/numberOfUsers`                              | No            |                 |
  | `GET /public/numberOfOrders`                             | No            |                 |
  | `GET /api/users`                                         | Yes           | `ADMIN`         |
  | `GET /api/users/{username}`                              | Yes           | `ADMIN`         |
  | `POST /api/users {"username": "...", "password": "..."}` | Yes           | `ADMIN`         |
  | `DELETE /api/users/{username}`                           | Yes           | `ADMIN`         |
  | `GET /api/orders`                                        | Yes           | `ADMIN`, `USER` |
  | `GET /api/orders/{refr}`                                 | Yes           | `ADMIN`, `USER` |
  | `POST /api/orders {"refr": "...", "description": "..."}` | Yes           | `ADMIN`         |
  | `DELETE /api/orders/{refr}`                              | Yes           | `ADMIN`         |

- **order-ui**

  `ReactJS` frontend application where `users` can see the list of orders and `admins` can manage orders and users. In order to access the application, a `user` and `admin` must login using his/her username and password. All the requests coming from `order-ui` to secured endpoints in `order-api` have the JWT token that is generated when the `user` or `admin` log in. `order-ui` uses [`Semantic UI React`](https://react.semantic-ui.com/) as CSS-styled framework.

## Prerequisites

- **jq**

  In order to run some commands/scripts, you must have [`jq`](https://stedolan.github.io/jq) installed on you machine

## Start Environment

- Open a terminal and inside `springboot-react-jwt-token` root folder run
  ```
  docker-compose up -d
  ```
  
- Wait a little bit until `mysql` container is Up (healthy). You can check their status running
  ```
  docker-compose ps
  ```

## Running order-app using Maven & Npm

- **order-api**

  - Open a terminal and navigate to `springboot-react-jwt-token/order-api` folder

  - Run the following `Maven` command to start the application
    ```
    ./mvnw clean spring-boot:run
    ```

- **order-ui**

  - Open another terminal and navigate to `springboot-react-jwt-token/order-ui` folder

  - \[Optional\] Run the command below if you are running the application for the first time
    ```
    npm install
    ```

  - Run the `npm` command below to start the application
    ```
    npm start
    ```

## Applications URLs

| Application | URL                                   | Credentials                  |
| ----------- | ------------------------------------- | ---------------------------- |
| order-api   | http://localhost:8080/swagger-ui.html |                              |
| order-ui    | http://localhost:3000                 | `admin/admin` or `user/user` |

## Demo

The gif below shows ...

## Testing order-api Endpoints

- **Manual Endpoints Test using Swagger**
  
  All endpoints with the lock sign are secured. In order to access them, you need a valid JWT token.

  - Open a browser and access http://localhost:8080/swagger-ui.html

  - Click on `Auth Controller`, then on `POST /auth/token` and, finally, on `Try it out`
  
  - Provide the following payload with `username` and `password`
    ```
    { "password": "user", "username": "user" }
    ```
    It should return something like
    ```
    Code: 200
    {
      "id": 2,
      "username": "user",
      "authorities": [{ "authority": "USER"}],
      "jwtToken": "eyJhbGciOiJ...."
    }
    ```

  - Copy the `jwtToken` value (**without** the double quotes)
  
  - Click on the `Authorize` button on the top of the page
  
  - On `Value` input field, paste the copied token prefixed by `Bearer and a space
    ```
    Bearer eyJhbGciOiJ....
    ```
  
  - Click on `Authorize` and then on `Close`
  
  - Done, you can now call the secure endpoints. The token will expire in **5 minutes**. Besides, the `user` user is forbidden to access the endpoint under `User Controller`. Just `admin` can.

- **Manual Endpoints Test using curl**

  - Open a terminal
  
  - Call `GET /public/numberOfOrders`
    ```
    curl -i localhost:8080/public/numberOfOrders
    ```
    It should return
    ```
    HTTP/1.1 200
    1
    ```

  - Call `GET /api/orders` without JWT token
    ```
    curl -i localhost:8080/api/orders
    ```
    As for this endpoint a valid JWT token is required, it should return
    ```
    HTTP/1.1 401
    {
      "timestamp": "...",
      "status": 401,
      "error": "Unauthorized",
      "message": "Unauthorized",
      "path": "/api/orders"
    }
    ```

  - Get `user` JWT token
    ```
    USER_JWT_TOKEN="$(curl -s -X POST "http://localhost:8080/auth/token" \
      -H 'Content-Type: application/json' \
      -d '{"username": "admin", "password": "admin"}' | jq -r .jwtToken)"
    ```

  - Call again `GET /api/orders`, now with `user` JWT token
    ```
    curl -i -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/api/orders
    ```
    It should return
    ```
    HTTP/1.1 200
    [
      {
        "refr": "abc",
        "description": "Buy one MacBook Pro"
      }
    ]
    ```

- **Automatic Endpoints Test**

  - Open a terminal and make sure you are in `springboot-react-jwt-token` root folder

  - Run the following script
    ```
    ./order-api/test-endpoints.sh
    ```
    It should return something like the output below, where it shows the http code for different requests
    ```
                     Endoints | without creds |  user creds |  admin creds |
    ------------------------- + ------------- + ----------- + ------------ |
     GET public/numberOfUsers |           200 |         200 |          200 |
    GET public/numberOfOrders |           200 |         200 |          200 |
    ......................... + ............. + ........... + ............ |
               GET /api/users |           401 |         403 |          200 |
          GET /api/users/user |           401 |         403 |          200 |
              POST /api/users |           401 |         403 |          201 |
            DELETE /api/users |           401 |         403 |          200 |
    ......................... + ............. + ........... + ............ |
              GET /api/orders |           401 |         200 |          200 |
          GET /api/orders/abc |           401 |         200 |          200 |
             POST /api/orders |           401 |         403 |          201 |
       DELETE /api/orders/def |           401 |         403 |          200 |
    ------------------------------------------------------------------------
     [200] Success -  [201] Created -  [401] Unauthorized -  [403] Forbidden
    ```

## Util Commands

- **MySQL**
  ```
  docker exec -it mysql mysql -uroot -psecret --database=orderdb
  show tables;
  ```

- **jwt.io**

  With [jwt.io](https://jwt.io) you can inform the JWT token and the online tool decodes the token, showing its header and payload.

## Shutdown

- Go to `order-api` and `order-ui` terminals and press `Ctrl+C` on each one

- To stop and remove docker-compose containers, networks and volumes, run the command below in `springboot-react-jwt-token` root folder
  ```
  docker-compose down -v
  ```

## How to upgrade order-ui dependencies to latest version

- In a terminal, make sure you are in `springboot-react-jwt-token/order-ui` folder

- Run the following commands
  ```
  npm i -g npm-check-updates
  ncu -u
  npm install
  ```

## References

- https://bezkoder.com/spring-boot-jwt-authentication/
- https://medium.com/rapaduratech/criando-um-token-jwt-para-autentica%C3%A7%C3%A3o-em-spring-boot-371ccb54a093
