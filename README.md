<img width="581" alt="image" src="https://github.com/user-attachments/assets/0059f36a-831a-4ab2-ad49-272c4deaa813" />

--- PRUEBAS realizar_traspaso ---
Traspaso realizado correctamente.
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Hospital ocupado
Excepción esperada (hospital origen): Hospital ocupado
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Tipo Sangre inexistente
Excepción esperada (tipo sangre): Tipo Sangre inexistente
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Valor de cantidad de traspaso por debajo de lo requerido
Excepción esperada (cantidad traspaso): Valor de cantidad de traspaso por debajo de lo requerido

--- PRUEBAS realizar_doancion ---
Donación realizada correctamente.
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Donante inexistente
Excepción esperada (donante): Donante inexistente
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Valor de cantidad de donación incorrecto
Excepción esperada (cantidad donación): Valor de cantidad de donación incorrecto
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Hospital ocupado
Excepción esperada (hospital): Hospital ocupado
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Donante excede el cupo de donación
Excepción esperada (frecuencia donación): Donante excede el cupo de donación

--- PRUEBAS consulta_traspasos ---
ID_TRASPASO  TIPO_SANGRE     HOSP_ORIGEN          HOSP_DESTINO         CANTIDAD   FECHA           RES_ORIGEN RES_DESTINO     ID_ORIGEN       ID_DESTINO     
-----------------------------------------------------------------------------------------------------------------------------------
1            Tipo A.         Complejo Asistencial de Avila Hospital Santos Reyes de Aranda de Duero 2,00       2025-01-11      2,85       3,45            1               2              
6            Tipo A.         Complejo Asistencial de Avila Hospital Santos Reyes de Aranda de Duero 1,00       2025-06-15      2,85       3,45            1               2              
4            Tipo A.         Hospital Santos Reyes de Aranda de Duero Complejo Asistencial Univesitario de Leon 1,20       2025-01-16      3,45       6,52            2               3              
2025-06-15 14:54:59 ERROR GestionDonacionesSangreException:66 - Tipo Sangre inexistente
Excepción esperada (tipo sangre): Tipo Sangre inexistente
FIN.............
