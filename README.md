## Учебный pet-project.

Проект представляет из себя REST API для работы с библиотекой. Он включает методы для добавления, получения, редактирования и удаления информации об авторах, книгах и их экземплярах.

Проект написан с использованием технологий JDBC + Servlets без использования String и ORM.

### Технологии:

- Язык программирования: Java 17
- Сервер приложений: Apache Tomcat
- База данных: MySQL
- Миграции базы данных: Liquibase
- Тестирование: Testcontainers для интеграционного тестирования
- Сборка проекта: Maven

### Структура базы: 
- Автор (Author) и книга (Book) связаны по принципу many-to-many.
- Книга (Book) и экземпляр (Copy) связыны по принципу one-to-many.

## Настройка и запуск
Для запуска необходимы:
- MySQL сервер
- Docker

Liquibase используется для инициализации и миграции базы данных. Стартовые данные и структура базы данных определяются в файлах миграции Liquibase.
Проект собирается в WAR-файл с использованием Maven.
Собранный WAR-файл необходимо развернуть на сервере Apache Tomcat.