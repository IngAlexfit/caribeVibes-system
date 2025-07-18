import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

// Guards
import { AuthGuard } from './core/guards/auth.guard';
import { AdminGuard } from './core/guards/admin.guard';
import { UserGuard } from './core/guards/user.guard';

// Components
import { HomeComponent } from './pages/home/home.component';
import { DestinationsComponent } from './pages/destinations/destinations.component';
import { HotelsComponent } from './pages/hotels/hotels.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { BookingsComponent } from './pages/bookings/bookings.component';
import { BookingDetailComponent } from './pages/booking-detail/booking-detail.component';
import { ContactComponent } from './pages/contact/contact.component';

// Admin Components
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard.component';
import { AdminContactsComponent } from './pages/admin/contacts/admin-contacts.component';
import { AdminBookingsComponent } from './pages/admin/bookings/admin-bookings.component';
import { AdminHotelsComponent } from './pages/admin/hotels/admin-hotels.component';
import { AdminDestinationsComponent } from './pages/admin/destinations/admin-destinations.component';

/**
 * @constant {Routes} routes
 * @description Configuración de rutas de la aplicación Caribe Vibes
 * Define todas las rutas disponibles y sus reglas de acceso
 */
const routes: Routes = [
  // Public Routes
  { path: '', component: HomeComponent },
  { path: 'home', redirectTo: '', pathMatch: 'full' },
  
  // Auth Routes
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },
  { path: 'login', redirectTo: 'auth/login', pathMatch: 'full' },
  { path: 'register', redirectTo: 'auth/register', pathMatch: 'full' },
  { path: 'registro', redirectTo: 'auth/register', pathMatch: 'full' },
  
  // Protected Routes
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [UserGuard]
  },
  
  // Public pages
  { path: 'destinations', component: DestinationsComponent },
  { path: 'destinations/:id', component: DestinationsComponent }, // Detalles del destino
  { path: 'destinos', redirectTo: 'destinations', pathMatch: 'full' },
  { path: 'hotels', component: HotelsComponent },
  { path: 'hotels/:id', component: HotelsComponent }, // Detalles del hotel
  { path: 'hoteles', redirectTo: 'hotels', pathMatch: 'full' },
  { path: 'contact', component: ContactComponent },
  { path: 'contacto', redirectTo: 'contact', pathMatch: 'full' },
  
  // Protected pages
  { 
    path: 'bookings', 
    component: BookingsComponent,
    canActivate: [UserGuard]
  },
  { 
    path: 'bookings/:id', 
    component: BookingDetailComponent,
    canActivate: [UserGuard]
  },
  { path: 'reservas', redirectTo: 'bookings', pathMatch: 'full' },
  
  // Admin Routes - Protected with AdminGuard
  { 
    path: 'admin', 
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full'
      },
      {
        path: 'dashboard',
        component: AdminDashboardComponent,
        canActivate: [AdminGuard]
      },
      {
        path: 'contacts',
        component: AdminContactsComponent,
        canActivate: [AdminGuard]
      },
      {
        path: 'bookings',
        component: AdminBookingsComponent,
        canActivate: [AdminGuard]
      },
      {
        path: 'hotels',
        component: AdminHotelsComponent,
        canActivate: [AdminGuard]
      },
      {
        path: 'destinations',
        component: AdminDestinationsComponent,
        canActivate: [AdminGuard]
      }
    ]
  },
  
  // Wildcard Route
  { path: '**', redirectTo: '', pathMatch: 'full' }
];

/**
 * @class AppRoutingModule
 * @description Módulo de enrutamiento principal de la aplicación.
 * Configura las rutas y las opciones de enrutamiento.
 */
@NgModule({
  imports: [RouterModule.forRoot(routes, { 
    enableTracing: false,
    scrollPositionRestoration: 'top'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
