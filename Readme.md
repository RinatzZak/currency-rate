# CurrencyRate
***
## Сервис для получения курсов валют с оболочкой в виде телеграм бота.
***
### Сделано по урокам образовательного портала OTUS, для ознакомления с новыми технологиями. 
### В данном проекте реализовано два микросервиса и оболочка телеграм бота. Первый микросервис является парсером с сайта ЦБ РФ и построен так, чтобы всегда можно было подключить новый источник для получения валютного курса. Второй микросервис является REST API для пользователей. 

***
**Стэк:**
Spring Boot, Lombok, Jackson, JUnit, Mockito, Ehcache, Jib-maven-plugin, Docker, Kubernetes, GitLab, Telegram API

Для ознакомления с Kubernetes микросервесы были собраны с помощью Jib-maven-plugin и запушены в приватный репозиторий на GitLab для дальнейшего деплоя через Kubernetes, для чего были написаны конфигурационные файлы.
Так же микросервисы запускаются из докера с помощью команд:

**Микросервис CbrRate:**

docker run --pm -p 8081:8081 registry.gitlab.com/zakirov1/dockerregistry/cbr-rate

**Микросервис currencyRateClient:**

docker run --rm -p 8080:8080 registry.gitlab.com/zakirov1/dockerregistry/currency-rate-client2

**Пример:**

Запрос http://localhost:8081/api/v1/currencyRate/USD/30-01-2023

Ответ:

{

"numCode": "840",

"charCode": "USD",

"nominal": "1",

"name": "Доллар США",

"value": "69,3372"

}