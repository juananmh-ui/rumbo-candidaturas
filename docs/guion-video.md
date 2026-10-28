# Guion de demostración

**Equipo: Oscar Ramírez Toro y Juan Antonio Moreno Hevilla.**

El archivo MP4 base muestra resultados reales de la CLI y capturas del Figma creado para Rumbo. No contiene la voz de ningún miembro. Este guion propone un reparto para grabar la intervención real de ambos, sin atribuir a una persona una narración que no se haya realizado.

## Antes de grabar

1. Abrir el repositorio y comprobar `java -version` y `./mvnw verify`.
2. Usar un archivo de demostración nuevo con `--archivo target/demo-grabacion.csv`.
3. Abrir el prototipo de Figma desde el enlace del README.
4. Grabar pantalla y micrófono con la herramienta que utilice el equipo. Mantener el vídeo final entre 5 y 10 minutos y exportarlo como MP4.

## Parte propuesta para Oscar: aplicación CLI

Presentar el problema: «Rumbo nos ayuda a seguir las candidaturas enviadas. En v1.0 cada comando ejecuta una operación sobre un archivo de texto y termina».

Demostrar, sin explicar el código:

```sh
java -jar target/rumbo-1.0.jar importar data/candidaturas-ejemplo.csv --archivo target/demo-grabacion.csv
java -jar target/rumbo-1.0.jar listar --archivo target/demo-grabacion.csv
java -jar target/rumbo-1.0.jar listar --estado pendiente --empresa Indra --archivo target/demo-grabacion.csv
java -jar target/rumbo-1.0.jar ver 13 --archivo target/demo-grabacion.csv
java -jar target/rumbo-1.0.jar anadir --titulo "Desarrollador JavaFX" --empresa Indra --estado pendiente --fecha 28/10/2026 --archivo target/demo-grabacion.csv
java -jar target/rumbo-1.0.jar ver 16 --archivo target/demo-grabacion.csv
java -jar target/rumbo-1.0.jar borrar 13 --archivo target/demo-grabacion.csv
java -jar target/rumbo-1.0.jar ver 13 --archivo target/demo-grabacion.csv
```

Mostrar un error de fecha o de CSV y señalar que no hay importación parcial ni menú interactivo.

## Parte propuesta para Juan Antonio: prototipo y diseño

Abrir el enlace interactivo actualizado del README. Escribir una candidatura con datos propios, guardarla y comprobar su detalle. Cambiar su estado, enlace y notas; guardar y volver al listado. Filtrar por su empresa y por el estado editado, limpiar los filtros y comprobar que los datos permanecen. Mostrar la cancelación y confirmación del borrado. Recorrer empresas, entrevistas, estadísticas, contactos y asistente. La base MP4 anterior conserva las pantallas originales; esta demostración actualizada debe grabarse desde Figma.

Explicar brevemente que las vistas de versiones futuras son un prototipo, mientras que el comportamiento implementado en v1.0 está en la CLI. Mostrar el manual y reconocer la misma identidad, tipografía y estados en las pantallas.

## Cierre conjunto

Mostrar el repositorio público, los archivos PDF separados y el tag v1.0. Confirmar que ambos miembros realizan su propia entrega y que en el vídeo intervienen las dos personas, mediante voz, presencia en pantalla o demostración real de una parte.

Las asignaciones de este guion son una propuesta de grabación, no un registro de contribuciones ya realizadas.
