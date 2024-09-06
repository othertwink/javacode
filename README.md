**КОПИРАЙТ:** Данил Бибаев 05.09.2024 Тестовое задание WalletApp для JAVACODE https://drive.google.com/file/d/15t4wdNmtZ9AShzrqLY0Jw8tlf6xdleM1/view?usp=sharing

# ВЫПОЛНЕНО
**Создано REST приложение, принимающее GET и POST запросы заданного вида с информацией о счёте, выполняющее логику по изменению счёта в базе данных PostgreSQL.**

✅ созданы liquibase миграции для базы данных

✅ приложение корректно работает в конкуретной среде свыше 1000RPS по одному кошельку и ни один запрос не возвращает HTTP 50X ответы

✅ предусмотрено соблюдение формата ответа для заведомо неверных запросов(кошелёк не существует, invalid json, недостаточно средств для проведения операции)

✅ эндпоинты подключения БД и приёма запросов протестированы при помощи интеграционных тестов и Jmeter

✅ приложение контейнеризировано, вся система поднимается через docker-compose

✅ предусмотрена возможность изменения конфигурации различных параметров приложения и базы данных при помощи внешнего файла без пересборки контейнеров

*От интеграции брокера сообщений решено отказаться, т.к. в стеке такое технологическое требование не указано явно, и для достижения необходимой нагрузоустойчивости был написан собственный контроллер.


# БЫСТРЫЙ СТАРТ
В файле .env в корневой директории проекта измените настройки на необходимые или оставьте по умолчанию.
Из корневой директории проекта выполните "docker-compose up". По умолчанию приложение принимает запросы на localhost:8090


# ХОД РАЗРАБОТКИ
Старт разработки - 3 сентября 12:50

Завершено - 5 сентября 12:30

# ЭТАПЫ
**Этап 1. Проектирование**

В соответствии с заданием выбран стек java 17, Spring 3, Postgresql. 
- Спроектирована микросервисная архитектура приложения:
  
  а) CRUD и приложение WalletApp помещены в отдельные модули cmn-hpa и wallet-service. Цель - предусмотреть дальнейшее масштабирование с возможностью подключать к приложению новые модули без переписывания базы приложения.
  
  б) Код в модулях разбит на пакеты, функционал разнесён по классам с соблюдением принципов **SOLID**(Этап 2)
- Spring Boot 3 проект поднят при помощи Spring Initializr
- Сформирована структура будущих модулей с разбивкой кода на пакеты для соблюдения принципов SOLID
- настроены зависимости
- Нагрузоустойчивость в состоянии гонки решено обеспечить при помощи использования Оптимистической Блокировки и поля версии с аннотацией @Version в сущности кошелька, с повторными попытками проведения транзакции в случае колизии.

**Этап 2. Разработка**
- Класс Wallet описывает сущность счёта в базе данных
- интерфейс WalletDAO реализует CRUD JPA repository для соединения и операциями с БД
- WalletServiceImpl реализует логику изменения счёта в БД
- WalletRequest описывает сущность запроса
- RequestController обрабатывает запросы и формирует ответы
- Созданы файлы application.properties файлы

**Этап 3. Интеграционный тест ендпоинта БД:** протестированы варианты соединения с некорректными данными, большоим количество запросов, некорректными запросами

**Этап 4. Нагрузочное тестирование ендпоинта приложения с JMeter**
- созданы образцы GET и POST запросов в соответствии с видом, данным в задании
- при повышении нагрузки большинство запросов начали возвращать 50X http ответы

**Этап 5. Рефакторинг кода**
- изменена стратегия обеспечения нагрузоустойчивости: добавлена аннотация @Lock(LockModeType.PESSIMISTIC_WRITE) к операции с сущностью в JPA репозитории в классе WalletDAO. Обоснование - попытка предотвратить состояние гонки при записи, в то время как состояние гонки при чтении уже предотвращено Оптимистической блокировкой по полю @Version сущности wallet.

**Этап 6. Сборка и миграции**
- при выполнении сборки maven не находил пакет dao в модуле cmn-jpa. Классы Wallet и WalletDAO временно перенесены в модуль wallet-service до получения помощи по зависимостям от старшего разработчика.
- созданы миграции при помощи liquibase

**Этап 7. Контейнеризация, внешний файл настроек**
- созданы файлы для контейнеризации Dockerfile, docker-compose скрипт, .env файл с переменными для настроек, проведены переменные в файлы application.properties

**Этап 8. Финальное тестирование**
- протестирован запуск приложения с изменением настроек в .env
- проведено нагрузочное тестирование приложения, работающего из докера. Все запросы от 1000 клиентов с разницей 0.01с обработаны корректно
- протестирована обработка некорректных запросов и других ошибок
