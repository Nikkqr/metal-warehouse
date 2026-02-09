# metal-warehouse
Сервис для управления складом рулонов металла.

## Endpoints
> POST /rolls - добавить рулон (поля: length, weight)

> DELETE /rolls/{id} - удалить рулон 

> GET /rolls - получить список с фильтрацией  
(один из параметров: idFrom/idTo, lengthFrom/lengthTo, weightFrom/weightTo, addedFrom/addedTo, removedFrom/removedTo)  

>GET /rolls/stats?start=...&end=... - статистика за период

## Технологии
- Java 21, Spring Boot 4.0  
- PostgreSQL
- Maven
- Docker
