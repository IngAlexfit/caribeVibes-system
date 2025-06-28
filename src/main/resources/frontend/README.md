# Caribe Vibes - Frontend Angular

Frontend moderno en Angular 18+ para la aplicación de turismo Caribe Vibes, integrado con backend Spring Boot.

## 🚀 Características

- **Angular 18+** con TypeScript
- **Bootstrap 5** para diseño responsivo
- **Arquitectura modular** con servicios, guards e interceptors
- **Autenticación JWT** integrada
- **Diseño responsivo** con tema caribeño
- **Componentes reutilizables** y optimizados

## 📦 Instalación

### Prerrequisitos
- Node.js 18+ 
- npm 9+ o yarn
- Angular CLI 18+

### Pasos de instalación

1. **Navegar al directorio del frontend**
   ```bash
   cd src/main/resources/frontend
   ```

2. **Instalar dependencias**
   ```bash
   npm install
   ```

3. **Instalar Angular CLI globalmente (si no está instalado)**
   ```bash
   npm install -g @angular/cli@18
   ```

## 🛠️ Comandos de desarrollo

### Desarrollo local
```bash
npm run start
# La aplicación estará disponible en http://localhost:4200
# Las requests API serán proxy hacia http://localhost:8080
```

### Construcción para producción
```bash
npm run build:prod
# Los archivos se generarán en dist/caribe-vibes-frontend
```

### Ejecutar tests
```bash
npm run test
```

### Linting
```bash
npm run lint
```

## 🏗️ Estructura del proyecto

```
src/
├── app/
│   ├── core/                 # Servicios core, guards, interceptors
│   │   ├── guards/           # Guards de autenticación y autorización
│   │   ├── interceptors/     # Interceptor JWT
│   │   ├── models/           # Interfaces TypeScript
│   │   └── services/         # Servicios para API calls
│   ├── shared/               # Componentes compartidos
│   │   └── components/       # Navbar, Footer, Loading, etc.
│   ├── pages/                # Páginas de la aplicación
│   │   ├── home/             # Página principal
│   │   ├── auth/             # Login y registro
│   │   ├── destinations/     # Listado de destinos
│   │   ├── hotels/           # Listado de hoteles
│   │   ├── bookings/         # Gestión de reservas
│   │   ├── dashboard/        # Dashboard de usuario
│   │   └── contact/          # Página de contacto
│   ├── app-routing.module.ts # Configuración de rutas
│   ├── app.component.*       # Componente principal
│   └── app.module.ts         # Módulo principal
├── assets/                   # Recursos estáticos
├── environments/             # Configuración de entornos
└── styles.scss              # Estilos globales
```

## 🎨 Diseño y Tema

### Colores del tema Caribe Vibes
- **Primario**: `#4FA8D8` (Azul caribeño)
- **Secundario**: `#2C5F7A` (Azul profundo)
- **Acento**: `#E8B45E` (Dorado arena)
- **Neutral**: `#f5f7fa` (Gris claro)

### Componentes de UI
- Navbar responsivo con autenticación
- Footer informativo con links
- Cards para destinos y hoteles
- Formularios con validación
- Modales y overlays
- Loading spinners

## 🔧 Configuración de desarrollo

### Proxy para desarrollo
El archivo `proxy.conf.json` está configurado para redirigir todas las llamadas `/api/*` al backend Spring Boot en `http://localhost:8080`.

### Variables de entorno
Crear archivos de entorno según sea necesario:
- `src/environments/environment.ts` (desarrollo)
- `src/environments/environment.prod.ts` (producción)

## 🔐 Autenticación

### JWT Integration
- Interceptor automático para agregar tokens
- Guards para proteger rutas
- Refresh token automático
- Logout con limpieza de sesión

### Roles de usuario
- **USER**: Usuario regular con reservas
- **ADMIN**: Administrador con acceso completo

## 📱 Responsive Design

La aplicación está optimizada para:
- **Desktop**: 1200px+
- **Tablet**: 768px - 1199px
- **Mobile**: < 768px

## 🚀 Integración con Spring Boot

### API Endpoints utilizados
- `/api/auth/*` - Autenticación
- `/api/destinations/*` - Destinos
- `/api/hotels/*` - Hoteles
- `/api/bookings/*` - Reservas
- `/api/contact/*` - Contacto

### Deployment
Para integrar con Spring Boot:

1. Construir el frontend:
   ```bash
   npm run build:prod
   ```

2. Copiar archivos de `dist/` a `src/main/resources/static/`

3. Configurar Spring Boot para servir archivos estáticos

## 🧪 Testing

### Unit Tests
```bash
npm run test
```

### E2E Tests
```bash
npm run e2e
```

## 📝 Contribución

1. Fork el proyecto
2. Crear rama de feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

## 📞 Soporte

Para soporte o preguntas sobre el frontend:
- Crear issue en el repositorio
- Contactar al equipo de desarrollo

---

**Caribe Vibes** - Descubre el Caribe a tu manera 🏝️
