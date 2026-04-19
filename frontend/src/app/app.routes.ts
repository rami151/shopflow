import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { guestGuard } from './core/guards/guest.guard';
import { roleGuard } from './core/guards/role.guard';
import { UserRole } from './core/models/user.model';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/home/pages/home-page/home-page.component').then(m => m.HomePageComponent)
  },
  {
    path: 'products',
    loadComponent: () => import('./features/products/pages/product-catalog/product-catalog.component').then(m => m.ProductCatalogComponent)
  },
  {
    path: 'products/:id',
    loadComponent: () => import('./features/products/pages/product-detail/product-detail.component').then(m => m.ProductDetailComponent)
  },
  {
    path: 'auth',
    canActivate: [guestGuard],
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/pages/login/login.component').then(m => m.LoginComponent)
      },
      {
        path: 'register',
        loadComponent: () => import('./features/auth/pages/register/register.component').then(m => m.RegisterComponent)
      }
    ]
  },
  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cart/pages/cart-page/cart-page.component').then(m => m.CartPageComponent)
  },
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () => import('./features/checkout/pages/checkout-page/checkout-page.component').then(m => m.CheckoutPageComponent)
  },
  {
    path: 'order-confirmation/:orderId',
    canActivate: [authGuard],
    loadComponent: () => import('./features/checkout/pages/order-confirmation/order-confirmation.component').then(m => m.OrderConfirmationComponent)
  },
  {
    path: 'dashboard',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.CUSTOMER] },
    loadComponent: () => import('./features/customer-dashboard/pages/dashboard-page/dashboard-page.component').then(m => m.DashboardPageComponent)
  },
  {
    path: 'dashboard/orders',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.CUSTOMER] },
    loadComponent: () => import('./features/customer-dashboard/pages/my-orders/my-orders.component').then(m => m.MyOrdersComponent)
  },
  {
    path: 'dashboard/reviews',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.CUSTOMER] },
    loadComponent: () => import('./features/customer-dashboard/pages/my-reviews/my-reviews.component').then(m => m.MyReviewsComponent)
  },
  {
    path: 'seller',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.SELLER, UserRole.ADMIN] },
    loadComponent: () => import('./features/seller-dashboard/pages/dashboard-page/dashboard-page.component').then(m => m.SellerDashboardPageComponent)
  },
  {
    path: 'seller/products',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.SELLER, UserRole.ADMIN] },
    loadComponent: () => import('./features/seller-dashboard/pages/my-products/my-products.component').then(m => m.MyProductsComponent)
  },
  {
    path: 'seller/orders',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.SELLER, UserRole.ADMIN] },
    loadComponent: () => import('./features/seller-dashboard/pages/received-orders/received-orders.component').then(m => m.ReceivedOrdersComponent)
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.ADMIN] },
    loadComponent: () => import('./features/admin-dashboard/pages/dashboard-page/dashboard-page.component').then(m => m.AdminDashboardPageComponent)
  },
  {
    path: 'admin/categories',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.ADMIN] },
    loadComponent: () => import('./features/admin-dashboard/pages/manage-categories/manage-categories.component').then(m => m.ManageCategoriesComponent)
  },
  {
    path: 'admin/coupons',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.ADMIN] },
    loadComponent: () => import('./features/admin-dashboard/pages/manage-coupons/manage-coupons.component').then(m => m.ManageCouponsComponent)
  },
  {
    path: 'admin/users',
    canActivate: [authGuard, roleGuard],
    data: { roles: [UserRole.ADMIN] },
    loadComponent: () => import('./features/admin-dashboard/pages/manage-users/manage-users.component').then(m => m.ManageUsersComponent)
  },
  {
    path: '**',
    redirectTo: ''
  }
];