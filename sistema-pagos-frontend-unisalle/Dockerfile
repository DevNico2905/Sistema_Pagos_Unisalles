# ===== Etapa 1: Compilación =====
FROM node:22-alpine AS build
WORKDIR /app

# Copiar dependencias e instalar
COPY package*.json ./
RUN npm ci

# Copiar código fuente y compilar
COPY . .
RUN npm run build -- --configuration=production

# ===== Etapa 2: Servidor web =====
FROM nginx:alpine
COPY --from=build /app/dist/sistema-pagos-frontend-unisalle/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
