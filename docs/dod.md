# Definition of Done (DoD)

Una Historia de Usuario se considera terminada cuando cumple con todos los siguientes criterios:

## Checklist

- [ ] El código cumple con los estándares definidos en `checkstyle.xml`.
- [ ] La funcionalidad fue implementada según los criterios de aceptación.
- [ ] Se realizaron pruebas unitarias con JUnit.
- [ ] Las pruebas unitarias pasan correctamente.
- [ ] La cobertura de código cumple con el mínimo establecido del 60%.
- [ ] La cobertura se verifica automáticamente con JaCoCo durante `mvn verify`.
- [ ] El código fue revisado por otro integrante del equipo mediante Pull Request.
- [ ] Se corrigieron las observaciones realizadas durante el Code Review.
- [ ] El código fue integrado correctamente a la rama correspondiente.
- [ ] No existen errores conocidos que impidan utilizar la funcionalidad.
- [ ] Se actualizó la documentación cuando fue necesario.
- [ ] Los commits cumplen con la convención de Conventional Commits.
- [ ] La funcionalidad fue probada antes de considerarla terminada.

## Verificación local

```bash
npm install
./mvnw verify
```

El workflow de GitHub Actions repite `./mvnw verify` en cada Pull Request y en cada cambio de `main`.

## Firma del equipo

| Integrante | Firma |
|---|---|
| Shalom Sofia Vargas Muñoz | ____Sofia Vargaz_______ |
| Yeison Andres Cifuentes Reina | ______Yeison cifuentes___________ |
| Jadith Milena Saenz Magallanes | ___Milena Saenz_____________ |
| Kevin Andres Balanta Mezu | ________Kevin Balanta__________ |
