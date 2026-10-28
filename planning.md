# Plan de la entrega v1.0

Equipo: **Oscar Ramírez Toro y Juan Antonio Moreno Hevilla**.

## Alcance

1. Implementar y probar importación, listado, filtros, alta, detalle y borrado por CLI sobre CSV.
2. Diseñar en Figma el flujo completo: candidaturas, empresas, entrevistas, estadísticas, contactos, notas y asistente.
3. Exportar pantallas y elaborar un manual que use la misma paleta, tipografía y componentes.
4. Preparar una demostración reproducible de comandos y navegación, con guion para la intervención de los dos miembros.
5. Publicar un único repositorio y etiquetar la versión comprobada como v1.0.

## Decisiones

- Java 24 como versión mínima; compilación comprobada con el JDK disponible, sin características preview.
- Sin dependencias en ejecución; JUnit y Mockito se usan solo en pruebas.
- Cabecera CSV opcional al importar y obligatoria al guardar; UTF-8, punto y coma y escape mediante comillas dobles.
- IDs positivos, únicos dentro del archivo. Un alta recibe el máximo ID existente más uno. Importar conserva los IDs y rechaza colisiones en bloque.
- Las fechas usan dd/MM/yyyy con validación estricta. La empresa se busca por fragmento, ignorando mayúsculas y tildes.
- Las interfaces de versiones futuras se prototipan, sin fingir que ya están implementadas en Java.
- La grabación final y las contribuciones personales deben reflejar la participación real del equipo.
- Tras la revisión, el usuario autorizó campos reales dentro de Figma. Se mantiene el diseño original y se incorpora una capa de código ejecutable; su almacenamiento en el navegador es independiente del archivo Java.
