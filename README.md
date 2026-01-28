## Запуск тестов
mvn clean test

## Allure отчёт
mvn allure:serve

## Запуск UI тестов в Яндекс.Браузере
mvn clean test "-Dbrowser=yandex" "-Dyandex.binary=%LOCALAPPDATA%\Yandex\YandexBrowser\Application\browser.exe"
