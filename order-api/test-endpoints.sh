#!/usr/bin/env bash

declare -A public_number_of_users
declare -A public_number_of_orders

declare -A user_get_me
declare -A user_get_users
declare -A user_get_user
declare -A user_delete_user

declare -A order_get_orders
declare -A order_get_order
declare -A order_create_order
declare -A order_delete_order

ADMIN_JWT_TOKEN=$(curl -s -X POST localhost:8080/auth/login -H 'Content-Type: application/json' -d '{"username": "admin", "password": "admin"}' | jq -r .jwtToken)
USER_JWT_TOKEN=$(curl -s -X POST localhost:8080/auth/login -H 'Content-Type: application/json' -d '{"username": "user", "password": "user"}' | jq -r .jwtToken)
USER2_JWT_TOKEN=$(curl -s -X POST localhost:8080/auth/signup -H 'Content-Type: application/json' -d '{"username": "user2", "password": "user2", "name": "User2", "email": "user2@mycompany.com"}' | jq -r .jwtToken)

public_number_of_users[without_creds]=$(curl -w %{http_code} -s -o /dev/null localhost:8080/public/numberOfUsers)
public_number_of_users[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/public/numberOfUsers)
public_number_of_users[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" localhost:8080/public/numberOfUsers)

public_number_of_orders[without_creds]=$(curl -w %{http_code} -s -o /dev/null localhost:8080/public/numberOfOrders)
public_number_of_orders[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/public/numberOfOrders)
public_number_of_orders[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" localhost:8080/public/numberOfOrders)

user_get_me[without_creds]=$(curl -w %{http_code} -s -o /dev/null localhost:8080/api/users/me)
user_get_me[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/api/users/me)
user_get_me[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" localhost:8080/api/users/me)

user_get_users[without_creds]=$(curl -w %{http_code} -s -o /dev/null localhost:8080/api/users)
user_get_users[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/api/users)
user_get_users[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" localhost:8080/api/users)

user_get_user[without_creds]=$(curl -w %{http_code} -s -o /dev/null localhost:8080/api/users/user)
user_get_user[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/api/users/user)
user_get_user[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" localhost:8080/api/users/user)

user_delete_user[without_creds]=$(curl -w %{http_code} -s -o /dev/null -X DELETE localhost:8080/api/users/user2)
user_delete_user[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" -X DELETE localhost:8080/api/users/user2)
user_delete_user[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" -X DELETE localhost:8080/api/users/user2)

order_get_orders[without_creds]=$(curl -w %{http_code} -s -o /dev/null localhost:8080/api/orders)
order_get_orders[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/api/orders)
order_get_orders[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" localhost:8080/api/orders)

order_get_order[without_creds]=$(curl -w %{http_code} -s -o /dev/null localhost:8080/api/orders/abc)
order_get_order[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" localhost:8080/api/orders/abc)
order_get_order[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" localhost:8080/api/orders/abc)

order_create_order[without_creds]=$(curl -w %{http_code} -s -o /dev/null -X POST localhost:8080/api/orders -H "Content-Type: application/json" -d '{"refr": "def", "description": "Buy three iPods"}')
order_create_order[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" -X POST localhost:8080/api/orders -H "Content-Type: application/json" -d '{"refr": "def", "description": "Buy three iPods"}')
order_create_order[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" -X POST localhost:8080/api/orders -H "Content-Type: application/json" -d '{"refr": "def", "description": "Buy three iPods"}')

order_delete_order[without_creds]=$(curl -w %{http_code} -s -o /dev/null -X DELETE localhost:8080/api/orders/def)
order_delete_order[user_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $USER_JWT_TOKEN" -X DELETE localhost:8080/api/orders/def)
order_delete_order[admin_creds]=$(curl -w %{http_code} -s -o /dev/null -H "Authorization: Bearer $ADMIN_JWT_TOKEN" -X DELETE localhost:8080/api/orders/def)

printf "\n"
printf "%s\n" "POST auth/login"
printf "%s\n" "==============="
printf "%s\n" "admin JWT token"
printf "%s\n" "---------------"
printf "%s\n" ${ADMIN_JWT_TOKEN}
printf "\n"
printf "%s\n" "user JWT token"
printf "%s\n" "--------------"
printf "%s\n" ${USER_JWT_TOKEN}
printf "\n"
printf "%s\n" "POST auth/signup"
printf "%s\n" "================"
printf "%s\n" "user2 JWT token"
printf "%s\n" "---------------"
printf "%s\n" ${USER2_JWT_TOKEN}
printf "\n"
printf "%s\n" "Authorization"
printf "%s\n" "============="
printf "%25s | %13s | %11s | %12s |\n" "Endpoints" "without token" "user token" "admin token"
printf "%25s + %13s + %11s + %12s |\n" "-------------------------" "-------------" "-----------" "------------"
printf "%25s | %13s | %11s | %12s |\n" "GET public/numberOfUsers" ${public_number_of_users[without_creds]} ${public_number_of_users[user_creds]} ${public_number_of_users[admin_creds]}
printf "%25s | %13s | %11s | %12s |\n" "GET public/numberOfOrders" ${public_number_of_orders[without_creds]} ${public_number_of_orders[user_creds]} ${public_number_of_orders[admin_creds]}
printf "%25s + %13s + %11s + %12s |\n" "........................." "............." "..........." "............"
printf "%25s | %13s | %11s | %12s |\n" "GET /api/users/me" ${user_get_me[without_creds]} ${user_get_me[user_creds]} ${user_get_me[admin_creds]}
printf "%25s | %13s | %11s | %12s |\n" "GET /api/users" ${user_get_users[without_creds]} ${user_get_users[user_creds]} ${user_get_users[admin_creds]}
printf "%25s | %13s | %11s | %12s |\n" "GET /api/users/user" ${user_get_user[without_creds]} ${user_get_user[user_creds]} ${user_get_user[admin_creds]}
printf "%25s | %13s | %11s | %12s |\n" "DELETE /api/users/user" ${user_delete_user[without_creds]} ${user_delete_user[user_creds]} ${user_delete_user[admin_creds]}
printf "%25s + %13s + %11s + %12s |\n" "........................." "............." "..........." "............"
printf "%25s | %13s | %11s | %12s |\n" "GET /api/orders" ${order_get_orders[without_creds]} ${order_get_orders[user_creds]} ${order_get_orders[admin_creds]}
printf "%25s | %13s | %11s | %12s |\n" "GET /api/orders/abc" ${order_get_order[without_creds]} ${order_get_order[user_creds]} ${order_get_order[admin_creds]}
printf "%25s | %13s | %11s | %12s |\n" "POST /api/orders" ${order_create_order[without_creds]} ${order_create_order[user_creds]} ${order_create_order[admin_creds]}
printf "%25s | %13s | %11s | %12s |\n" "DELETE /api/orders/def" ${order_delete_order[without_creds]} ${order_delete_order[user_creds]} ${order_delete_order[admin_creds]}
printf "%72s\n" "------------------------------------------------------------------------"
printf " [200] Success -  [201] Created -  [401] Unauthorized -  [403] Forbidden"
printf "\n"