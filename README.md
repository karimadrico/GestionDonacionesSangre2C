# Gestión Donaciones Sangre 2C

## Descripción

Este proyecto implementa la gestión de donaciones y traspasos de sangre entre hospitales utilizando JDBC y una base de datos Oracle. Incluye las siguientes funcionalidades principales:

- **realizar_traspaso**: Traspaso de sangre entre hospitales, actualizando reservas y registrando el movimiento.
- **realizar_doancion**: Registro de donaciones de sangre, validando cantidad y frecuencia, y actualizando reservas.
- **consulta_traspasos**: Consulta de todos los traspasos de un tipo de sangre, mostrando información relevante de hospitales y reservas.

## Estructura del Proyecto

- `src/lsi/ubu/solucion/GestionDonacionesSangre.java`: Implementación de las transacciones y pruebas.
- `src/lsi/ubu/enunciado/GestionDonacionesSangreException.java`: Excepciones personalizadas para la gestión de errores.
- `sql/gestion_donaciones_sangre.sql`: Script para crear y poblar las tablas necesarias.
- `lib/`: Librerías necesarias (JDBC, SLF4J, Log4J, etc.).
- `user_library`: Biblioteca de usuario de Eclipse con las dependencias requeridas.

**Autor:** Karima Drafli Rico

# EJECUCIÓN:

--- PRUEBAS realizar_traspaso ---
Traspaso realizado correctamente.
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Hospital Inexistente
Excepción esperada (hospital origen): Hospital Inexistente
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Tipo Sangre inexistente
Excepción esperada (tipo sangre): Tipo Sangre inexistente
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Valor de cantidad de traspaso por debajo de lo requerido
Excepción esperada (cantidad traspaso): Valor de cantidad de traspaso por debajo de lo requerido

--- PRUEBAS realizar_doancion ---
Donación realizada correctamente.
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Donante inexistente
Excepción esperada (donante): Donante inexistente
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Valor de cantidad de donación incorrecto
Excepción esperada (cantidad donación): Valor de cantidad de donación incorrecto
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Hospital Inexistente
Excepción esperada (hospital): Hospital Inexistente
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Donante excede el cupo de donación
Excepción esperada (frecuencia donación): Donante excede el cupo de donación

--- PRUEBAS consulta_traspasos ---

ID_TRASPASO  TIPO_SANGRE     HOSP_ORIGEN          HOSP_DESTINO         CANTIDAD   FECHA           RES_ORIGEN RES_DESTINO     ID_ORIGEN       ID_DESTINO     
-----------------------------------------------------------------------------------------------------------------------------------
1            Tipo A.         Complejo Asistencial de Avila Hospital Santos Reyes de Aranda de Duero 2,00       2025-01-11      2,85       3,45            1               2              
6            Tipo A.         Complejo Asistencial de Avila Hospital Santos Reyes de Aranda de Duero 1,00       2025-06-15      2,85       3,45            1               2              
4            Tipo A.         Hospital Santos Reyes de Aranda de Duero Complejo Asistencial Univesitario de Leon 1,20       2025-01-16      3,45       6,52            2               3              
2025-06-15 15:03:39 ERROR GestionDonacionesSangreException:66 - Tipo Sangre inexistente
Excepción esperada (tipo sangre): Tipo Sangre inexistente
FIN.............
<img width="644" alt="image" src="https://github.com/user-attachments/assets/da154362-19d2-4997-94c6-257f620e24fe" />

