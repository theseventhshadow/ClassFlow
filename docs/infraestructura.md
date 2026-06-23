# Infraestructura

## Tabla de Contenidos

- [Entornos](#entornos)
- [Docker Compose (Local)](#docker-compose-local)
- [Kubernetes (Produccion)](#kubernetes-produccion)
- [Imagenes Docker](#imagenes-docker)
- [Ingress y Enrutamiento](#ingress-y-enrutamiento)
- [Healthchecks](#healthchecks)
- [Recursos y Limites](#recursos-y-limites)
- [Persistencia de Datos](#persistencia-de-datos)
- [Gestion de Configuracion](#gestion-de-configuracion)
- [Comandos Utiles](#comandos-utiles)

---

## Entornos

ClassFlow se despliega en dos entornos:

| Entorno | Orquestacion | Proposito |
|---|---|---|
| **Local** | Docker Compose | Desarrollo y pruebas locales |
| **Produccion** | Kubernetes (minikube o cluster real) | Despliegue en produccion |

---

## Docker Compose (Local)

### Arquitectura de contenedores

El archivo `backend/docker-compose.yml` orquesta **13 contenedores** en una red bridge llamada `classflow-back-net`:

| Tipo | Contenedor | Puerto expuesto | Base imagen |
|---|---|---|---|
| **Base de datos** | `auth-db` | - | postgres:15-alpine |
| | `academic-db` | - | postgres:15-alpine |
| | `assistance-db` | - | postgres:15-alpine |
| | `message-db` | - | postgres:15-alpine |
| | `notification-db` | - | postgres:15-alpine |
| **Microservicios** | `ms-auth` | 8081 | eclipse-temurin:25-jre-alpine |
| | `ms-academic` | 8082 | eclipse-temurin:25-jre-alpine |
| | `ms-assistance` | 8083 | eclipse-temurin:25-jre-alpine |
| | `ms-message` | 8084 | eclipse-temurin:25-jre-alpine |
| | `ms-notification` | 8085 | eclipse-temurin:25-jre-alpine |
| **Infraestructura** | `api-gateway` | 8080 | eclipse-temurin:25-jre-alpine |
| | `bff` | 8086 | eclipse-temurin:25-jre-alpine |
| **Frontend** | `frontend` | 3000 | nginx:alpine |

### Red

```yaml
networks:
  classflow-back-net:
    name: classflow-back-net
    driver: bridge
```

Todos los contenedores comparten la misma red bridge. Los microservicios se comunican entre si usando los nombres de servicio de Docker Compose como hostnames.

### Dependencias entre servicios

```
auth-db (healthy) <--- ms-auth
academic-db (healthy) <--- ms-academic
assistance-db (healthy) <--- ms-assistance
message-db (healthy) <--- ms-message
notification-db (healthy) <--- ms-notification
                                    \
ms-auth -----------------------------> bff
ms-academic -------------------------> bff
ms-assistance -----------------------> bff
ms-message --------------------------> bff
ms-notification ---------------------> bff
                                         \
bff -------------------------------------> api-gateway
ms-auth ---------------------------------> api-gateway
ms-academic -----------------------------> api-gateway
ms-assistance ---------------------------> api-gateway
ms-message ------------------------------> api-gateway
ms-notification -------------------------> api-gateway
                                              \
api-gateway ----------------------------------> frontend
```

Cada microservicio con base de datos espera a que su base de datos este saludable (`condition: service_healthy`) antes de iniciar.

### Volumenes persistentes

```yaml
volumes:
  auth-postgres-data:
  academic-postgres-data:
  assistance-postgres-data:
  message-postgres-data:
  notification-postgres-data:
```

Cada base de datos tiene su propio volumen Docker nombrado. Los datos persisten entre reinicios del contenedor. Para eliminar los volumenes y reiniciar desde cero:

```bash
docker compose down -v
```

### Variables de entorno

Las variables sensibles se cargan desde un archivo `.env` (basado en `.env.example`) que no se versiona:

```bash
# .env
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION=86400000
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion
```

Solo `ms-notification` consume variables de entorno directamente desde `.env`:

```yaml
ms-notification:
  environment:
    - MAIL_HOST=${MAIL_HOST:-smtp.gmail.com}
    - MAIL_PORT=${MAIL_PORT:-587}
    - MAIL_USERNAME=${MAIL_USERNAME}
    - MAIL_PASSWORD=${MAIL_PASSWORD}
```

---

## Kubernetes (Produccion)

### Namespace

Todos los recursos de ClassFlow se despliegan en el namespace `classflow`:

```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: classflow
```

### Inventario de recursos

| Archivo | Recurso | Tipo |
|---|---|---|
| `namespace.yml` | `classflow` | Namespace |
| `secret.yml` | `classflow-db-secrets`, `classflow-mail-secret` | Secret (Opaque) |
| `configmap.yml` | `classflow-config` | ConfigMap |
| `api-gateway.yml` | `api-gateway` | Deployment + Service (ClusterIP) |
| `bff.yml` | `bff` | Deployment + Service (ClusterIP) |
| `frontend.yml` | `frontend` | Deployment + Service (ClusterIP) |
| `ms-auth.yml` | `ms-auth` | Deployment + Service (ClusterIP) |
| `ms-academic.yml` | `ms-academic` | Deployment + Service (ClusterIP) |
| `ms-assistance.yml` | `ms-assistance` | Deployment + Service (ClusterIP) |
| `ms-message.yml` | `ms-message` | Deployment + Service (ClusterIP) |
| `ms-notification.yml` | `ms-notification` | Deployment + Service (ClusterIP) |
| `databases/auth-db.yml` | `auth-db` | StatefulSet + Service |
| `databases/academic-db.yml` | `academic-db` | StatefulSet + Service |
| `databases/assistance-db.yml` | `assistance-db` | StatefulSet + Service |
| `databases/message-db.yml` | `message-db` | StatefulSet + Service |
| `databases/notification-db.yml` | `notification-db` | StatefulSet + Service |
| `ingress-traefik.yml` | `classflow-public` | Ingress |
| `ingress-traefik-v2-gateway.yml` | `classflow-api-gateway-v2` | IngressRoute (Traefik v2) |
| `ingress-traefik-v3-gateway-auth.yml` | `jwt-auth` + `classflow-api-gateway-v3` | Middleware + IngressRoute (Traefik v3) |

### Resumen de Pods

| Pod | Tipo | Puerto | Service name (DNS interno) |
|---|---|---|---|
| `api-gateway` | Deployment | 8080 | `api-gateway.classflow.svc.cluster.local` |
| `bff` | Deployment | 8086 | `bff.classflow.svc.cluster.local` |
| `ms-auth` | Deployment | 8081 | `ms-auth.classflow.svc.cluster.local` |
| `ms-academic` | Deployment | 8082 | `ms-academic.classflow.svc.cluster.local` |
| `ms-assistance` | Deployment | 8083 | `ms-assistance.classflow.svc.cluster.local` |
| `ms-message` | Deployment | 8084 | `ms-message.classflow.svc.cluster.local` |
| `ms-notification` | Deployment | 8085 | `ms-notification.classflow.svc.cluster.local` |
| `frontend` | Deployment | 80 | `frontend.classflow.svc.cluster.local` |
| `auth-db` | StatefulSet | 5432 | `auth-db.classflow.svc.cluster.local` |
| `academic-db` | StatefulSet | 5432 | `academic-db.classflow.svc.cluster.local` |
| `assistance-db` | StatefulSet | 5432 | `assistance-db.classflow.svc.cluster.local` |
| `message-db` | StatefulSet | 5432 | `message-db.classflow.svc.cluster.local` |
| `notification-db` | StatefulSet | 5432 | `notification-db.classflow.svc.cluster.local` |

### Servicios (ClusterIP)

Cada Deployment expone un Service de tipo `ClusterIP` para la comunicacion interna. Las bases de datos usan `StatefulSet` con `volumeClaimTemplates` para almacenamiento persistente.

---

## Imagenes Docker

### Backend (multi-stage build)

Todos los servicios backend usan el mismo patron de Dockerfile multi-stage:

```dockerfile
# Etapa 1: Compilacion
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q -DskipTests package

# Etapa 2: Ejecucion
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE {puerto}
USER app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

| Servicio | Imagen | Puerto expuesto |
|---|---|---|
| `api-gateway` | `api-gateway:local` | 8080 |
| `bff` | `bff:local` | 8086 |
| `ms-auth` | `ms-auth:local` | 8081 |
| `ms-academic` | `ms-academic:local` | 8082 |
| `ms-assistance` | `ms-assistance:local` | 8083 |
| `ms-message` | `ms-message:local` | 8084 |
| `ms-notification` | `ms-notification:local` | 8085 |

### Frontend (multi-stage build)

```dockerfile
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ARG VITE_API_BASE_URL=http://localhost:8080/api
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
RUN npm run build

FROM nginx:alpine
WORKDIR /usr/share/nginx/html
RUN rm -rf ./*
COPY --from=build /app/dist .
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
ENTRYPOINT ["nginx", "-g", "daemon off;"]
```

| Componente | Imagen | Puerto |
|---|---|---|
| `frontend` | `classflow-frontend:local` | 80 (contenedor) / 3000 (host) |

### Nginx (Frontend)

El frontend se sirve con Nginx con la siguiente configuracion:

```nginx
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;  # SPA: todas las rutas al index
    }

    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";  # Cacheo de assets
    }
}
```

---

## Ingress y Enrutamiento

### Opciones de Ingress

Existen tres variantes de ingreso, segun la configuracion del cluster:

| Archivo | Version de Traefik | Caracteristicas |
|---|---|---|
| `ingress-traefik.yml` | v2 o v3 (Ingress estandar) | Rutas publicas sin autenticacion |
| `ingress-traefik-v2-gateway.yml` | v2 (IngressRoute) | Enruta `/api` al gateway |
| `ingress-traefik-v3-gateway-auth.yml` | v3 (IngressRoute + Middleware) | ForwardAuth JWT contra ms-auth |

#### 1. Ingress estandar (`ingress-traefik.yml`)

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: classflow-public
  namespace: classflow
  annotations:
    traefik.ingress.kubernetes.io/router.entrypoints: web
spec:
  ingressClassName: traefik
  rules:
    - host: classflow.local
      http:
        paths:
          - path: /api/auth/login
            pathType: Prefix
            backend:
              service:
                name: api-gateway
                port: 8080
          - path: /api/auth/register
            pathType: Prefix
            backend:
              service:
                name: api-gateway
                port: 8080
          - path: /
            pathType: Prefix
            backend:
              service:
                name: frontend
                port: 80
```

Las rutas publicas (`/api/auth/login`, `/api/auth/register`) se enrutan directamente. El frontend se sirve desde la raiz.

#### 2. Traefik v3 con ForwardAuth (`ingress-traefik-v3-gateway-auth.yml`)

Flujo de autenticacion con validacion JWT antes del enrutamiento:

```
Request -> Traefik -> Middleware: forwardAuth -> ms-auth /api/auth/validate
                  |                                      |
                  | 200 OK                               |
                  |                                      |
                  v                                      v
            api-gateway                           401 (rechazado)
```

```yaml
apiVersion: traefik.io/v1alpha1
kind: Middleware
metadata:
  name: jwt-auth
  namespace: classflow
spec:
  forwardAuth:
    address: http://ms-auth.classflow.svc.cluster.local:8081/api/auth/validate
    authResponseHeaders:
      - X-User-Id
      - X-User-Role
    trustForwardHeader: true
```

Las rutas publicas se excluyen del middleware usando prioridad:

| Prioridad | Rutas | Middleware |
|---|---|---|
| 20 (alta) | `/api/auth/login`, `/api/auth/register` | Sin middleware |
| 10 (baja) | `/api/**` | `jwt-auth` (ForwardAuth) |

### DNS local

Agregar al archivo `hosts` de Windows (`C:\Windows\System32\drivers\etc\hosts`):

```
127.0.0.1  classflow.local
```

---

## Healthchecks

### Docker Compose

Cada servicio incluye healthchecks para garantizar que solo reciban trafico cuando esten listos:

| Tipo de contenedor | Healthcheck | Intervalo |
|---|---|---|
| PostgreSQL | `pg_isready -U {user} -d {db}` | 10s |
| Microservicios Spring Boot | `curl -f http://localhost:{puerto}/actuator/health` | 10s |
| Frontend Nginx | - (depende de api-gateway) | - |

### Kubernetes

Se utilizan sondas `readinessProbe` para determinar cuando un pod esta listo para recibir trafico:

| Pod | Sonda | Path/Puerto | Delay inicial | Periodo |
|---|---|---|---|---|
| `api-gateway` | HTTP GET | `/actuator/health` :8080 | 20s | 10s |
| `bff` | HTTP GET | `/actuator/health` :8086 | 20s | 10s |
| `ms-auth` | HTTP GET | `/actuator/health` :8081 | 30s | 10s |
| `ms-academic` | HTTP GET | `/actuator/health` :8082 | 30s | 10s |
| `ms-assistance` | HTTP GET | `/actuator/health` :8083 | 30s | 10s |
| `ms-message` | HTTP GET | `/actuator/health` :8084 | 30s | 10s |
| `ms-notification` | HTTP GET | `/actuator/health` :8085 | 30s | 10s |
| `frontend` | HTTP GET | `/` :80 | 5s | 10s |
| `auth-db` | exec | `pg_isready` | 10s | 5s |
| (demas DBs) | exec | `pg_isready` | 10s | 5s |

---

## Recursos y Limites

### Docker Compose

No se definen limites de recursos en Docker Compose. Todos los contenedores usan los valores por defecto del motor Docker.

### Kubernetes

| Pod | Solicitud (request) | Limite (limit) |
|---|---|---|
| `api-gateway` | 128Mi / 100m CPU | 256Mi / 250m CPU |
| `bff` | 128Mi / 100m CPU | 256Mi / 250m CPU |
| `ms-auth` | 256Mi / 250m CPU | 512Mi / 500m CPU |
| `ms-academic` | 256Mi / 250m CPU | 512Mi / 500m CPU |
| `ms-assistance` | 256Mi / 250m CPU | 512Mi / 500m CPU |
| `ms-message` | 256Mi / 250m CPU | 512Mi / 500m CPU |
| `ms-notification` | 256Mi / 250m CPU | 512Mi / 500m CPU |
| `frontend` | 64Mi / 50m CPU | 128Mi / 100m CPU |
| `auth-db` | 128Mi / 100m CPU | 256Mi / 250m CPU |
| (demas DBs) | 128Mi / 100m CPU | 256Mi / 250m CPU |

Total estimado del cluster: **~2GB RAM / ~2.5 CPU cores**.

---

## Persistencia de Datos

### Docker Compose

Volumenes nombrados locales:

| Volumen | Montaje |
|---|---|
| `auth-postgres-data` | `/var/lib/postgresql/data` |
| `academic-postgres-data` | `/var/lib/postgresql/data` |
| `assistance-postgres-data` | `/var/lib/postgresql/data` |
| `message-postgres-data` | `/var/lib/postgresql/data` |
| `notification-postgres-data` | `/var/lib/postgresql/data` |

### Kubernetes

Cada base de datos usa un `StatefulSet` con `volumeClaimTemplates` que solicita un `PersistentVolumeClaim` de 1Gi:

```yaml
volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 1Gi
```

---

## Gestion de Configuracion

### ConfigMap

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: classflow-config
  namespace: classflow
data:
  SPRING_PROFILES_ACTIVE: "docker"
  MAIL_HOST: "smtp.gmail.com"
  MAIL_PORT: "587"
```

Los Deployments montan el ConfigMap mediante `envFrom`:

```yaml
envFrom:
  - configMapRef:
      name: classflow-config
```

### Secrets

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: classflow-db-secrets
  namespace: classflow
type: Opaque
data:
  auth-db-user: YXV0aF91c2Vy
  auth-db-password: YXV0aF9wYXNz
  # ... (valores en base64 para las 5 bases de datos)
---
apiVersion: v1
kind: Secret
metadata:
  name: classflow-mail-secret
  namespace: classflow
type: Opaque
stringData:
  MAIL_USERNAME: "tu_email@gmail.com"
  MAIL_PASSWORD: "tu_contraseña_de_aplicacion"
```

Los StatefulSets de bases de datos referencian los valores mediante `secretKeyRef`:

```yaml
env:
  - name: POSTGRES_DB
    valueFrom:
      secretKeyRef:
        name: classflow-db-secrets
        key: auth-db-name
```

---

## Comandos Utiles

### Docker Compose

```bash
# Levantar todos los servicios
cd backend
docker compose up -d

# Ver logs de un servicio especifico
docker compose logs -f ms-auth

# Reconstruir una imagen y recrear el contenedor
docker compose up -d --build ms-auth

# Detener todos los servicios
docker compose down

# Detener y eliminar volumenes (reinicio completo)
docker compose down -v

# Ver estado de los contenedores
docker compose ps
```

### Kubernetes

```bash
# Crear namespace
kubectl apply -f backend/k8s/namespace.yml

# Desplegar todos los recursos
kubectl apply -f backend/k8s/

# Desplegar solo las bases de datos
kubectl apply -f backend/k8s/databases/

# Ver pods
kubectl get pods -n classflow

# Ver logs de un pod
kubectl logs -n classflow deployment/ms-auth

# Ver servicios
kubectl get svc -n classflow

# Aplicar Ingress con autenticacion JWT (Traefik v3)
kubectl apply -f backend/k8s/ingress-traefik-v3-gateway-auth.yml

# Acceder localmente (requiere entrada en hosts)
# http://classflow.local
```

### Construccion de imagenes

```bash
# Construir todas las imagenes via Docker Compose
docker compose build

# Construir imagen de un servicio especifico
docker compose build ms-auth

# Construir imagen de forma manual
cd backend/ms-auth
docker build -t ms-auth:local .
```
