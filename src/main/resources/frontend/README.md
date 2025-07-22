# 🏝️ Caribe Vibes - Frontend Angular

<div align="center">
  <img src="src/assets/images/logo-caribeVibes-circle.png" alt="Caribe Vibes Logo" width="120" height="120">
  
  **Plataforma de turismo del Caribe con experiencias personalizadas**
  
  ![Angular](https://img.shields.io/badge/Angular-18+-DD0031?style=for-the-badge&logo=angular&logoColor=white)
  ![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
  ![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3+-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)
  ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0+-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
</div>

---

## 📖 Descripción del Proyecto

**Caribe Vibes** es una aplicación web moderna y responsiva para la gestión de turismo en el Caribe, que permite a los usuarios explorar destinos, reservar hoteles, gestionar sus viajes y vivir experiencias personalizadas. La aplicación cuenta con un sistema completo de autenticación, gestión de reservas, panel administrativo y una interfaz intuitiva optimizada para dispositivos móviles y desktop.

### 🎯 Características Principales

- 🏨 **Gestión de Hoteles**: Búsqueda, filtrado y reserva de hoteles con sistema de calificaciones
- 🗺️ **Exploración de Destinos**: Catálogo interactivo de destinos caribeños con información detallada
- 📅 **Sistema de Reservas**: Gestión completa de reservas con estados, fechas y actividades opcionales
- 👤 **Autenticación Avanzada**: Login/registro con JWT, roles de usuario y protección de rutas
- 📊 **Panel Administrativo**: Dashboard completo para administradores con estadísticas y gestión
- 💬 **Sistema de Contacto**: Formulario de contacto con gestión administrativa de mensajes
- 📱 **Diseño Responsive**: Optimizado para mobile-first con tema caribeño atractivo
- 🔍 **Búsqueda y Filtros**: Sistema avanzado de filtros por destino, precio, calificación y fechas
- ⭐ **Sistema de Reseñas**: Calificaciones y comentarios de hoteles por parte de usuarios

## �️ Stack Tecnológico

### Frontend Core
- **Angular 18+**: Framework principal con TypeScript 5.0+
- **RxJS**: Programación reactiva para manejo de datos asíncronos
- **Angular Router**: Sistema de enrutamiento con guards y resolvers
- **Angular Forms**: Formularios reactivos con validaciones avanzadas

### UI/UX & Styling
- **Bootstrap 5.3+**: Framework CSS responsivo
- **SCSS**: Preprocesador CSS con variables y mixins personalizados
- **Font Awesome**: Íconos vectoriales para interfaz
- **SweetAlert2**: Modales y alertas interactivas elegantes

### Arquitectura & Patterns
- **Modular Architecture**: Separación por módulos funcionales
- **Service Layer**: Servicios especializados para lógica de negocio
- **Interceptors**: Manejo automático de autenticación y errores
- **Guards**: Protección de rutas por roles y estado de autenticación
- **Reactive Forms**: Formularios con validación en tiempo real

### Desarrollo & Herramientas
- **Angular CLI**: Herramientas de desarrollo y build
- **TypeScript**: Tipado estático y programación orientada a objetos
- **ESLint**: Linting y calidad de código
- **Jasmine & Karma**: Testing unitario
- **Angular DevTools**: Debugging y performance profiling

## 📦 Instalación y Configuración

### Prerrequisitos del Sistema
```bash
# Verificar versiones requeridas
node --version    # >= 18.13.0
npm --version     # >= 9.0.0
ng version        # Angular CLI 18+
```

### Instalación Paso a Paso

1. **Clonar el repositorio y navegar al frontend**
   ```bash
   git clone https://github.com/IngAlexfit/caribeVibes-turismo.git
   cd caribeVibes/src/main/resources/frontend
   ```

2. **Instalar dependencias del proyecto**
   ```bash
   npm install
   ```

3. **Configurar variables de entorno**
   ```bash
   # Copiar archivo de configuración de desarrollo
   cp src/environments/environment.template.ts src/environments/environment.ts
   ```

4. **Verificar configuración del proxy**
   ```bash
   # Verificar que proxy.conf.json apunte al backend correcto
   cat proxy.conf.json
   ```

### Configuración de Entornos

#### Desarrollo Local (`environment.ts`)
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  backendUrl: 'http://localhost:8080',
  frontendUrl: 'http://localhost:4200'
};
```

#### Producción (`environment.prod.ts`)
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://your-domain.com/api',
  backendUrl: 'https://your-domain.com',
  frontendUrl: 'https://your-frontend-domain.com'
};
```

## � Scripts y Comandos de Desarrollo

### Desarrollo y Servidor Local
```bash
# Iniciar servidor de desarrollo con hot reload
npm start
# o
ng serve

# Servidor con configuración específica
ng serve --configuration=development --host=0.0.0.0 --port=4200

# Abrir automáticamente en navegador
ng serve --open
```

### Build y Producción
```bash
# Build para desarrollo (con source maps)
npm run build

# Build optimizado para producción
npm run build:prod
# o
ng build --configuration=production

# Analizar bundle size
ng build --stats-json
npx webpack-bundle-analyzer dist/stats.json
```

### Testing y Calidad
```bash
# Tests unitarios
npm test
# o
ng test

# Tests con coverage
ng test --code-coverage

# Tests end-to-end
npm run e2e

# Linting y formato de código
npm run lint
ng lint

# Fix automático de problemas de linting
ng lint --fix
```

### Herramientas de Desarrollo
```bash
# Generar componente
ng generate component pages/my-component

# Generar servicio
ng generate service core/services/my-service

# Generar guard
ng generate guard core/guards/my-guard

# Servir con proxy personalizado
ng serve --proxy-config proxy.conf.json
```

## 📁 Arquitectura y Estructura del Proyecto

### Estructura de Directorios Completa
```
src/
├── app/                           # Código fuente principal de la aplicación
│   ├── core/                      # Módulo core con servicios esenciales
│   │   ├── guards/                # Guards de autenticación y autorización
│   │   │   ├── auth.guard.ts      # Guard para rutas autenticadas
│   │   │   └── admin.guard.ts     # Guard para rutas de administrador
│   │   ├── interceptors/          # Interceptores HTTP
│   │   │   └── auth.interceptor.ts # Interceptor JWT automático
│   │   ├── models/                # Interfaces y tipos TypeScript
│   │   │   ├── user.model.ts      # Modelos de usuario y autenticación
│   │   │   ├── hotel.model.ts     # Modelos de hoteles y habitaciones
│   │   │   ├── destination.model.ts # Modelos de destinos turísticos
│   │   │   ├── booking.model.ts   # Modelos de reservas y transacciones
│   │   │   └── common.model.ts    # Modelos comunes y utilidades
│   │   └── services/              # Servicios para comunicación con API
│   │       ├── api-base.service.ts # Servicio base con configuración común
│   │       ├── auth.service.ts    # Servicio de autenticación y sesión
│   │       ├── hotel.service.ts   # Servicio para gestión de hoteles
│   │       ├── destination.service.ts # Servicio para destinos
│   │       ├── booking.service.ts # Servicio para reservas
│   │       └── contact.service.ts # Servicio para contacto
│   ├── features/                  # Módulos de características específicas
│   │   └── hotel-reviews/         # Sistema de reseñas de hoteles
│   │       ├── hotel-reviews.component.ts
│   │       ├── hotel-reviews.module.ts
│   │       └── components/
│   ├── pages/                     # Componentes de páginas principales
│   │   ├── home/                  # Página de inicio con hero y destacados
│   │   ├── auth/                  # Páginas de autenticación
│   │   │   ├── login/             # Componente de inicio de sesión
│   │   │   └── register/          # Componente de registro de usuario
│   │   ├── destinations/          # Exploración y detalle de destinos
│   │   ├── hotels/                # Listado, filtros y detalle de hoteles
│   │   ├── bookings/              # Gestión de reservas del usuario
│   │   ├── booking-detail/        # Vista detallada de una reserva
│   │   ├── dashboard/             # Panel principal del usuario
│   │   ├── contact/               # Página de contacto y soporte
│   │   └── admin/                 # Páginas administrativas
│   │       ├── dashboard/         # Dashboard administrativo principal
│   │       ├── bookings/          # Gestión admin de reservas
│   │       ├── contacts/          # Gestión admin de mensajes
│   │       ├── hotels/            # CRUD de hoteles para admin
│   │       └── destinations/      # CRUD de destinos para admin
│   ├── shared/                    # Componentes y utilidades compartidas
│   │   ├── components/            # Componentes reutilizables
│   │   │   ├── navbar/            # Navegación principal con auth
│   │   │   ├── footer/            # Pie de página informativo
│   │   │   ├── loading/           # Spinner de carga personalizado
│   │   │   └── booking-modal/     # Modal de reserva de hoteles
│   │   └── styles/                # Estilos SCSS compartidos
│   │       ├── _variables.scss    # Variables CSS del tema caribeño
│   │       └── _mixins.scss       # Mixins reutilizables
│   ├── services/                  # Servicios adicionales y utilidades
│   │   └── image-upload.service.ts # Servicio para validación de imágenes
│   ├── app-routing.module.ts      # Configuración principal de rutas
│   ├── app.component.*            # Componente raíz de la aplicación
│   ├── app.module.ts              # Módulo raíz con imports y providers
│   └── proxy-test.service.ts      # Servicio para testing de conectividad
├── assets/                        # Recursos estáticos
│   └── images/                    # Imágenes de la aplicación
│       ├── logo-caribeVibes-circle.png # Logo principal circular
│       ├── icono-caribeVibes.png  # Ícono para favicon
│       ├── hotels/                # Imágenes de hoteles
│       └── destinations/          # Imágenes de destinos
├── environments/                  # Configuración por entornos
│   ├── environment.ts             # Configuración de desarrollo
│   └── environment.prod.ts       # Configuración de producción
├── styles.scss                   # Estilos globales de la aplicación
├── index.html                    # HTML principal con metadatos
├── main.ts                       # Punto de entrada de la aplicación
├── polyfills.ts                  # Polyfills para compatibilidad
└── test.ts                       # Configuración para testing
```

## 🎨 Sistema de Diseño y UI/UX

### Paleta de Colores Caribeña
```scss
// Colores Principales
$primary-color: #007bff;    // Azul caribeño vibrante
$secondary-color: #28a745;  // Verde tropical
$accent-color: #ffc107;     // Dorado arena de playa

// Colores de Estado
$success-color: #28a745;    // Verde éxito
$danger-color: #dc3545;     // Rojo alerta
$warning-color: #ffc107;    // Amarillo advertencia
$info-color: #17a2b8;       // Azul información

// Colores de Texto y Fondo
$text-primary: #212529;     // Texto principal oscuro
$text-secondary: #6c757d;   // Texto secundario gris
$bg-light: #f8f9fa;         // Fondo claro neutral
$border-color: #dee2e6;     // Bordes suaves
```

### Tipografía y Espaciado
- **Font Stack**: System fonts optimizadas para legibilidad
- **Responsive Typography**: Escalado automático por viewport
- **Line Height**: 1.5 para óptima legibilidad
- **Spacing System**: Basado en múltiplos de 8px (Bootstrap 5)

### Componentes de Interfaz

#### 🧭 Navegación
- **Navbar Responsive**: Con colapso en móviles y menú de usuario
- **Breadcrumbs**: Navegación contextual en páginas internas  
- **Sidebar Admin**: Panel lateral para funciones administrativas

#### 🎴 Cards y Contenedores
- **Hotel Cards**: Con imagen, rating, precio y acciones
- **Destination Cards**: Diseño atractivo con overlays de información
- **Booking Cards**: Estados visuales según status de reserva
- **Stats Cards**: Para métricas en dashboard administrativo

#### 📝 Formularios
- **Validación en Tiempo Real**: Feedback inmediato al usuario
- **Input Groups**: Campos agrupados con íconos contextuals
- **Date Pickers**: Calendarios integrados para fechas de reserva
- **File Upload**: Drag & drop para imágenes en administración

#### 🔘 Botones y Acciones
- **Primary Actions**: Botones destacados para acciones principales
- **Secondary Actions**: Botones outline para acciones secundarias
- **Icon Buttons**: Acciones rápidas con íconos Font Awesome
- **Loading States**: Spinners integrados durante procesamientos

#### 🎭 Modales y Overlays
- **Booking Modal**: Modal completo para proceso de reserva
- **Confirmation Dialogs**: SweetAlert2 para confirmaciones
- **Image Galleries**: Lightbox para visualización de fotos
- **Filter Panels**: Paneles deslizantes para filtros avanzados

### Responsive Design Strategy

#### Mobile First Approach
```scss
// Breakpoints personalizados
$mobile: 576px;    // Smartphones
$tablet: 768px;    // Tablets
$desktop: 992px;   // Desktop pequeño
$wide: 1200px;     // Desktop amplio
```

#### Grid System Adaptativo
- **Mobile (< 768px)**: Layout de 1 columna, navegación colapsada
- **Tablet (768px - 1199px)**: Layout de 2-3 columnas, filtros en accordion
- **Desktop (1200px+)**: Layout completo con sidebar y múltiples columnas

## 🔐 Sistema de Autenticación y Seguridad

### Arquitectura de Autenticación JWT
```typescript
interface AuthFlow {
  login: LoginRequest → JWT Token + Refresh Token
  tokenRefresh: Refresh Token → New JWT Token  
  logout: Clear Tokens + Navigate to Home
  autoLogin: Check Stored Token → Auto Authenticate
}
```

### Guards de Protección de Rutas
```typescript
// Guard para rutas autenticadas
@Injectable()
export class AuthGuard implements CanActivate {
  canActivate(): boolean {
    return this.authService.isAuthenticated();
  }
}

// Guard para rutas administrativas
@Injectable() 
export class AdminGuard implements CanActivate {
  canActivate(): boolean {
    return this.authService.isAdmin();
  }
}
```

### Interceptor HTTP Automático
- **Token Injection**: Agregar JWT automáticamente a requests
- **Error Handling**: Manejo centralizado de errores 401/403
- **Token Refresh**: Renovación automática de tokens expirados
- **Loading States**: Indicadores de carga durante requests

### Roles y Permisos
```typescript
interface UserRoles {
  USER: {
    permissions: ['view_hotels', 'create_booking', 'view_own_bookings'];
    routes: ['/hotels', '/bookings', '/dashboard'];
  };
  ADMIN: {
    permissions: ['manage_hotels', 'manage_bookings', 'view_contacts'];
    routes: ['/admin/*', '/admin/dashboard', '/admin/bookings'];
  };
}
```

## �️ Sistema de Rutas y Navegación

### Configuración de Rutas Principales
```typescript
const routes: Routes = [
  // Rutas Públicas
  { path: '', component: HomeComponent },
  { path: 'destinos', component: DestinationsComponent },
  { path: 'destinos/:id', component: DestinationsComponent },
  { path: 'hoteles', component: HotelsComponent },
  { path: 'hoteles/:id', component: HotelsComponent },
  { path: 'contacto', component: ContactComponent },

  // Rutas de Autenticación  
  { path: 'login', component: LoginComponent },
  { path: 'registro', component: RegisterComponent },

  // Rutas Protegidas (Usuario Autenticado)
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'reservas',
    component: BookingsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'reservas/:id',
    component: BookingDetailComponent,
    canActivate: [AuthGuard]
  },

  // Rutas Administrativas
  {
    path: 'admin',
    canActivate: [AdminGuard],
    children: [
      { path: 'dashboard', component: AdminDashboardComponent },
      { path: 'reservas', component: AdminBookingsComponent },
      { path: 'contactos', component: AdminContactsComponent },
      { path: 'hoteles', component: AdminHotelsComponent },
      { path: 'destinos', component: AdminDestinationsComponent }
    ]
  }
];
```

### Navegación Adaptativa
- **Breadcrumb Navigation**: Para páginas de detalle y administración
- **Query Parameters**: Para filtros, paginación y estado de búsqueda
- **Route Guards**: Protección por roles y autenticación
- **Lazy Loading**: Carga diferida de módulos administrativos

## 🔄 Gestión de Estado y Datos

### Servicios de Datos Reactivos
```typescript
// Ejemplo: HotelService con observables reactivos
@Injectable()
export class HotelService extends ApiBaseService {
  private hotelsSubject = new BehaviorSubject<HotelResponse[]>([]);
  public hotels$ = this.hotelsSubject.asObservable();
  
  private loadingSubject = new BehaviorSubject<boolean>(false);
  public loading$ = this.loadingSubject.asObservable();

  getAllHotels(page: number = 0, size: number = 10): Observable<PagedResponse<HotelResponse>> {
    this.loadingSubject.next(true);
    return this.http.get<PagedResponse<HotelResponse>>(
      this.getApiUrl(`hotels?page=${page}&size=${size}`)
    ).pipe(
      tap(response => {
        this.hotelsSubject.next(response.content);
        this.loadingSubject.next(false);
      }),
      catchError(this.handleError)
    );
  }
}
```

### Manejo de Estados de Carga
```typescript
interface ComponentState {
  loading: boolean;          // Carga inicial de datos
  submitting: boolean;       // Envío de formularios  
  error: string | null;      // Mensajes de error
  success: string | null;    // Mensajes de éxito
}
```

### Comunicación Reactiva Entre Componentes
- **Subject/Observable Patterns**: Para comunicación padre-hijo
- **Event Emitters**: Para acciones específicas como reservas
- **Router Navigation**: Para paso de parámetros entre rutas
- **Query Parameters**: Para mantener estado de filtros y búsquedas

## 📊 Funcionalidades Principales

### 🏨 Gestión de Hoteles
- **Listado con Filtros**: Búsqueda por texto, destino, precio y calificación
- **Vista de Detalle**: Información completa con galería, habitaciones y reseñas
- **Sistema de Reservas**: Modal integrado con selección de fechas y servicios
- **Calificaciones**: Sistema de estrellas con promedio de puntuaciones

### 🗺️ Exploración de Destinos  
- **Catálogo Visual**: Cards atractivas con información destacada
- **Detalle Interactivo**: Descripción, actividades y hoteles asociados
- **Filtro por Región**: Organización geográfica de destinos
- **Navegación a Hoteles**: Integración directa con listado filtrado

### 📅 Sistema de Reservas
```typescript
interface BookingFlow {
  step1: 'Selección de Hotel y Fechas';
  step2: 'Elección de Habitación y Servicios'; 
  step3: 'Datos del Huésped y Confirmación';
  step4: 'Procesamiento y Confirmación Final';
}
```

- **Proceso Guiado**: Wizard de múltiples pasos con validación
- **Cálculo Dinámico**: Precio actualizado en tiempo real
- **Estados de Reserva**: Pendiente, Confirmada, Cancelada, Completada
- **Gestión Personal**: Dashboard con todas las reservas del usuario

### 👨‍💼 Panel Administrativo
- **Dashboard de Métricas**: KPIs de reservas, ingresos y ocupación
- **CRUD de Hoteles**: Gestión completa con carga de imágenes
- **CRUD de Destinos**: Administración de catálogo de destinos  
- **Gestión de Reservas**: Confirmación, cancelación y seguimiento
- **Centro de Mensajes**: Administración de consultas de contacto

### 💬 Sistema de Contacto y Soporte
- **Formulario Inteligente**: Validación en tiempo real y categorización
- **Dashboard Admin**: Gestión centralizada de mensajes
- **Estados de Seguimiento**: Nuevo, En Proceso, Resuelto
- **Respuesta Integrada**: Sistema de respuestas directas

## 🔧 Configuración de Proxy y Desarrollo

### Configuración de Proxy para Backend
```json
// proxy.conf.json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug",
    "onProxyReq": function(proxyReq, req, res) {
      console.log("Proxy request to:", proxyReq.path);
    }
  }
}
```

### Variables de Entorno Dinámicas
```typescript
// Configuración adaptativa según entorno
export const environment = {
  production: false,
  apiUrl: process.env['API_URL'] || 'http://localhost:8080/api',
  enableDebugMode: !environment.production,
  enableMockData: false,
  logLevel: 'debug'
};
```

### Herramientas de Debug y Desarrollo
```bash
# Servidor con logs detallados
ng serve --verbose

# Build con análisis de bundle
ng build --stats-json
npx webpack-bundle-analyzer dist/stats.json

# Testing con coverage detallado
ng test --code-coverage --watch=false --browsers=ChromeHeadless
```

## 🚀 Integración con Spring Boot Backend

### Endpoints de API Utilizados
```typescript
interface APIEndpoints {
  // Autenticación
  auth: {
    login: 'POST /api/auth/login',
    register: 'POST /api/auth/register', 
    refresh: 'POST /api/auth/refresh',
    logout: 'POST /api/auth/logout'
  };

  // Gestión de Destinos
  destinations: {
    getAll: 'GET /api/destinations?page={}&size={}',
    getById: 'GET /api/destinations/{id}',
    search: 'GET /api/destinations/search?query={}'
  };

  // Gestión de Hoteles  
  hotels: {
    getAll: 'GET /api/hotels?page={}&size={}',
    getById: 'GET /api/hotels/{id}',
    getByDestination: 'GET /api/hotels/destination/{destinationId}',
    search: 'GET /api/hotels/search?query={}&destination={}&minPrice={}&maxPrice={}'
  };

  // Sistema de Reservas
  bookings: {
    create: 'POST /api/bookings',
    getByUser: 'GET /api/bookings/user?page={}&size={}',
    getById: 'GET /api/bookings/{id}',
    cancel: 'PUT /api/bookings/{id}/cancel',
    confirm: 'PUT /api/bookings/{id}/confirm'
  };

  // Contacto y Soporte
  contact: {
    create: 'POST /api/contact',
    getAll: 'GET /api/contact?page={}&size={}&status={}', // Admin only
    updateStatus: 'PUT /api/contact/{id}/status'  // Admin only
  };

  // Administración
  admin: {
    dashboard: 'GET /api/admin/dashboard/stats',
    bookings: 'GET /api/admin/bookings?page={}&size={}&status={}',
    users: 'GET /api/admin/users?page={}&size={}',
    reports: 'GET /api/admin/reports?type={}&dateFrom={}&dateTo={}'
  };
}
```

### Modelo de Datos Compartido
```typescript
// Interfaces que corresponden con DTOs del backend
interface HotelResponse {
  id: number;
  name: string;
  description: string;
  address: string;
  rating: number;
  imageUrl: string;
  destination: DestinationResponse;
  roomTypes: RoomTypeResponse[];
  amenities: string[];
  priceRange: {
    min: number;
    max: number;
  };
}

interface BookingRequest {
  hotelId: number;
  roomTypeId: number;
  checkInDate: string;    // ISO date format
  checkOutDate: string;   // ISO date format
  guestCount: number;
  specialRequests?: string;
  selectedActivities?: number[];
}
```

### Manejo de Errores del Backend
```typescript
// Interceptor para manejo centralizado de errores
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Ha ocurrido un error inesperado';
        
        switch (error.status) {
          case 400:
            errorMessage = 'Datos inválidos enviados al servidor';
            break;
          case 401:
            errorMessage = 'No autorizado. Por favor inicia sesión';
            this.authService.logout();
            break;
          case 403:
            errorMessage = 'No tienes permisos para realizar esta acción';
            break;
          case 404:
            errorMessage = 'El recurso solicitado no fue encontrado';
            break;
          case 500:
            errorMessage = 'Error interno del servidor. Intenta nuevamente';
            break;
        }
        
        return throwError(() => new Error(errorMessage));
      })
    );
  }
}
```

## 🧪 Testing y Calidad de Código

### Estrategia de Testing
```typescript
// Testing de Componentes
describe('HotelsComponent', () => {
  let component: HotelsComponent;
  let hotelService: jasmine.SpyObj<HotelService>;
  
  beforeEach(() => {
    const spy = jasmine.createSpyObj('HotelService', ['getAllHotels']);
    
    TestBed.configureTestingModule({
      declarations: [HotelsComponent],
      providers: [{ provide: HotelService, useValue: spy }]
    });
    
    hotelService = TestBed.inject(HotelService) as jasmine.SpyObj<HotelService>;
  });

  it('should load hotels on init', () => {
    const mockHotels = [{ id: 1, name: 'Test Hotel' }];
    hotelService.getAllHotels.and.returnValue(of(mockHotels));
    
    component.ngOnInit();
    
    expect(hotelService.getAllHotels).toHaveBeenCalled();
    expect(component.hotels).toEqual(mockHotels);
  });
});

// Testing de Servicios
describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });
    
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  it('should authenticate user and store token', () => {
    const mockUser = { id: 1, email: 'test@test.com' };
    const loginData = { email: 'test@test.com', password: 'password' };
    
    service.login(loginData).subscribe(user => {
      expect(user).toEqual(mockUser);
      expect(service.isAuthenticated()).toBe(true);
    });
    
    const req = httpMock.expectOne('/api/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush({ user: mockUser, token: 'fake-jwt-token' });
  });
});
```

### Cobertura de Testing
- **Componentes**: >80% de cobertura en lógica de negocio
- **Servicios**: >90% de cobertura en métodos públicos  
- **Guards e Interceptors**: 100% de cobertura
- **Utilities**: 100% de cobertura

### Linting y Calidad
```typescript
// .eslintrc.json configurado con reglas estrictas
{
  "extends": [
    "@angular-eslint/recommended",
    "@typescript-eslint/recommended"
  ],
  "rules": {
    "@typescript-eslint/no-unused-vars": "error",
    "@angular-eslint/component-class-suffix": "error",
    "@angular-eslint/directive-selector": "error",
    "prefer-const": "error",
    "no-console": "warn"
  }
}
```

## 📱 Optimización y Performance

### Estrategias de Optimización Implementadas

#### Lazy Loading de Módulos
```typescript
// Carga diferida del módulo administrativo
const routes: Routes = [
  {
    path: 'admin',
    loadChildren: () => import('./pages/admin/admin.module').then(m => m.AdminModule),
    canActivate: [AdminGuard]
  }
];
```

#### OnPush Change Detection Strategy
```typescript
// Componentes optimizados con OnPush
@Component({
  selector: 'app-hotel-card',
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `...`
})
export class HotelCardComponent {
  @Input() hotel!: HotelResponse;
}
```

#### Virtual Scrolling para Listas Grandes
```html
<!-- Implementación de virtual scrolling -->
<cdk-virtual-scroll-viewport itemSize="200" class="hotel-list-viewport">
  <div *cdkVirtualFor="let hotel of hotels">
    <app-hotel-card [hotel]="hotel"></app-hotel-card>
  </div>
</cdk-virtual-scroll-viewport>
```

#### Tree Shaking y Bundle Optimization
```typescript
// Imports específicos para reducir bundle size
import { map, filter, take } from 'rxjs/operators';
import { Observable } from 'rxjs';

// Evitar imports completos
// ❌ import * as _ from 'lodash';  
// ✅ import { debounce } from 'lodash-es';
```

### Métricas de Performance Objetivo
- **First Contentful Paint**: < 2s
- **Largest Contentful Paint**: < 3s  
- **Time to Interactive**: < 4s
- **Bundle Size**: < 2MB (gzipped)
- **Lighthouse Score**: > 90 (Performance, Accessibility, Best Practices)

## � Deployment y Producción

### Build para Producción
```bash
# Build optimizado con todas las optimizaciones
ng build --configuration=production

# Archivos generados en dist/caribe-vibes-frontend/
├── index.html
├── main.[hash].js
├── polyfills.[hash].js
├── runtime.[hash].js
├── styles.[hash].css
└── assets/
```

### Integración con Spring Boot
```bash
# 1. Generar build de producción
npm run build:prod

# 2. Copiar archivos al backend (automatizable con script)
cp -r dist/caribe-vibes-frontend/* ../../../java/resources/static/

# 3. Configurar Spring Boot application.properties
spring.web.resources.static-locations=classpath:/static/
spring.mvc.view.prefix=/
spring.mvc.view.suffix=.html
```

### Variables de Entorno para Producción
```typescript
// environment.prod.ts optimizado para producción
export const environment = {
  production: true,
  apiUrl: 'https://api.caribevibes.com/api',
  backendUrl: 'https://api.caribevibes.com',
  frontendUrl: 'https://caribevibes.com',
  enableDebugMode: false,
  logLevel: 'error',
  cacheTimeout: 300000, // 5 minutos
  maxRetryAttempts: 3
};
```

### Docker Configuration
```dockerfile
# Multi-stage build para optimizar imagen
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build:prod

FROM nginx:alpine
COPY --from=build /app/dist/caribe-vibes-frontend /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
```

### Nginx Configuration
```nginx
# nginx.conf para SPA routing
server {
    listen 80;
    server_name caribevibes.com;
    root /usr/share/nginx/html;
    
    # Gzip compression
    gzip on;
    gzip_types text/css application/javascript image/svg+xml;
    
    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # SPA routing - redirect all requests to index.html
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API proxy to backend
    location /api/ {
        proxy_pass http://backend:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 📋 Checklist de Desarrollo

### 🔍 Antes de Commit
- [ ] Ejecutar linting: `npm run lint`
- [ ] Ejecutar tests: `npm test`  
- [ ] Verificar build: `npm run build`
- [ ] Verificar responsive en DevTools
- [ ] Validar accesibilidad con Lighthouse
- [ ] Revisar console.log en producción

### 🚀 Antes de Deploy
- [ ] Tests E2E passing
- [ ] Performance audit > 90
- [ ] Security audit sin vulnerabilidades críticas
- [ ] Backup de base de datos
- [ ] Verificar variables de entorno
- [ ] SSL/TLS configurado correctamente

### 📊 Monitoreo Post-Deploy
- [ ] Verificar métricas de performance
- [ ] Monitorear logs de errores
- [ ] Validar integración con backend
- [ ] Verificar funcionalidades críticas
- [ ] Revisar feedback de usuarios

## 🤝 Guía de Contribución

### Estándares de Código
```typescript
// Nomenclatura de archivos y componentes
my-component.component.ts     // PascalCase para clases
my-service.service.ts         // camelCase para archivos
my-interface.model.ts         // Descriptivo para modelos

// Estructura de componentes
@Component({
  selector: 'app-my-component',     // Prefijo 'app-'
  templateUrl: './my-component.component.html',
  styleUrls: ['./my-component.component.scss']
})
export class MyComponent implements OnInit {
  // Propiedades públicas primero
  public title = 'Mi Componente';
  
  // Propiedades privadas después
  private subscription$ = new Subject<void>();
  
  // Constructor con inyección de dependencias
  constructor(
    private myService: MyService,
    private router: Router
  ) {}
  
  // Lifecycle hooks
  ngOnInit(): void {
    this.loadData();
  }
  
  // Métodos públicos
  public onButtonClick(): void {
    // Implementación
  }
  
  // Métodos privados
  private loadData(): void {
    // Implementación
  }
  
  // Cleanup
  ngOnDestroy(): void {
    this.subscription$.next();
    this.subscription$.complete();
  }
}
```

### Git Workflow
```bash
# Crear rama para nueva funcionalidad
git checkout -b feature/hotel-reviews-system

# Commits descriptivos con convención
git commit -m "feat: agregar sistema de reseñas de hoteles"
git commit -m "fix: corregir filtro por calificación en hoteles"
git commit -m "docs: actualizar README con nuevas funcionalidades"

# Push y crear Pull Request
git push origin feature/hotel-reviews-system
```

### Convención de Commits
- `feat:` Nueva funcionalidad
- `fix:` Corrección de bug
- `docs:` Cambios en documentación
- `style:` Cambios de formato/estilo
- `refactor:` Refactorización de código
- `test:` Agregar o modificar tests
- `perf:` Mejoras de performance

## 🆘 Troubleshooting y FAQ

### Problemas Comunes

#### Error: "The `cwd` option must be a path to a directory"
```bash
# Verificar que estás en el directorio correcto
pwd
# Debe mostrar: .../caribeVibes/src/main/resources/frontend

# Limpiar node_modules y reinstalar
rm -rf node_modules package-lock.json
npm install
```

#### Error: "Cannot find module" durante build
```bash
# Verificar versión de Node.js
node --version  # Debe ser >= 18.13.0

# Limpiar cache de npm
npm cache clean --force

# Reinstalar Angular CLI
npm uninstall -g @angular/cli
npm install -g @angular/cli@latest
```

#### Problemas de CORS en desarrollo
```javascript
// Verificar configuración de proxy en proxy.conf.json
{
  "/api/*": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "debug"
  }
}

