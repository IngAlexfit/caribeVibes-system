import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule, DatePipe, CurrencyPipe, DecimalPipe, SlicePipe } from '@angular/common';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

// Interceptors
import { AuthInterceptor } from './core/interceptors/auth.interceptor';

// Components
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { FooterComponent } from './shared/components/footer/footer.component';
import { LoadingComponent } from './shared/components/loading/loading.component';
import { BookingModalComponent } from './shared/components/booking-modal/booking-modal.component';

// Pages
import { HomeComponent } from './pages/home/home.component';
import { DestinationsComponent } from './pages/destinations/destinations.component';
import { HotelsComponent } from './pages/hotels/hotels.component';
import { LoginComponent } from './pages/auth/login/login.component';
import { RegisterComponent } from './pages/auth/register/register.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { BookingsComponent } from './pages/bookings/bookings.component';
import { BookingDetailComponent } from './pages/booking-detail/booking-detail.component';
import { ContactComponent } from './pages/contact/contact.component';

// Admin Pages
import { AdminDashboardComponent } from './pages/admin/dashboard/admin-dashboard.component';
import { AdminContactsComponent } from './pages/admin/contacts/admin-contacts.component';
import { AdminBookingsComponent } from './pages/admin/bookings/admin-bookings.component';
import { AdminHotelsComponent } from './pages/admin/hotels/admin-hotels.component';
import { AdminDestinationsComponent } from './pages/admin/destinations/admin-destinations.component';

// Feature Modules
import { HotelReviewsModule } from './features/hotel-reviews/hotel-reviews.module';

/**
 * @class AppModule
 * @description Módulo principal de la aplicación Angular.
 * Define todos los componentes, servicios, módulos e interceptores que se utilizarán en la aplicación.
 */
@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    FooterComponent,
    LoadingComponent,
    BookingModalComponent,
    HomeComponent,
    DestinationsComponent,
    HotelsComponent,
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
    BookingsComponent,
    BookingDetailComponent,
    ContactComponent,
    // Admin Components
    AdminDashboardComponent,
    AdminContactsComponent,
    AdminBookingsComponent,
    AdminHotelsComponent,
    AdminDestinationsComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    AppRoutingModule,
    HotelReviewsModule
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [
    DatePipe,
    CurrencyPipe,
    DecimalPipe,
    SlicePipe,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
