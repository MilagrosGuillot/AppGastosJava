📌 Este proyecto es una aplicación Java Spring Boot diseñada para administrar gastos (expenses) junto con sus categorías asociadas. 
La arquitectura de la aplicación sigue un enfoque de tres capas, que abarca los controladores, los servicios y los repositorios. 
Este último hace uso del patrón JDBC template para facilitar el acceso a la base de datos y la manipulación de los datos relacionados con los gastos y categorías.
a
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Para obtener el total de gastos de un mes especifico
http://localhost:8080/api/v1/expense/sum-by-month?year=2023&month=11

Para obtener el total de gastos de todo los meses
http://localhost:8080/api/v1/expense/total-sum

Para obtener todas las categorias
http://localhost:8080/api/v1/expense/categories
