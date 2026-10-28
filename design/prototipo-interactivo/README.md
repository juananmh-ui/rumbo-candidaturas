# Rumbo — prototipo interactivo para Figma

Equipo: **Oscar Ramírez Toro y Juan Antonio Moreno Hevilla**.

Esta capa ejecutable amplía el diseño de Rumbo en el archivo Figma existente. Es una interfaz de prototipo con campos HTML reales: permite introducir datos propios, dar de alta candidaturas, consultar cualquier ID, editar, filtrar por empresa y/o estado, borrar con confirmación, importar CSV y exportar las candidaturas. Las empresas, entrevistas, estadísticas y contactos comparten el estado del prototipo. El asistente muestra una carta de ejemplo y se identifica como la integración prevista para v3.0.

Los datos se conservan en el almacenamiento del navegador de la vista previa. Si el entorno impide usarlo, la interfaz lo avisa y conserva los cambios mientras dure la sesión. Exportar CSV permite guardarlos por separado. Esta capa de Figma y el archivo de datos de la CLI Java son independientes: no hay un servidor que los sincronice. La implementación funcional de Acceso a Datos v1.0 sigue siendo la CLI Java.

No usa servicios externos, claves ni dependencias de JavaScript. Los quince registros iniciales son datos ficticios del mismo CSV incluido en el proyecto. Las fuentes Inter y Roboto Mono se incluyen con sus licencias OFL. El código se ha preparado con asistencia de IA y queda disponible para la revisión y las aportaciones reales del equipo.

## Comprobación de regresiones

```sh
node --test model.test.cjs
```

Para una comprobación de desarrollo local, servir esta carpeta con cualquier servidor estático y abrir su `index.html`. La entrega se utiliza desde la capa importada en Figma.

## Recorrido de prueba

1. Filtrar por empresa, por estado y por ambos; comprobar un resultado vacío y limpiar.
2. Crear una candidatura con título, empresa y fecha propios; comprobar el ID asignado en el detalle.
3. Editar ese registro, cambiar estado, enlace y notas; guardar y volver al listado.
4. Recargar y comprobar que se conserva; si el navegador bloquea el almacenamiento, comprobar el aviso.
5. Cancelar una edición y un borrado: ninguno debe cambiar el registro.
6. Confirmar el borrado: debe desaparecer solo el registro elegido.
7. Importar un CSV con IDs nuevos y comprobarlo; un ID duplicado o una fecha imposible debe rechazar la importación completa.

El botón «Restablecer datos de ejemplo» requiere confirmación y sustituye los datos de prueba. Exportar antes si se quieren conservar cambios.
