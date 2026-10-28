# Rumbo - Tracker de candidaturas laborales

**Equipo: Oscar Ramírez Toro y Juan Antonio Moreno Hevilla.**

Rumbo permite registrar y seguir candidaturas de empleo. La entrega **v1.0** es una aplicación Java por argumentos que lee y escribe un archivo de texto UTF-8. Cada invocación realiza una operación y termina.

## Enlaces y entregables individuales

- Repositorio público: <https://github.com/juananmh-ui/rumbo-candidaturas>
- [Prototipo interactivo en Figma: campos reales, alta, edición y filtros](https://www.figma.com/proto/uSeAlEUvQRcLUVO7WOOehq?node-id=26-1064&starting-point-node-id=26%3A1064&scaling=scale-down&content-scaling=fixed).
- [Archivo de diseño y componentes en Figma](https://www.figma.com/design/uSeAlEUvQRcLUVO7WOOehq).
- [Pantallas del prototipo, PDF](docs/Rumbo_Pantallas_del_Prototipo.pdf).
- [Manual de estilo, PDF](docs/Rumbo_Manual_de_Estilo.pdf).
- [Vídeo base de demostración, MP4](https://github.com/juananmh-ui/rumbo-candidaturas/releases/download/v1.0/Rumbo_Demostracion_base.mp4).
- [Aplicación ejecutable, JAR](https://github.com/juananmh-ui/rumbo-candidaturas/releases/download/v1.0/rumbo-1.0.jar).

Los documentos se entregan por separado. El MP4 es una base técnica con rótulos, **sin narración de los alumnos**: el equipo debe incorporar su intervención real antes de presentarlo como vídeo final. El [guion](docs/guion-video.md) reparte la demostración entre ambos miembros.

## Tecnologías y alcance

| Elemento | v1.0 actual | Evolución prevista |
| --- | --- | --- |
| Java | Java 24 mínimo; compilación con JDK 25 y `--release 24` | Se mantiene Java 24 o superior |
| Construcción | Maven Wrapper 3.9.11 | Maven |
| Persistencia | CSV/TXT UTF-8 con separador `;` | JDBC en v2.0; Hibernate/JPA en v3.0 |
| Interfaz funcional | CLI por argumentos | JavaFX + FXML de Scene Builder desde v2.0 |
| Base de datos | Ninguna | SQLite prevista desde v2.0 |
| Pruebas | JUnit 5.13.4 y Mockito 5.20.0 | Ampliación por entrega |
| Diseño | Figma, 26 pantallas de diseño, capa de código interactiva, componentes y manual | Referencia de las futuras pantallas JavaFX |
| Asistente IA | Simulación visual del prototipo | Ollama4j + Ollama local en v3.0 |

La v1.0 no implementa JDBC, Hibernate, JavaFX ni llamadas a un modelo de IA. Las pantallas de empresas, entrevistas, estadísticas, contactos, notas e IA anticipan las próximas entregas. Sus instrucciones de ejecución se incorporarán cuando esas versiones existan.

## Compilar y ejecutar

Requisitos: un **JDK 24 o superior** y acceso a Internet en la primera compilación para descargar Maven y las dependencias. No hace falta instalar Maven por separado.

Comprueba `java -version` y que `JAVA_HOME` apunta al mismo JDK, no a un Java antiguo. En IntelliJ, importa `pom.xml` y selecciona un JDK 24 o superior para el proyecto y para Maven.

Windows / PowerShell:

```powershell
.\mvnw.cmd clean verify
java -jar target/rumbo-1.0.jar --help
```

En el equipo de preparación estaba instalado este JDK. Si Maven utiliza otro Java, se puede seleccionar para la sesión:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot"
.\mvnw.cmd clean verify
```

Linux / macOS:

```sh
./mvnw clean verify
java -jar target/rumbo-1.0.jar --help
```

El resultado es `target/rumbo-1.0.jar`, sin dependencias externas en ejecución. Puede ejecutarse desde otra carpeta, teniendo en cuenta que las rutas relativas se resuelven desde la carpeta actual.

## Ejemplo completo

Los comandos operan por defecto sobre `data/candidaturas.csv`. El CSV de ejemplo es un archivo distinto y se conserva para poder repetir la demostración.

```sh
java -jar target/rumbo-1.0.jar importar data/candidaturas-ejemplo.csv
java -jar target/rumbo-1.0.jar listar
java -jar target/rumbo-1.0.jar listar --estado pendiente --empresa Indra
java -jar target/rumbo-1.0.jar ver 13
java -jar target/rumbo-1.0.jar anadir --titulo "Dev JavaFX" --empresa "Indra" --estado pendiente --fecha 28/10/2026 --notas "Preparar el portfolio"
java -jar target/rumbo-1.0.jar ver 16
java -jar target/rumbo-1.0.jar borrar 13
```

Se admite `añadir` y el alias `anadir`, además de los comandos con el guion del enunciado: `-listar`, `-añadir`, `-ver`, `-borrar` y `-importar`.

Para elegir otro archivo de datos:

```sh
java -jar target/rumbo-1.0.jar importar data/candidaturas-ejemplo.csv --archivo data/demo.csv
java -jar target/rumbo-1.0.jar listar --estado entrevista --archivo data/demo.csv
```

No hay menú, `Scanner` ni lectura interactiva. Si todavía no existe el archivo de datos, el listado explica cómo iniciar la colección; un alta o una importación lo crean.

## Contrato de datos y errores

Cabecera y ejemplo:

```text
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
13;Dev Java;Indra;pendiente;17/10/2026;https://example.com/ofertas/13;Preparar una carta de presentación específica
```

- Una candidatura ocupa una línea física. La cabecera es opcional al importar y se incluye al guardar.
- `titulo`, `empresa`, `estado` y `fecha` son obligatorios para el alta. `enlace` y `notas` son opcionales.
- Estados: `pendiente`, `entrevista`, `rechazado`, `aceptado`.
- Fechas reales en `dd/MM/yyyy`, con validación estricta de días, meses y años bisiestos.
- Los filtros se combinan con AND. Empresa admite fragmentos e ignora mayúsculas y tildes.
- Los campos que contienen `;` o comillas se escriben entre comillas dobles. Una comilla interna se escapa como `""`.
- Se rechazan saltos de línea en campos para conservar un registro por línea.
- Un alta usa el máximo ID existente más uno. Borrar no renumera los demás registros.
- La importación conserva los IDs y rechaza colisiones con el archivo actual o dentro del CSV. Primero valida el archivo completo; después guarda todos los registros. Un error impide una importación parcial.
- El archivo se escribe primero en un temporal del mismo directorio y después se reemplaza. Se usa movimiento atómico si el sistema lo admite.
- La v1.0 se utiliza de forma secuencial y monousuario; no incorpora coordinación de escrituras simultáneas entre procesos.

| Código de salida | Significado |
| --- | --- |
| 0 | Operación completada |
| 2 | Argumentos o datos no válidos |
| 3 | ID o archivo de importación inexistente |
| 4 | Error de lectura, permisos o escritura |

Ejemplos de errores que terminan con un mensaje claro en `stderr`:

```sh
java -jar target/rumbo-1.0.jar ver 999
java -jar target/rumbo-1.0.jar importar no-existe.csv
java -jar target/rumbo-1.0.jar anadir --titulo Java --empresa Indra --estado pendiente --fecha 31/02/2026
```

## Arquitectura y pruebas

```text
src/main/java/es/rumbo/
  Main.java                Entrada, mensajes y códigos de salida
  cli/Arguments.java       Análisis y validación de argumentos
  model/                   Candidatura y Estado
  persistence/             CSV y repositorio de archivos
  service/                 Altas, filtros, consultas, borrados e importación
src/test/                  Pruebas JUnit y Mockito
data/                      CSV ficticio de 15 candidaturas
design/                    Tokens, fuentes, assets y código del diseño Figma
docs/                      PDF, verificaciones, guion y uso de IA
```

Se han superado **35 pruebas**: reglas de negocio, combinación de filtros, fechas, CSV con comillas y tildes, persistencia UTF-8, errores que no modifican datos e invocaciones sucesivas de la CLI. Ver [detalle de verificación](docs/verificacion.md).

La configuración de Mockito utiliza un agente explícito para evitar la autoasociación dinámica en JDK recientes, siguiendo su [documentación oficial](https://javadoc.io/static/org.mockito/mockito-core/5.20.0/org.mockito/org/mockito/Mockito.html#0.3).

## Prototipo y diseño

El enlace principal abre **Rumbo · Candidaturas interactivas**, un recorrido con una capa de código ejecutable dentro del mismo archivo Figma. Permite escribir datos propios, añadir, consultar cualquier candidatura, editar sus campos, filtrar por empresa y/o estado y borrar con confirmación. Se ha comprobado el recorrido desde el enlace de presentación, incluyendo conservación de filtros al volver y conservación de datos al recargar. Ver [revisión del prototipo](docs/revision-prototipo.md).

El prototipo conserva sus datos en ese navegador; si el entorno impide guardar, muestra un aviso. Importar y exportar CSV permite trasladar candidaturas. Los datos de la vista previa y el archivo de la CLI Java son independientes. La IA sigue siendo una vista de ejemplo de v3.0. El [código de la capa](design/prototipo-interactivo/) y sus 15 pruebas de regresión están incluidos en el repositorio.

Las 26 pantallas originales con capas de diseño y sus dos recorridos de ejemplos se conservan como referencia visual. Los PDF y el MP4 base documentan esas pantallas. Para probar campos reales o grabar la demostración actualizada se debe utilizar el enlace interactivo principal.

El archivo conserva capas de texto editables, componentes de estado, navegación, filas e indicadores, variables semánticas y estilos. Se han reutilizado botones, campos y selectores de Simple Design System de Figma. La identidad de Rumbo, sus colores y el layout se documentan en el manual. Las fuentes Inter y Roboto Mono se incluyen con sus licencias OFL.

## Equipo y contribuciones reales

| Miembro | Identificación | Contribuciones personales |
| --- | --- | --- |
| Oscar Ramírez Toro | Miembro del equipo | Pendientes de revisión y registro por el propio miembro |
| Juan Antonio Moreno Hevilla | Miembro del equipo y titular del repositorio | Solicitud y autorización de la preparación asistida; revisión y aportaciones personales pendientes de registrar |

Esta base se ha preparado con asistencia de IA, documentada en [docs/uso-ia.md](docs/uso-ia.md), `AGENTS.md`, `planning.md` y `tasks.md`. No se han generado commits ni contribuciones ficticias en nombre de Oscar. La importación inicial asistida no sustituye las aportaciones sustanciales que exige el ejercicio a cada alumno.

Antes de entregar: confirmar las cuentas de ambos colaboradores, registrar las aportaciones reales, incorporar la intervención de ambos en el vídeo y que cada miembro adjunte todos los entregables individualmente. La versión de esta entrega se identifica mediante el tag **v1.0**.
