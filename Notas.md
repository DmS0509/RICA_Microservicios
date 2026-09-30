
--------------------------------------------------- 
## _TALLER MICROSERVICIOS DATOS DE PRUEBA_

--------------------------------------------------- 
### _INVESTIGADORES_

| N° | Metodo | EndPoint |
|---|---|---|
| 1 || `GET` | `http://localhost:8080/api/status` |
| 2 || `POST` | `http://localhost:8080/api/investigadores` |
| 3 || `GET` | `http://localhost:8080/api/investigadores` |

* Body peticion #2

{
  "nombreCompleto": "Ana Torres",
  "correoInstitucional": "ana.torres@uptc.edu.co",
  "grupoDeInvestigacion": "GIT-UPTC"
}

--------------------------------------------------- 
### _PUBLICACIONES_

| N° | Metodo | EndPoint |
|---|---|---|
| 4 || `GET` | `http://localhost:8081/api/status` |
| 5 || `POST` | `http://localhost:8081/api/publicaciones` |
| 6 || `GET` | `http://localhost:8081/api/publicaciones/{id}	` |
| 7 || `GET` | `http://localhost:8081/api/publicaciones?investigadorCorreo=ana.torres@uptc.edu.co` |
| 8 || `POST` | `http://localhost:8081/api/publicaciones (correo inventado)` |
| 9 || `GET` | `http://localhost:8081/api/publicaciones/id-que-no-existe	` |

* Body peticion #5

{
  "investigadorCorreo": "ana.torres@uptc.edu.co",
  "titulo": "Arquitectura hexagonal en la practica",
  "tipo": "ARTICULO",
  "anio": 2026,
  "detalles": { "revista": "Revista UPTC", "doi": "10.1234/rica.2026" }
}

* Body peticion #8

{
  "investigadorCorreo": "fantasma.inexistente@uptc.edu.co",
  "titulo": "Publicacion de un investigador que no existe",
  "tipo": "ARTICULO",
  "anio": 2026,
  "detalles": {}
}