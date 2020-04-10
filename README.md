# springboot-react-jwt-token

The goal of this project is to implement an application called `order-app` to manage orders. For it, we will implement a back-end application called `order-api` using [`Spring Boot`](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/) framework and a font-end application called `order-ui` using [ReactJS](https://reactjs.org/). Besides, we will use [`JWT Token`](https://en.wikipedia.org/wiki/JSON_Web_Token) to secure both applications.

## Applications

- **order-api**

  `Spring Boot` Web Java backend application that exposes a Rest API to manage orders. Its sensitive endpoints can just be accessed if a user provides a valid JWT Token and it has authorization roles for it. `order-api` stores its data in [`MySQL`](https://www.mysql.com/) database.

  `order-api` has the following endpoints

  | Endpoint                                                 | Authenticated | Roles           |
  | -------------------------------------------------------- | ------------- | --------------- |
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

  `ReactJS` frontend application where `users` can see the list of orders and `admins` can manage orders and users. In order to access the application, a `user` and `admin` must login using his/her username and password. All the requests coming from `order-ui` to sensitive endpoints in `order-api` have the access token (JWT) that is generated when the `user` or `admin` logged in. `order-ui` uses [`Semantic UI React`](https://react.semantic-ui.com/) as CSS-styled framework.

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
| order-api   | http://localhost:8080/swagger-ui.html | [Access Token](#getting-access-token) |
| order-ui    | http://localhost:3000                 | `admin/admin` or `user/user` |

## Demo

The gif below shows ...

## Testing order-api Endpoints

TODO

### Getting Access Token

TODO

## Util Commands

- **MySQL**
  ```
  docker exec -it mysql mysql -uroot -psecret --database=orderdb
  show tables;
  ```

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