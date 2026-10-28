# Revisión del prototipo de candidaturas

**Equipo: Oscar Ramírez Toro y Juan Antonio Moreno Hevilla.**

Fecha: **28/10/2026**.

## Problema y corrección

El primer recorrido de diseño utilizaba transiciones a estados predefinidos. Pulsar los campos llevaba a ejemplos ya rellenados y varios retornos abrían el listado original. La comprobación de destinos de los botones no verificaba que se pudieran escribir y conservar datos propios.

El usuario pidió expresamente **«Sí: campos reales y datos editables dentro de Figma»**. Se añadió una capa de código ejecutable en el mismo archivo, conservando las 26 pantallas originales y la biblioteca de componentes como referencia de diseño. La capa usa la identidad visual de Rumbo y comienza con los mismos quince registros ficticios que el CSV de la CLI.

- [Recorrido principal corregido](https://www.figma.com/proto/uSeAlEUvQRcLUVO7WOOehq?node-id=26-1064&starting-point-node-id=26%3A1064&scaling=scale-down&content-scaling=fixed).
- [Capa de código en el editor](https://www.figma.com/design/uSeAlEUvQRcLUVO7WOOehq?node-id=24-1064&autoplay-code-preview=24-1064).
- Marco de inicio: `26:1064`, «Rumbo · Prototipo interactivo».
- Capa importada: `24:1064`.
- Fuente: `design/prototipo-interactivo/`.

Los formularios guardan el texto introducido por el usuario. El detalle abre cualquier ID existente; la edición carga sus datos y mantiene el ID. Empresa y estado se filtran de forma independiente o combinada, ignorando mayúsculas y tildes en la empresa. Los botones de cancelar no guardan cambios. El borrado requiere confirmación y conserva los demás registros. Los indicadores se calculan a partir de las candidaturas actuales.

Los datos se guardan en el navegador de esa vista previa y son independientes del archivo utilizado por Java. Si el entorno bloquea el almacenamiento, se avisa. El CSV permite importar y exportar; las importaciones se validan antes de modificar los datos. La vista de IA continúa identificada como un ejemplo de v3.0.

## Verificación realizada

Se ejecutaron **15 pruebas de regresión del modelo JavaScript**, todas correctas:

```sh
node --test design/prototipo-interactivo/model.test.cjs
```

Cubren altas, edición del registro correcto, campos obligatorios, fechas reales y bisiestas, filtros independientes y combinados, tildes, conservación de datos al filtrar, borrado, CSV con comillas, importación completa o nada, IDs duplicados y rechazo de enlaces ejecutables.

Se hicieron además comprobaciones de interfaz durante el desarrollo local. Tras importar la capa, se repitió el ciclo desde el **enlace de presentación de Figma**:

| Comprobación desde Figma | Resultado |
| --- | --- |
| Alta escribiendo título, empresa, fecha, enlace y notas propios | Correcto |
| Edición de título, estado y notas, manteniendo ID y enlace | Correcto |
| Filtros combinados sobre los datos recién creados y editados | Correcto |
| Volver del detalle al listado mantiene los filtros | Correcto |
| Recargar el enlace conserva la candidatura guardada | Correcto |
| Cancelar el borrado conserva la candidatura | Correcto |
| Confirmar el borrado elimina solo el registro elegido | Correcto |

También se abrió la navegación de empresas, entrevistas, estadísticas, contactos y asistente. Se restablecieron los datos de ejemplo tras las pruebas para retirar los registros creados durante la verificación. Estas comprobaciones no afirman que se hayan probado todos los dispositivos o todas las combinaciones posibles.

## Materiales de la entrega

El README enlaza ahora al recorrido interactivo. Los PDF y el MP4 base de v1.0 conservan las capturas de las pantallas originales de diseño. Para la demostración final del comportamiento actualizado hay que grabar el nuevo recorrido, con la intervención real de ambos miembros del equipo. El código funcional de Acceso a Datos v1.0 sigue siendo la CLI Java; no se ha sustituido por una aplicación web.
