# Space Price

<details open=""><summary><h2>Описание</h2></summary>
  <div>
    <a href="http://80.78.244.225">Space Price</a> &ndash; современный сервис для получения актуальной информации о товарах
        интернет магазинов.
  </div>
  <p></p>
  <div align="center">
    <a href="https://youtu.be/cxv1ecIp0Y4"><img width="80%" align="center" src="https://user-images.githubusercontent.com/73485824/171059985-02b04b28-3108-4168-9f2e-1fb3de4e5e6c.jpg"></a>
    <br>
    <br>
    <br>
    <table border="0">
      <tr>
        <td width="50%"><a href="https://user-images.githubusercontent.com/73485824/171059197-9ebafeb2-af00-4770-a391-19a5e1758c55.png"><img src="https://user-images.githubusercontent.com/73485824/171059197-9ebafeb2-af00-4770-a391-19a5e1758c55.png"></a></td>
        <td width="50%"><a href="https://user-images.githubusercontent.com/73485824/171062049-ee52c577-0dd2-4af3-80f3-512aa3dd1dd2.png"><img src="https://user-images.githubusercontent.com/73485824/171062049-ee52c577-0dd2-4af3-80f3-512aa3dd1dd2.png"></a></td>
      </tr>
    </table>
  </div>
  <div>
    На сегодняшний момент приложение способно получать информацию
    из следующих магазинов:
  </div>
  <div>
    &nbsp;&nbsp;&nbsp;&nbsp;<img src="https://img.oldi.ru/bitrix/templates/oldi_new/images/logo.png" alt="Oldi" height="30px">
  </div>
  <div>
    &nbsp;&nbsp;&nbsp;&nbsp;<img src="https://cms.mvideo.ru/magnoliaPublic/dam/jcr:3c0c7e7e-d07f-4ecd-aa6d-6c4d11cda8f7" alt="MVideo" height="30px">
  </div>
  <div>
    &nbsp;&nbsp;&nbsp;&nbsp;<img src="https://upload.wikimedia.org/wikipedia/ru/e/e7/%D0%9B%D0%BE%D0%B3%D0%BE%D1%82%D0%B8%D0%BF_%D0%A1%D0%B8%D1%82%D0%B8%D0%BB%D0%B8%D0%BD%D0%BA.svg" alt="Citilink" height="30px">
  </div>
  <p></p>
  <img alt="GitHub all releases" src="https://img.shields.io/github/downloads/Geek-Team-Development/market-analyzer/total?color=brightgreen">
  <img alt="coverage" src="https://img.shields.io/badge/coverage-60%25-yellow">
  <img alt="build" src="https://img.shields.io/badge/build-passing-brightgreen">
  <img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/w/Geek-Team-Development/market-analyzer">
</details>
<details><summary><h2>Функциональность</h2></summary>
<ul>
<li>поиск товаров на главной странице приложения через поисковую строку;</li>
<li>пагинация &ndash; отображаются по 10 товаров;</li>
<li>сортировка по возрастанию и убыванию цены;</li>
<li>регистрация пользователей;</li>
<li>для зарегистрированных пользователей:
<ul>
<li>добавление интересующего товара в избранное;</li>
<li>автоматическая подписка на рассылку уведомлений об изменениях информации о товарах из избранного;</li>
<li>автоматическое обновление информации о товарах из избранного каждые 4 часа, а также при каждом
переходе на страницу избранного;</li>
<li>редактирование личных данных на странице профиля;</li>
</ul>
</li>
<li>сервис telegram bot &ndash; <a href="http://t.me/SpacePriceBot">@SpacePriceBot</a>, функциональность которого
  полностью повторяет всё вышеперечисленное.</li>
