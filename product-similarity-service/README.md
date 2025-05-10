Product Similarity Service

Microservicio en Spring Boot con arquitectura hexagonal que expone un endpoint para obtener productos similares, implementando resiliencia, timeouts, degradación suave, y validado con pruebas unitarias, de integración y de carga (k6 + Grafana + InfluxDB). A continuación se describen cómo instalar, ejecutar y validar la solución.

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

## 🏛️ Arquitectura & Diseño

La solución sigue el patrón **hexagonal** (Ports and Adapters), separando la lógica de negocio, detalles de infraestructura y exposición externa:

- **Adaptadores de entrada (adapter/in)**: Controladores REST (`SimilarController`) que manejan peticiones HTTP y delegan a la capa de aplicación.
- **Capa de aplicación (application)**: Servicios (`GetSimilarProductsImpl`) orquestan la lógica de negocio, combinando llamadas a puertos de salida y aplicando resiliencia.
- **Puertos y modelos de dominio (domain)**: Interfaces (`SimilarIdsClient`, `ProductDetailClient`) y objetos de valor (`Product`) que definen el contrato interno sin depender de frameworks.
- **Adaptadores de salida (adapter/out)**: Implementaciones concretas de clientes HTTP reactivos (_WebClient_) que consumen APIs externas simuladas, configuradas con timeouts, circuit breakers y fallback.

### Aspectos clave

- **Resiliencia con Resilience4j**: Cada llamada externa está protegida por Circuit Breaker y timeout configurables, con degradación suave para no interrumpir el flujo principal.
- **Programación reactiva**: Uso de _WebClient_ y Reactor para realizar múltiples llamadas concurrentes y non-blocking, limitando el nivel de concurrencia para controlar recursos.
- **Contrato contract-first OpenAPI**: La especificación `openapi.yml` es estática y se expone directamente en Swagger UI, asegurando que el contrato no difiera del código.
- **Pruebas exhaustivas**: Cobertura con pruebas unitarias, de integración (controller y E2E) y de carga, utilizando mocks y simulando escenarios de error y latencia.

---

## ⏩ Posibles futuras mejoras Rápidas

1. Configurar _connection pooling_ en `WebClient` (Reactor Netty) para reutilizar sockets.
2. Bulk-fetch si downstream API lo soporta.

---

## 📈 Resultados Clave de la Prueba de Carga

- **p50** (`normal`): ~120 ms
- **p90** (`normal`): ~630 ms
- **Throughput**: ~160 req/s sostenido
- **Graceful Degradation**: 404/500 y timeouts devuelven `[]` sin falla del endpoint

---

**Autor:** Mario Benito Ágra Taboada


