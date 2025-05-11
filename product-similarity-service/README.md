Product Similarity Service

Microservicio en Spring Boot (Java 17) con arquitectura hexagonal que expone el endpoint
GET /product/{productId}/similar.
Implementa llamados reactivos paralelos, timeouts por ítem, Circuit Breaker (Resilience4j), degradación suave y se valida con pruebas unitarias, de integración, E2E y carga (k6 + Grafana + InfluxDB).

---

📂 Estructura del Proyecto

product-similarity-service/
├─ app/                         # Código fuente Spring Boot
│  ├─ src/main/java/...         # Paquetes hexagonales: adapter, application, domain
│  ├─ src/main/resources/       # Configuración y openapi.yml en static/
│  ├─ src/test/java/...         # Tests unitarios e integración
│  ├─ Dockerfile                # Multi-stage build Java 17 + JAR
│  └─ mvnw, mvnw.cmd, .mvn/     # Maven Wrapper
├─ shared/
│  ├─ simulado/                 # mocks.json para stub Simulado
│  └─ k6/                       # script test.js para pruebas de carga
├─ docker-compose.yaml          # Orquestación de app, simulado, InfluxDB, Grafana y k6
└─ README.md                    # Documentación del proyecto

---

🚀 Requisitos Previos

JDK 17
Maven (o usar ./mvnw)
Docker & Docker Compose
(Opcional) Git

🛠️ Compilar & Tests

1. Clonar el repositorio

git clone https://github.com/mb-at/product-similarity-service.git
cd product-similarity-service

2. Compilar el JAR & ejecutar tests

# Build sin tests
./mvnw clean package -DskipTests
# Ejecutar tests unitarios y de integración
./mvnw test

---

📡 Ejecutar Localmente (sin Docker)

cd app
./mvnw spring-boot:run

La aplicación escucha por defecto en puerto 5000.

Endpoint principal: GET http://localhost:5000/product/{productId}/similar

Swagger UI

Accede a: http://localhost:5000/swagger-ui/index.html

Verás la spec estática openapi.yml con paths, componentes y ejemplos.

---

🐳 Ejecutar con Docker Compose

Orquesta 5 servicios: app, simulado (mocks), influxdb, grafana y k6.

docker-compose down --remove-orphans
# Levanta todo en un solo comando
docker-compose up --build -d

app → Spring Boot en localhost:5000

simulado → Stub en localhost:3001 (mocks.json)

influxdb → Métricas en localhost:8086

grafana → Dashboard en http://localhost:3000

k6 → Contenedor de carga listo, pero no ejecuta automáticamente

---

🎯 Pruebas de Carga (k6)

1. Ejecutar el script de carga en k6: docker-compose run --rm k6 run /scripts/test.js


2. El test dispara 5 escenarios (`normal`, `notFound`, `error`, `slow`, `verySlow`) a 200 VUs durante 10s cada uno.
3. Métricas volcadas automáticamente a **InfluxDB**.
4. Abrir Grafana:
   - URL: http://localhost:3000
   - Importar (o usar el provisioning incluido) el dashboard de k6.

---

# 🏛️ Arquitectura & Diseño

## Patrón Hexagonal (Ports & Adapters)

### Capas Principales
- **Adapters de Entrada (`adapter/in`)**:
  - `SimilarController` (Endpoint REST).
  
- **Capa de Aplicación (`application`)**:
  - `GetSimilarProductsImpl`: Orquesta la lógica de negocio y resiliencia.

- **Dominio (`domain`)**:
  - Interfaces: `SimilarIdsClient`, `ProductDetailClient`.
  - Modelo: `Product`.

- **Adapters de Salida (`adapter/out`)**:
  - Clientes HTTP reactivos con `WebClient`.
  - Configuraciones: Timeouts, Circuit Breaker y fallbacks.

---

## 🔧 Puntos Clave de Resiliencia

### Fetch de IDs
- **Ruta**: `GET /product/{id}/similarids` (Stub).
- **Circuit Breaker** (`productSimilarityService`):
  - Se abre tras **3 fallos** (timeout o error HTTP).
  - En estado **open**: Fallo instantáneo (sin esperar timeout).
  - Transición a **half-open** tras 5 segundos. Si un probe tiene éxito → circuito cerrado.
- **Fallback Global**: Retorna lista vacía `[]`.

### Fetch de Detalles en Paralelo
- **Concurrencia**: 20 hilos (`Flux`).
- **Timeout**: 3 segundos por detalle.
  - En timeout/error HTTP: Log de `WARN` y descarta el ítem.

---

## 🧪 Pruebas

### Tipos de Pruebas
- **Unitarias**: Validan lógica de adapters y uso de Reactor.
- **Integración**: 
  - `MockMvc` sobre controladores con stubs.
- **E2E (Resiliencia)**:
  - 3 fallos consecutivos en IDs → Circuito abre → Respuesta `[]`.
  - Forzar half-open + éxito → Circuito se cierra → Datos reales.

---

## 🖥️ Entorno de Prueba

| Componente               | Especificación/Imagen                     |
|--------------------------|-------------------------------------------|
| **Hardware**             | Intel i7 / 32 GB RAM / Ubuntu 22.04       |
| **Java**                 | OpenJDK 17                                |
| **Docker**               | 20.x                                      |
| **Servicios en Contenedores** | `product-similarity-service:app`, `ldabiralai/simulado:latest` (puerto 3001), `influxdb:1.8.2`, `grafana/grafana:8.1.2`, `loadimpact/k6:0.28.0` |

---

## 📈 Resultados de Prueba de Carga

| Métrica                  | Valor                     |
|--------------------------|---------------------------|
| **p50 (normal)**         | ~4 ms (circuito abierto)  |
| **p90 (normal)**         | ~165 ms                   |
| **p95 (normal)**         | ~286 ms                   |
| **Latencia máxima**       | ~7.15 s (fast-fail inicial) |
| **Throughput sostenido**  | ~235 req/s                |
| **Tiempo bloqueado (avg)**| ~0.8 ms                   |
| **VUs concurrentes**      | 200 máx                   |

---

**Autor:** Mario Benito Ágra Taboada


