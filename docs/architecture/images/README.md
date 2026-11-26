# Diagramas UML — AgroValle Connect

Diagramas de diseño del proyecto en formato **Markdown + Mermaid**, listos para
visualizarse en GitHub, GitLab, VS Code o [mermaid.live](https://mermaid.live).
Las imágenes originales (`.png`) se conservan en esta misma carpeta como evidencia
del proceso de diseño.

## Índice

| # | Diagrama | Archivo | Tipo | Imagen original |
| --- | --- | --- | --- | --- |
| 1 | Componentes | [`01-componentes.md`](01-componentes.md) | UML · Componentes | `docs/architecture/images/diagrama-componentes (1).png` |
| 2 | Despliegue | [`02-despliegue.md`](02-despliegue.md) | UML · Despliegue | `docs/architecture/images/diagrama-despliegue (1).png` |
| 3 | Secuencia | [`03-secuencia.md`](03-secuencia.md) | UML · Secuencia | `docs/architecture/images/diagrama-secuencia (1).png` |
| 4 | Entidad-Relación | [`04-entidad-relacion.md`](04-entidad-relacion.md) | Modelo de datos (DER) | `docs/architecture/images/modelo-entidad-relacion.png` |
| 5 | Arquitectura en capas | [`05-arquitectura-capas.md`](05-arquitectura-capas.md) | UML · Paquetes / capas | `docs/architecture/arquitectura-mvc-gof.md` |

## Cómo visualizarlos

- **GitHub:** renderiza automáticamente los bloques ` ```mermaid `.
- **VS Code:** instalar la extensión *Markdown Preview Mermaid Support* y abrir la vista previa.
- **Navegador:** pegar el contenido del bloque en <https://mermaid.live>.

## Convenciones

- Los nombres de clases, tablas y endpoints coinciden con el código y el esquema reales.
- El diagrama de secuencia usa las rutas implementadas (`/api/v1/auth/registro/agricultor`),
  no las rutas de ejemplo del enunciado.
- El DER refleja el modelo de diseño completo; el script `docs/diseno/schema.sql`
  es una versión simplificada para el ambiente de Staging.
