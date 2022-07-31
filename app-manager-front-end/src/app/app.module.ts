import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { MenuItemComponent } from './components/header/menu/menu-item/menu-item.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import { MenuComponent } from './components/header/menu/menu.component';
import { RouterModule, Routes } from "@angular/router";
import { HomeComponent } from './components/home/home.component';
import { LogInComponent } from './components/auth/log-in/log-in.component';
import { EmailFieldComponent } from './components/auth/components/email-field/email-field.component';
import { PasswordFieldComponent } from './components/auth/components/password-field/password-field.component';
import { ForgetFieldComponent } from './components/auth/components/forget-field/forget-field.component';
import { ErrorFieldComponent } from './components/auth/components/error-field/error-field.component';
import { ButtonComponent } from './components/auth/components/button/button.component';
import { SignUpComponent } from './components/auth/sign-up/sign-up.component';
import { RePasswordFieldComponent } from './components/auth/components/re-password-field/re-password-field.component';
import { UpdateComponent } from './components/auth/update/update.component';
import { ResetEmailComponent } from './components/auth/reset-email/reset-email.component';
import { AppsComponent } from "./components/apps/apps.component";
import {ActiveUserGuard} from "./guard/active-user/active-user.guard";
import {UnAuthUserGuard} from "./guard/unauth-user/un-auth-user.guard";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import { AppItemComponent } from './components/apps/app-item/app-item.component';
import { PaginationComponent } from './components/apps/pagination/pagination.component';
import { CreateAppFormComponent } from './components/apps/forms/create-app/create-app-form/create-app-form.component';
import { CreateAppComponent } from './components/apps/forms/create-app/create-app.component';
import { UpdateAppComponent } from './components/apps/forms/update-app/update-app.component';
import { UpdateAppFormComponent } from './components/apps/forms/update-app/update-app-form/update-app-form.component';
import { NameFieldComponent } from './components/apps/forms/components/name-field/name-field.component';
import { StatsFormComponent } from './components/stats/forms/stats-form/stats-form.component';
import { DateFieldComponent } from './components/stats/forms/components/date-field/date-field.component';
import { SelectFieldComponent } from './components/stats/forms/components/select-field/select-field.component';
import { CreateEventComponent } from './components/events/forms/create-event/create-event.component';
import { CreateEventFormComponent } from './components/events/forms/create-event/create-event-form/create-event-form.component';
import { ExtraInfFieldComponent } from './components/events/forms/components/extra-inf-field/extra-inf-field.component';
import { EventNameFieldComponent } from './components/events/forms/components/event-name-field/event-name-field.component';
import {UpdateConfirmedUserGuard} from "./guard/update-confirmed-user/update-confirmed-user.guard";
import { ConfirmResetComponent } from './components/auth/confirm-reset/confirm-reset.component';
import {UpdateNotConfirmedUserGuard} from "./guard/update-not-confirmed-user/update-not-confirmed-user.guard";
import { CodeFieldComponent } from './components/auth/components/code-field/code-field.component';
import { SendAgainLinkComponent } from './components/auth/components/send-again-link/send-again-link.component';
import { CounterComponent } from './components/auth/components/counter/counter.component';
import { DropdownComponent } from './components/apps/dropdown/dropdown.component';
import { SearchPanelComponent } from './components/apps/search-panel/search-panel.component';
import { GoogleOauthButtonComponent } from './components/auth/components/google-oauth-button/google-oauth-button.component';
import {RefreshTokenInterceptor} from "./interceptor/refresh-token/refresh-token.interceptor";
import {AccessTokenInterceptor} from "./interceptor/access-token/access-token.interceptor";
import { StatsComponent } from './components/stats/stats.component';
import { AppChartComponent } from './components/stats/app-chart/app-chart.component';
import { StatsButtonComponent } from './components/stats/forms/components/stats-button/stats-button.component';
import { EventItemComponent } from './components/events/event-item/event-item.component';
import { EventsComponent } from './components/events/events.component';
import { UpdateEventComponent } from './components/events/forms/update-event/update-event.component';
import { UpdateEventFormComponent } from './components/events/forms/update-event/update-event-form/update-event-form.component';

const appRoutes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'apps', component: AppsComponent, canActivate: [ActiveUserGuard]},
  {path: 'app/:id/stats', component: StatsComponent, canActivate: [ActiveUserGuard]},
  {path: 'app/:id/update', component: UpdateAppComponent, canActivate: [ActiveUserGuard]},
  {path: 'app/:id/event/create', component: CreateEventComponent, canActivate: [ActiveUserGuard]},
  {path: 'app/:appId/event/:eventId/update', component: UpdateEventComponent, canActivate: [ActiveUserGuard]},
  {path: 'app/:id/events', component: EventsComponent, canActivate: [ActiveUserGuard]},
  {path: 'app/create', component: CreateAppComponent, canActivate: [ActiveUserGuard]},
  {path: 'auth/log-in', component: LogInComponent, canActivate: [UnAuthUserGuard]},
  {path: 'auth/sign-up', component: SignUpComponent, canActivate: [UnAuthUserGuard]},
  {path: 'auth/reset', component: ResetEmailComponent, canActivate: [UnAuthUserGuard]},
  {path: 'auth/reset/confirm', component: ConfirmResetComponent, canActivate: [UpdateNotConfirmedUserGuard]},
  {path: 'auth/update', component: UpdateComponent, canActivate: [UpdateConfirmedUserGuard]},
]

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    MenuItemComponent,
    MenuComponent,
    HomeComponent,
    LogInComponent,
    EmailFieldComponent,
    PasswordFieldComponent,
    ForgetFieldComponent,
    ErrorFieldComponent,
    ButtonComponent,
    SignUpComponent,
    RePasswordFieldComponent,
    UpdateComponent,
    ResetEmailComponent,
    AppsComponent,
    AppItemComponent,
    PaginationComponent,
    CreateAppFormComponent,
    CreateAppComponent,
    UpdateAppComponent,
    UpdateAppFormComponent,
    NameFieldComponent,
    StatsFormComponent,
    DateFieldComponent,
    SelectFieldComponent,
    CreateEventComponent,
    CreateEventFormComponent,
    ExtraInfFieldComponent,
    EventNameFieldComponent,
    ConfirmResetComponent,
    CodeFieldComponent,
    SendAgainLinkComponent,
    CounterComponent,
    DropdownComponent,
    SearchPanelComponent,
    GoogleOauthButtonComponent,
    StatsComponent,
    AppChartComponent,
    StatsButtonComponent,
    EventItemComponent,
    EventsComponent,
    UpdateEventComponent,
    UpdateEventFormComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(appRoutes),
    ReactiveFormsModule
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: RefreshTokenInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AccessTokenInterceptor,
      multi: true
    }],
  bootstrap: [AppComponent]
})
export class AppModule { }

platformBrowserDynamic().bootstrapModule(AppModule);