// Iniciar con proxy explícito
ng serve --proxy-config proxy.conf.json
```

#### Error de autenticación JWT
```typescript
// Verificar token en localStorage
localStorage.getItem('auth_token');

// Limpiar datos de autenticación
localStorage.removeItem('auth_token');
localStorage.removeItem('refresh_token');
```

### Enlaces Útiles
- [Documentación Angular](https://angular.io/docs)
- [Bootstrap 5 Documentation](https://getbootstrap.com/docs/5.3/)
- [RxJS Operators](https://rxjs.dev/guide/operators)
- [TypeScript Handbook](https://www.typescriptlang.org/docs/)
- [Font Awesome Icons](https://fontawesome.com/icons)

## 📞 Soporte y Contacto

### Información del Proyecto
- **Nombre**: Caribe Vibes - Frontend Angular
- **Versión**: 1.0.0
- **Autor**: IngAlexfit
- **Repositorio**: [caribeVibes-turismo](https://github.com/IngAlexfit/caribeVibes-turismo)
- **Licencia**: MIT

### Contacto del Equipo
- **Email**: desarrollo@caribevibes.com
- **Issues**: [GitHub Issues](https://github.com/IngAlexfit/caribeVibes-turismo/issues)
- **Documentación**: [Wiki del Proyecto](https://github.com/IngAlexfit/caribeVibes-turismo/wiki)

### Contribución
Para contribuir al proyecto:
1. Fork del repositorio
2. Crear rama de feature: `git checkout -b feature/nueva-funcionalidad`  
3. Commit de cambios: `git commit -m 'feat: agregar nueva funcionalidad'`
4. Push a la rama: `git push origin feature/nueva-funcionalidad`
5. Abrir Pull Request con descripción detallada

---

<div align="center">
  <p><strong>🏝️ Caribe Vibes - Descubre el Caribe a tu manera</strong></p>
  <p>Desarrollado con ❤️ para crear experiencias de viaje inolvidables</p>
  
  [![Made with Angular](https://img.shields.io/badge/Made%20with-Angular-E23237?style=for-the-badge&logo=angular&logoColor=white)](https://angular.io)
  [![Powered by Spring Boot](https://img.shields.io/badge/Powered%20by-Spring%20Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
</div>
