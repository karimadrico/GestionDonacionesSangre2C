
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
<img width="577" alt="image" src="https://github.com/user-attachments/assets/62b5f5ef-b9db-49e1-8e45-5cd46fc3b9a0" />