</ul>
</details>
<details><summary><h2>Сборка и запуск приложения</h2></summary>
<div>
Для запуска приложения локально необходимо иметь следующие установленные приложения:
</div>
<ul>
<li><a href="https://docs.oracle.com/en/java/javase/11/install/index.html">JDK</a> &ndash; v.11.0.12;</li>
<li><a href="https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html">Maven</a> &ndash; v.3.8.1;</li>
<li><a href="https://nodejs.org/ru/">Node.js</a> &ndash; v.16.13.2 (включая npm &ndash; v.8.6.0);</li>
<li><a href="https://angular.io/cli">Angular CLI</a> &ndash; v.13.2.6;</li>
<li><a href="https://docs.docker.com/engine/install">Docker</a> &ndash; v.20.10.12;</li>
<li><a href="https://docs.docker.com/engine/install">Docker-compose</a> &ndash; v.20.10.12;</li>
</ul>
<p>В проекте использованы дополнительные образы из docker hub
(при запуске через docker-compose скачивать или устанавливать не надо):
</p>
<ul>
<li><a href="https://hub.docker.com/_/mongo">mongo</a> &ndash; в качестве базы данных;</li>
<li><a href="https://hub.docker.com/_/rabbitmq">rabbitmq:3-management</a> &ndash; в качестве Message Broker;</li>
<li><a href="https://hub.docker.com/r/bitnami/redis">redis</a> &ndash; для кеширования результатов запросов пользователей в telegram
  bot;</li>
<li><a href="https://hub.docker.com/_/nginx">nginx</a>  &ndash; для раздачи статического контента.</li>
</ul>
&nbsp;&nbsp;&nbsp;&nbsp;После установки вышеуказанных программ необходимо:
  <ul>
    <li><a href="#git_clone">Склонировать репозиторий на локальный компьютер</a></li>
    <li><a href="#start-test-branch">Перейти на ветку "start-test"</a></li>
    <li><a href="#front-image">Cобрать образ фронтэнда</a></li>
    <li><a href="#mvn-build">Запустить сборку проекта через Maven</a></li>
    <li><a href="#run-app">Запустить приложение</a></li>
  </ul>

<a name="git_clone"><h3>Склонировать репозиторий на локальный компьютер:</h3></a>
```
git clone https://github.com/Geek-Team-Development/market-analyzer
```
<a name="start-test-branch"><h3>Перейти на ветку "start-test":</h3></a>
```
  cd market-analyzer
  git checkout start-test
```
<a name="front-image"><h3>Cобрать образ фронтэнда:</h3></a>
```
cd frontend-app
npm i
ng build
docker image build -t geekteam/frontend .
```
<a name="mvn-build"><h3>Запустить сборку проекта через Maven</h3></a>
```
cd ..
mvn clean install
```
<a name="run-app"><h3>Запустить приложение</h3></a>
```
docker-compose up -d
```
</details>
<details><summary><h2>Структура папок</h2></summary>
<table>
<tr>
<th>Директория</th>
<th>Описание</th>
</tr>
<tr>
<td>analyzer-backend-api-app</td>
<td>Сервер бэкэнда, взаимодействующий с браузером и сервером телеграм бота</td>
</tr>
<tr>
<td>analyzer-database</td>
<td>База данных</td>
</tr>
<tr>
<td>analyzer-dto</td>
<td>Модуль, содержащий dto с соответствующими мапперами для 
взаимодействия сервисов с базой данных</td>
</tr>
<tr>
<td>analyzer-parser</td>
<td>Модуль, содержащий интерфейс, который должны реализовывать все парсеры</td>
</tr>
<tr>
<td>diginetica-shop-parsers</td>
<td>Модуль, содержащий реализации парсеров, которые работают через сервис diginetica</td>
</tr>
<tr>
<td>frontend-app</td>
<td>Фронтэнд - модуль, содержащий логику для взаимодействия с сервером из модуля analyzer-backend-api-app</td>
</tr>
<tr>
<td>mvideo-parser</td>
<td>Модуль, содержащий реализацию парсера для магазина MVideo</td>
</tr>
<tr>
<td>telegram-bot</td>
<td>Сервер взаимодействующий с телеграм ботом <a href="http://t.me/SpacePriceBot">@SpacePriceBot</a> и сервером бэкэнда
из модуля analyzer-backend-api-app</td>
</tr>
</table>
</details>
