# Информационно-справочная система архива организации

## О проекте
Этот проект — курсовая работа по Java (2 курс, весна 2023)


Проект построен на **Spring Boot**, есть:  
- REST API,
- работа с базой данных через Spring Data JPA,  
- разделение на слои (Controller, Service, Repository, Model),  
- Spring Security  


## Стек 
- Java
- Spring Boot (Security, Data JPA, Thymeleaf)  
- Maven 
- Hibernate
- Lombok 
- PostgreSQL

## Функционал
- Разграничение доступа (администратор / пользователь)  
- Управление документами (создание, редактирование, удаление, фильтры и поиск) 
- Заказ документов сотрудниками, их выдача/возврат (admin)


## Структура проекта
- `controllers/` — REST API (для пользователя, админа и входа в систему)  
- `models/` — основные сущности: документы, клиенты, сотрудники, отделы, заказы  
- `repositories/` — интерфейсы Spring Data JPA для работы с БД  
- `services/` — бизнес-логика и работа с моделями  
- `config/` — безопасность и аутентификация  


## Недостатки: 
- Безопасность паролей
- В учебных целях отсутствуют тесты 
- Требуется доработка обработки ошибок
- Нет явных DTO (Data Transfer Object) между слоями


## Скрины: 

Начальная страница для администратора

<img width="2000" height="827" alt="image" src="https://github.com/user-attachments/assets/94f582a6-6952-4e28-928c-181304a5e727" />

Фильтры и сортировка для администратора

<img width="1306" height="971" alt="image" src="https://github.com/user-attachments/assets/25585bc7-2ab7-43a8-97a6-9571e1b06c1b" />

Страница редактирования документа

<img width="1316" height="879" alt="image" src="https://github.com/user-attachments/assets/d5834d95-88a2-4072-a440-0f1c055cff46" />
<img width="1340" height="451" alt="image" src="https://github.com/user-attachments/assets/2af3d14e-5c64-4c4c-b7bd-b88862b6d267" />

Страница выдачи оригиналов документов

<img width="1999" height="900" alt="image" src="https://github.com/user-attachments/assets/d54f20f2-4e9b-4123-9b9d-7f9e88879c18" />

ER – диаграмма базы данных

<img width="1218" height="1126" alt="image" src="https://github.com/user-attachments/assets/1c263996-6b06-40a1-ae4a-9367802279ea" />



