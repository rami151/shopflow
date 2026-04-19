import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout';
import { HomeComponent } from './pages/home';
import { ProductsComponent } from './pages/products';
import { PlaceholderComponent } from './pages/placeholder';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'products', component: ProductsComponent },
      {
        path: 'products/:id',
        component: PlaceholderComponent,
        data: { icon: '📦', title: 'Product Details', description: 'View full product details and reviews' }
      },
      {
        path: 'cart',
        component: PlaceholderComponent,
        data: { icon: '🛒', title: 'Shopping Cart', description: 'Review and manage your items' }
      },
      {
        path: 'checkout',
        component: PlaceholderComponent,
        data: { icon: '💳', title: 'Checkout', description: 'Complete your purchase' }
      },
      {
        path: 'orders',
        component: PlaceholderComponent,
        data: { icon: '📋', title: 'My Orders', description: 'Track your orders and deliveries' }
      },
      {
        path: 'account',
        component: PlaceholderComponent,
        data: { icon: '👤', title: 'My Account', description: 'Manage your profile and preferences' }
      },
      {
        path: 'dashboard',
        component: PlaceholderComponent,
        data: { icon: '📊', title: 'Dashboard', description: 'View your statistics and activity' }
      },
      {
        path: 'categories',
        component: PlaceholderComponent,
        data: { icon: '🏷️', title: 'Categories', description: 'Browse products by category' }
      },
      {
        path: 'auth/login',
        component: PlaceholderComponent,
        data: { icon: '🔐', title: 'Sign In', description: 'Login to your account' }
      },
      {
        path: 'auth/register',
        component: PlaceholderComponent,
        data: { icon: '✍️', title: 'Create Account', description: 'Join ShopFlow today' }
      },
    ]
  }
];
