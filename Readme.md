## Приложение

--- 
- Стандартное простое **crud-приложение** для проверки знаний аспектов.
- Документация поддерживаетт `swagger`
- Также она генерируется в `target/openapi.yml` при запуске плагина
- Через `java -jar` запускается. 
- Версия jdk - 17. 
- В `pom.xml` можно отключить postgres зависимость + закомментировать `application.properties` свойства,
тогда h2 сама сгенерит все что надо. 

### ! Нюансы
На докере запустить не получилось - почему-то он не хочет запускать schema.sql
в связи с этим не создаются нужные таблицы, но коннект к базе данных происходит, приложение нормально запускается.