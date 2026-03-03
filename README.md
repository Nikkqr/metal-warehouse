# metal-warehouse
REST API для управления рулонами на складе.

## Функционал

- **Добавление рулона** на склад (длина и вес - обязательные параметры)
- **Удаление рулона** по ID
- **Получение списка** с фильтрацией по id/весу/длине/дате (в том числе комбинации фильтров)
- **Статистика за период**:
    - Количество добавленных/удалённых рулонов
    - Средняя/мин/макс длина и вес
    - Суммарный вес
    - Дни с мин/макс количеством и весом рулонов

## Endpoints
> POST /rolls - добавить рулон (поля: length, weight)

> DELETE /rolls/{id} - удалить рулон 

> GET /rolls - получить список с фильтрацией  
(один из параметров: idFrom/idTo, lengthFrom/lengthTo, weightFrom/weightTo, addedFrom/addedTo, removedFrom/removedTo)  

>GET /rolls/stats?start=...&end=... - статистика за период

## Технологии
- Java 21
- Spring Boot 4.0 (Web, Data JPA)
- PostgreSQL
- Maven
- Docker
- JUnit, Mockito
