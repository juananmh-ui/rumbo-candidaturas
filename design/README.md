# Diseño de Rumbo

**Equipo: Oscar Ramírez Toro y Juan Antonio Moreno Hevilla.**

La aplicación funcional v1.0 es la CLI Java. El prototipo se ha creado dentro de Figma con capas editables, componentes, variables y conexiones; no se entrega un sitio HTML como sustituto de Figma.

- Archivo: <https://www.figma.com/design/uSeAlEUvQRcLUVO7WOOehq>
- Prototipo: <https://www.figma.com/proto/uSeAlEUvQRcLUVO7WOOehq?node-id=2-274&starting-point-node-id=2%3A274&scaling=scale-down&content-scaling=fixed>
- `tokens.json`: fuente de la paleta y las escalas utilizadas.
- `assets/`: marca, isotipo y fuentes Inter/Roboto Mono con licencias OFL.
- `figma-screens.js`: código de montaje usado mediante la API de Figma. Requiere el contexto del archivo y los IDs registrados durante la construcción; no forma parte del ejecutable Java.
- Los dos PDF se encuentran en `docs/` y se adjuntan individualmente en la entrega.

La biblioteca básica incluye Estado (4 variantes), Navegación (2), Fila de candidatura (4) e Indicador, además de controles reutilizados de Simple Design System. El diseño de escritorio usa una ventana de 1440 × 960 px y auto-layout para las agrupaciones de contenido.

Las capturas de documentación representan los recorridos con datos de ejemplo. El detalle, la edición y el borrado se centran en la candidatura #13; las vistas v3.0 no equivalen a una integración de IA ya implementada.
