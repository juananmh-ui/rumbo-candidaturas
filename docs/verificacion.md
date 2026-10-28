# Verificación de Rumbo v1.0

**Equipo: Oscar Ramírez Toro y Juan Antonio Moreno Hevilla.**

Fecha: **28/10/2026**.

## Compilación comprobada

- JDK instalado: Eclipse Temurin 25.0.2; compilación dirigida a Java 24 mediante `maven.compiler.release=24`.
- Maven Wrapper 3.9.11, probado con `verify`.
- Resultado: **BUILD SUCCESS**.
- JAR ejecutable: `target/rumbo-1.0.jar`.

## Pruebas ejecutadas

| Clase | Casos | Fallos | Errores |
| --- | ---: | ---: | ---: |
| CandidaturaServiceTest | 5 | 0 | 0 |
| CliIntegrationTest | 8 | 0 | 0 |
| CsvCodecTest | 16 | 0 | 0 |
| FileRepositoryTest | 6 | 0 | 0 |
| **Total** | **35** | **0** | **0** |

Se probó además el JAR mediante procesos Java independientes: primer listado, importación de 15 registros, filtros combinados, consulta del ID 13, alta del ID 16, consulta del alta, borrado, consulta de un ID eliminado, fecha imposible y archivo inexistente. Se comprobaron los códigos de salida esperados y la persistencia entre invocaciones.

Las pruebas de importación comprueban que un duplicado o una línea incorrecta al final del CSV no modifica parcialmente el destino. Las pruebas de serialización conservan punto y coma, comillas, tildes y campos vacíos.

## Diseño y documentos

- Prototipo Figma: 26 pantallas con capas editables y **252 conexiones** de navegación.
- Dos puntos de inicio: seguimiento con ejemplos y primera importación.
- Comprobación visual de listado, formulario, empresas, estadísticas y asistente.
- PDF de pantallas: 27 páginas incluyendo portada.
- Manual de estilo: 13 páginas; paleta, estados, tipografía, logo, controles, tablas, tarjetas, navegación, iconografía, layout y componentes.
- Las combinaciones de texto y fondo de la paleta se han medido: todas las combinaciones listadas en el manual superan 4,5:1. Esto no equivale a certificar toda la interfaz.

## Publicación

Repositorio público: <https://github.com/juananmh-ui/rumbo-candidaturas>. La entrega técnica se identifica como **v1.0**. Los PDF y la base MP4 se publican como archivos individuales; no se proporciona un ZIP de entrega.

## Intervención del equipo

Se ha generado una base MP4 de **8 minutos y 21 segundos**, con resultados obtenidos al ejecutar el JAR y capturas de las pantallas de Figma. El vídeo no incluye narración de los alumnos; `docs/timeline-video.csv` contiene los tiempos de cada escena.

La verificación técnica no acredita la participación personal de los alumnos. La intervención real de ambos en la grabación y sus contribuciones al repositorio deben incorporarse y registrarse antes de la entrega evaluable.
