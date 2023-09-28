# java-filmorate
Template repository for Filmorate project.

ER диаграмма проекта  Filmorate:

![picture](/src/main/resources/filmorate.png)

Пример базовых запросов к базе данных:
1. Список названий всех фильмов
```
select film_name from films;
```
2. Данные юзера с id = 2
```
select * from users where user_id = 2;
```