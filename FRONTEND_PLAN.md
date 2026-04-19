# ShopFlow Frontend Development Plan
## Angular 17+ Architecture & Implementation Strategy

---

## 1. PROJECT OVERVIEW

### 1.1 Context
- **Framework:** Angular 17+ (standalone components)
- **Language:** TypeScript (strict mode)
- **Grade Weight:** 5 points (out of 20 total)
- **Duration:** Final phase of 3-week project
- **Backend:** Spring Boot REST API (already defined in CLAUDE.md)

### 1.2 Success Criteria (from grading rubric)
- **API Integration & JWT (2 pts):** HTTP calls, interceptor, refresh token, route guards
- **UI Features (2 pts):** Complete purchase flow (catalog → cart → order)
- **Code Quality & UX (1 pt):** Responsive design, reusable components, state management

---

## 2. TECH STACK & TOOLS

### 2.1 Core Dependencies
```json
{
  "dependencies": {
    "@angular/core": "^17.0.0",
    "@angular/common": "^17.0.0",
    "@angular/router": "^17.0.0",
    "@angular/forms": "^17.0.0",
    "@angular/platform-browser": "^17.0.0",
    "@angular/platform-browser-dynamic": "^17.0.0",
    "rxjs": "~7.8.0",
    "tslib": "^2.3.0",
    "zone.js": "~0.14.0"
  },
  "devDependencies": {
    "@angular-devkit/build-angular": "^17.0.0",
    "@angular/cli": "^17.0.0",
    "@angular/compiler-cli": "^17.0.0",
    "typescript": "~5.2.2"
  }
}
```

### 2.2 UI/Design Tools
- **CSS Framework:** Tailwind CSS 3.x (utility-first, mobile-first, modern)
  - Why: Fast prototyping, small bundle size, great with Angular
  - Alternative considered: Bootstrap 5 (rejected — too opinionated)
  
- **Icons:** Heroicons or Lucide Angular
  - CDN fallback for rapid prototyping
  
- **Animations:** Angular Animations API
  - Subtle transitions, no heavy libraries
  
- **Responsive Design:** Mobile-first breakpoints
  - sm: 640px | md: 768px | lg: 1024px | xl: 1280px

### 2.3 State Management
- **Strategy:** Service-based reactive state (RxJS BehaviorSubject)
- **Why NOT NgRx:** Overkill for a 3-week university project
- **Pattern:** Smart/Dumb component architecture
  - Smart: Container components with injected services
  - Dumb: Presentation components with @Input/@Output

### 2.4 Development Tools
- **VS Code Extensions:**
  - Angular Language Service
  - Prettier
  - ESLint
  - Tailwind CSS IntelliSense
  
- **Browser DevTools:**
  - Angular DevTools (Chrome extension)
  - Network tab for API debugging

---

## 3. FILE STRUCTURE

### 3.1 Directory Layout
```
frontend/
├── src/
│   ├── app/
│   │   ├── core/                          ← Singleton services, guards, interceptors
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts          ← Protects authenticated routes
│   │   │   │   ├── role.guard.ts          ← Checks user role (CUSTOMER/SELLER/ADMIN)
│   │   │   │   └── guest.guard.ts         ← Redirects authenticated users from login/register
│   │   │   ├── interceptors/
│   │   │   │   ├── jwt.interceptor.ts     ← Injects Bearer token in every request
│   │   │   │   ├── error.interceptor.ts   ← Global error handler (401 → refresh token)
│   │   │   │   └── loading.interceptor.ts ← Shows/hides global spinner
│   │   │   ├── services/
│   │   │   │   ├── auth.service.ts        ← Login, register, refresh, logout, token storage
│   │   │   │   ├── token.service.ts       ← LocalStorage token management (secure)
│   │   │   │   └── toast.service.ts       ← Global notification system
│   │   │   └── models/
│   │   │       ├── user.model.ts          ← User interface (id, email, role, etc.)
│   │   │       ├── jwt-response.model.ts  ← { access_token, refresh_token }
│   │   │       └── api-response.model.ts  ← Generic API response wrapper
│   │   │
│   │   ├── features/                      ← Feature modules (lazy-loaded when possible)
│   │   │   ├── auth/
│   │   │   │   ├── pages/
│   │   │   │   │   ├── login/
│   │   │   │   │   │   ├── login.component.ts
│   │   │   │   │   │   ├── login.component.html
│   │   │   │   │   │   └── login.component.css
│   │   │   │   │   └── register/
│   │   │   │   │       ├── register.component.ts
│   │   │   │   │       ├── register.component.html
│   │   │   │   │       └── register.component.css
│   │   │   │   └── auth-routing.module.ts
│   │   │   │
│   │   │   ├── home/
│   │   │   │   ├── pages/
│   │   │   │   │   └── home-page/
│   │   │   │   │       ├── home-page.component.ts
│   │   │   │   │       ├── home-page.component.html
│   │   │   │   │       └── home-page.component.css
│   │   │   │   ├── components/
│   │   │   │   │   ├── hero-banner/       ← Promo banner with CTA
│   │   │   │   │   ├── featured-products/ ← Top-selling products grid
│   │   │   │   │   └── category-showcase/ ← Category cards
│   │   │   │   └── home-routing.module.ts
│   │   │   │
│   │   │   ├── products/
│   │   │   │   ├── pages/
│   │   │   │   │   ├── product-catalog/   ← Grid + filters + pagination
│   │   │   │   │   │   ├── product-catalog.component.ts
│   │   │   │   │   │   ├── product-catalog.component.html
│   │   │   │   │   │   └── product-catalog.component.css
│   │   │   │   │   └── product-detail/    ← Product page + variants + reviews
│   │   │   │   │       ├── product-detail.component.ts
│   │   │   │   │       ├── product-detail.component.html
│   │   │   │   │       └── product-detail.component.css
│   │   │   │   ├── components/
│   │   │   │   │   ├── product-card/      ← Reusable product card (grid item)
│   │   │   │   │   ├── product-filters/   ← Category, price range, rating filters
│   │   │   │   │   ├── product-search/    ← Search bar with autocomplete
│   │   │   │   │   ├── variant-selector/  ← Size/Color dropdown
│   │   │   │   │   └── review-list/       ← Product reviews display
│   │   │   │   ├── services/
│   │   │   │   │   └── product.service.ts ← GET products, search, filters, top-selling
│   │   │   │   └── products-routing.module.ts
│   │   │   │
│   │   │   ├── cart/
│   │   │   │   ├── pages/
│   │   │   │   │   └── cart-page/
│   │   │   │   │       ├── cart-page.component.ts
│   │   │   │   │       ├── cart-page.component.html
│   │   │   │   │       └── cart-page.component.css
│   │   │   │   ├── components/
│   │   │   │   │   ├── cart-item/         ← Single line item (qty selector, remove)
│   │   │   │   │   ├── cart-summary/      ← Subtotal, fees, coupon, total
│   │   │   │   │   └── coupon-input/      ← Promo code input + validation
│   │   │   │   ├── services/
│   │   │   │   │   └── cart.service.ts    ← GET cart, add/update/remove items, apply coupon
│   │   │   │   └── cart-routing.module.ts
│   │   │   │
│   │   │   ├── checkout/
│   │   │   │   ├── pages/
│   │   │   │   │   ├── checkout-page/     ← Address selection + order recap
│   │   │   │   │   │   ├── checkout-page.component.ts
│   │   │   │   │   │   ├── checkout-page.component.html
│   │   │   │   │   │   └── checkout-page.component.css
│   │   │   │   │   └── order-confirmation/ ← Success page after order
│   │   │   │   │       ├── order-confirmation.component.ts
│   │   │   │   │       ├── order-confirmation.component.html
│   │   │   │   │       └── order-confirmation.component.css
│   │   │   │   ├── components/
│   │   │   │   │   ├── address-selector/  ← Radio list of saved addresses
│   │   │   │   │   ├── order-summary/     ← Final recap before payment
│   │   │   │   │   └── payment-form/      ← Mock payment (not real gateway)
│   │   │   │   ├── services/
│   │   │   │   │   └── order.service.ts   ← POST order, GET order details
│   │   │   │   └── checkout-routing.module.ts
│   │   │   │
│   │   │   ├── customer-dashboard/
│   │   │   │   ├── pages/
│   │   │   │   │   ├── dashboard-page/    ← Overview: orders, reviews
│   │   │   │   │   ├── my-orders/         ← Order history with status
│   │   │   │   │   ├── my-reviews/        ← Posted reviews
│   │   │   │   │   └── my-addresses/      ← Address management
│   │   │   │   ├── components/
│   │   │   │   │   ├── order-list/        ← Table/Cards of orders
│   │   │   │   │   ├── order-status-badge/← Status pill (PENDING, SHIPPED, etc.)
│   │   │   │   │   └── review-form/       ← Submit new review
│   │   │   │   └── customer-dashboard-routing.module.ts
│   │   │   │
│   │   │   ├── seller-dashboard/
│   │   │   │   ├── pages/
│   │   │   │   │   ├── dashboard-page/    ← Stats: sales, products
│   │   │   │   │   ├── my-products/       ← Product CRUD
│   │   │   │   │   └── received-orders/   ← Orders containing seller's products
│   │   │   │   ├── components/
│   │   │   │   │   ├── product-form/      ← Create/Edit product
│   │   │   │   │   ├── stats-card/        ← Revenue, orders count
│   │   │   │   │   └── order-status-updater/ ← Change order status
│   │   │   │   └── seller-dashboard-routing.module.ts
│   │   │   │
│   │   │   └── admin-dashboard/
│   │   │       ├── pages/
│   │   │       │   ├── dashboard-page/    ← Global stats
│   │   │       │   ├── manage-categories/ ← CRUD categories
│   │   │       │   ├── manage-coupons/    ← CRUD coupons
│   │   │       │   └── manage-users/      ← Activate/deactivate users
│   │   │       └── admin-dashboard-routing.module.ts
│   │   │
│   │   ├── shared/                        ← Reusable components, pipes, directives
│   │   │   ├── components/
│   │   │   │   ├── header/                ← Navigation bar with cart icon
│   │   │   │   ├── footer/                ← Site footer
│   │   │   │   ├── spinner/               ← Loading spinner
│   │   │   │   ├── rating-stars/          ← Star display (1-5)
│   │   │   │   ├── pagination/            ← Page navigation component
│   │   │   │   └── modal/                 ← Generic modal dialog
│   │   │   ├── pipes/
│   │   │   │   ├── currency-format.pipe.ts ← Format prices (TND symbol)
│   │   │   │   └── date-ago.pipe.ts        ← Relative date (2 days ago)
│   │   │   └── directives/
│   │   │       └── lazy-load-image.directive.ts ← Image lazy loading
│   │   │
│   │   ├── app.component.ts               ← Root component
│   │   ├── app.component.html             ← <router-outlet> + header/footer
│   │   ├── app.routes.ts                  ← App routing config
│   │   └── app.config.ts                  ← provideHttpClient, etc.
│   │
│   ├── assets/
│   │   ├── images/
│   │   │   ├── logo.svg
│   │   │   ├── hero-banner.jpg
│   │   │   └── placeholder-product.png
│   │   └── styles/
│   │       └── variables.css              ← CSS custom properties (colors, fonts)
│   │
│   ├── environments/
│   │   ├── environment.ts                 ← Dev config (API URL: http://localhost:8080)
│   │   └── environment.prod.ts            ← Production config
│   │
│   ├── styles.css                         ← Global styles + Tailwind imports
│   ├── index.html
│   └── main.ts
│
├── angular.json                           ← Angular CLI config
├── tsconfig.json                          ← TypeScript config (strict mode)
├── tsconfig.app.json
├── package.json
└── README.md                              ← Install + run instructions
```

---

## 4. PAGE-BY-PAGE SPECIFICATIONS

### 4.1 Public Pages (No Auth Required)

#### A. Home Page (`/`)
**Purpose:** Landing page with marketing content + quick access to catalog

**Layout:**
```
+----------------------------------------------------------+
|  [Header: Logo | Search | Cart | Login/Register]         |
+----------------------------------------------------------+
|                                                          |
|  [Hero Banner]                                           |
|  Full-width promo image + CTA button                     |
|                                                          |
+----------------------------------------------------------+
|  Featured Categories                                     |
|  [Electronics] [Fashion] [Home] [Beauty] (4 cards)      |
+----------------------------------------------------------+
|  Top Selling Products                                    |
|  [Product Card] [Product Card] [Product Card] (Grid)    |
|  "View All" button →                                     |
+----------------------------------------------------------+
|  [Footer: Links | Social | Copyright]                    |
+----------------------------------------------------------+
```

**API Calls:**
- `GET /api/categories` (first 4-6 categories)
- `GET /api/products/top-selling?limit=8`

**State:**
- `categories$: Observable<Category[]>`
- `topProducts$: Observable<Product[]>`
- `loading: boolean`

---

#### B. Product Catalog Page (`/products`)
**Purpose:** Browse all products with filters + search + pagination

**Layout:**
```
+----------------------------------------------------------+
|  [Header]                                                |
+----------------------------------------------------------+
|  [Search Bar: "Search products..."]                     |
+----------------------------------------------------------+
|  FILTERS (Sidebar)      |  PRODUCT GRID                  |
|  ─────────────────────  |  ┌─────┐ ┌─────┐ ┌─────┐     |
|  Categories             |  │Img  │ │ Img  │ │ Img  │     |
|  ☐ Electronics          |  │ Name│ │ Name │ │ Name │     |
|  ☐ Fashion              |  │ $19 │ │ $29  │ │ $39  │     |
|  ☐ Home                 |  └─────┘ └─────┘ └─────┘     |
|                         |  ┌─────┐ ┌─────┐ ┌─────┐     |
|  Price Range            |  │ ... │ │ ...  │ │ ...  │     |
|  [Min] - [Max]          |  └─────┘ └─────┘ └─────┘     |
|                         |                                |
|  Rating                 |  [Pagination: < 1 2 3 4 >]     |
|  ⭐⭐⭐⭐⭐ & up           |                                |
|  ⭐⭐⭐⭐ & up             |                                |
+----------------------------------------------------------+
```

**API Calls:**
- `GET /api/products?page=0&size=12&category=&minPrice=&maxPrice=&minRating=`
- `GET /api/categories`

**State:**
- `products$: Observable<Product[]>`
- `filters: { category: string, minPrice: number, maxPrice: number, minRating: number }`
- `currentPage: number`
- `totalPages: number`

**Features:**
- Real-time filter updates (debounced)
- URL query params sync (`?category=electronics&page=2`)
- Empty state message when no results

---

#### C. Product Detail Page (`/products/:id`)
**Purpose:** Show product details + variants + reviews + add to cart

**Layout:**
```
+----------------------------------------------------------+
|  [Header]                                                |
+----------------------------------------------------------+
|  [Product Images]        |  Product Name                 |
|  ┌─────────────────┐     |  ⭐⭐⭐⭐ (4.2) - 24 reviews    |
|  │                 │     |                               |
|  │  Main Image     │     |  $49.99  [was $59.99]        |
|  │                 │     |                               |
|  └─────────────────┘     |  Select Variant:              |
|  [Thumb][Thumb][Thumb]   |  Size: [S] [M] [L]            |
|                          |  Color: [Red] [Blue]          |
|                          |                               |
|                          |  Quantity: [-] [1] [+]        |
|                          |  Stock: 15 available          |
|                          |                               |
|                          |  [Add to Cart Button]         |
|                          |                               |
+----------------------------------------------------------+
|  Description                                             |
|  Lorem ipsum dolor sit amet, consectetur adipiscing...   |
+----------------------------------------------------------+
|  Customer Reviews                                        |
|  ⭐⭐⭐⭐⭐ John D. - "Great product!"                      |
|  ⭐⭐⭐⭐ Sarah M. - "Good value for money"                |
|  [Show All Reviews]                                      |
+----------------------------------------------------------+
```

**API Calls:**
- `GET /api/products/:id`
- `GET /api/reviews/product/:id`

**State:**
- `product$: Observable<Product>`
- `reviews$: Observable<Review[]>`
- `selectedVariant: ProductVariant | null`
- `quantity: number`

**Interactions:**
- Variant selection updates price + stock
- Add to cart → POST `/api/cart/items`
- Image gallery (click thumbnail to change main image)

---

### 4.2 Authenticated Pages (Customer Role)

#### D. Cart Page (`/cart`)
**Purpose:** Review items, apply coupon, proceed to checkout

**Layout:**
```
+----------------------------------------------------------+
|  [Header]                                                |
+----------------------------------------------------------+
|  YOUR CART (3 items)                                     |
+----------------------------------------------------------+
|  [Product Image] Product Name                    $29.99  |
|  Size: M  |  Qty: [-][2][+]  |  [Remove]                |
+----------------------------------------------------------+
|  [Product Image] Another Product                 $19.99  |
|  Color: Blue  |  Qty: [-][1][+]  |  [Remove]            |
+----------------------------------------------------------+
|                                                          |
|  [Enter Coupon Code: _______] [Apply]                   |
|  Coupon applied: SAVE10 (-$5.00)                        |
|                                                          |
+----------------------------------------------------------+
|  Subtotal:           $49.98                              |
|  Shipping:           $5.00                               |
|  Discount (SAVE10):  -$5.00                              |
|  ─────────────────────────                               |
|  Total:              $49.98                              |
|                                                          |
|  [Continue Shopping]  [Proceed to Checkout →]           |
+----------------------------------------------------------+
```

**API Calls:**
- `GET /api/cart`
- `PUT /api/cart/items/:itemId` (update quantity)
- `DELETE /api/cart/items/:itemId` (remove item)
- `POST /api/cart/coupon` (apply coupon)
- `DELETE /api/cart/coupon` (remove coupon)

**State:**
- `cart$: Observable<Cart>`
- `loading: boolean`
- `couponCode: string`

**Business Rules (enforced by backend, displayed in UI):**
- Shipping: $5 if subtotal < $50, else free
- Coupon validation (expired, max uses, etc.)

---

#### E. Checkout Page (`/checkout`)
**Purpose:** Select address, review order, confirm purchase

**Layout:**
```
+----------------------------------------------------------+
|  [Header]                                                |
+----------------------------------------------------------+
|  CHECKOUT - Step 1: Delivery Address                     |
+----------------------------------------------------------+
|  Select Delivery Address:                                |
|  ● 123 Main St, Tunis 1000 (Default)                    |
|  ○ 456 Avenue Habib Bourguiba, Sfax 3000                |
|  [+ Add New Address]                                     |
+----------------------------------------------------------+
|  CHECKOUT - Step 2: Order Summary                        |
+----------------------------------------------------------+
|  [Product] x2  $59.98                                    |
|  [Product] x1  $19.99                                    |
|                                                          |
|  Subtotal:     $79.97                                    |
|  Shipping:     $0.00                                     |
|  Discount:     -$5.00                                    |
|  ─────────────────                                       |
|  Total:        $74.97                                    |
|                                                          |
|  [← Back to Cart]  [Confirm Order →]                    |
+----------------------------------------------------------+
```

**API Calls:**
- `POST /api/orders` (place order)

**Flow:**
1. User arrives from cart
2. Selects delivery address
3. Reviews order summary
4. Clicks "Confirm Order"
5. → Redirect to `/order-confirmation/:orderId`

---

#### F. Order Confirmation Page (`/order-confirmation/:orderId`)
**Purpose:** Success message + order details

**Layout:**
```
+----------------------------------------------------------+
|  ✓ Order Placed Successfully!                            |
|                                                          |
|  Order Number: ORD-2025-48291                            |
|  Estimated Delivery: Jan 20, 2025                        |
|                                                          |
|  [View Order Details]  [Continue Shopping]               |
+----------------------------------------------------------+
```

---

#### G. Customer Dashboard (`/dashboard`)
**Purpose:** Personal space — orders, reviews, addresses

**Tabs:**
1. **Overview:** Quick stats (orders count, pending reviews)
2. **My Orders:** Order history with status badges
3. **My Reviews:** Posted reviews + "Write Review" button
4. **Addresses:** CRUD address management

**Layout (My Orders tab):**
```
+----------------------------------------------------------+
|  MY ORDERS                                               |
+----------------------------------------------------------+
|  Order #ORD-2025-12345     Jan 15, 2025     DELIVERED    |
|  3 items  |  $89.97  |  [View Details]                   |
+----------------------------------------------------------+
|  Order #ORD-2025-12346     Jan 18, 2025     SHIPPED      |
|  1 item   |  $29.99  |  [Track] [Cancel]                |
+----------------------------------------------------------+
```

**API Calls:**
- `GET /api/orders/my`
- `GET /api/dashboard/customer` (stats)
- `PUT /api/orders/:id/cancel` (if PENDING/PAID)

---

### 4.3 Authenticated Pages (Seller Role)

#### H. Seller Dashboard (`/seller`)
**Purpose:** Manage products + view sales stats

**Tabs:**
1. **Overview:** Revenue, product count, recent orders
2. **My Products:** Product list with Edit/Delete
3. **Received Orders:** Orders containing seller's products (with status update)

**Layout (My Products tab):**
```
+----------------------------------------------------------+
|  MY PRODUCTS                       [+ Add New Product]   |
+----------------------------------------------------------+
|  [Image] Product Name      $49.99   Stock: 15   Active  |
|  [Edit] [Delete]                                         |
+----------------------------------------------------------+
|  [Image] Another Product   $29.99   Stock: 0    Inactive|
|  [Edit] [Delete]                                         |
+----------------------------------------------------------+
```

**API Calls:**
- `GET /api/dashboard/seller`
- `GET /api/products?sellerId=me` (seller's products)
- `POST /api/products` (create)
- `PUT /api/products/:id` (update)
- `DELETE /api/products/:id` (soft delete)

---

### 4.4 Authenticated Pages (Admin Role)

#### I. Admin Dashboard (`/admin`)
**Purpose:** Global stats + manage categories, coupons, users

**Tabs:**
1. **Overview:** Total revenue, user count, order count
2. **Categories:** CRUD tree view
3. **Coupons:** Active/Expired coupons with usage stats
4. **Users:** Activate/Deactivate user accounts
5. **Orders:** All orders with status management

**API Calls:**
- `GET /api/dashboard/admin`
- `GET /api/categories` (admin view)
- `POST /api/categories`
- `GET /api/coupons`
- `POST /api/coupons`

---

## 5. ROUTING ARCHITECTURE

### 5.1 Route Configuration (`app.routes.ts`)
```typescript
export const routes: Routes = [
  // Public routes
  { path: '', component: HomePageComponent },
  { path: 'products', component: ProductCatalogComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  
  // Auth routes (redirect if already logged in)
  {
    path: 'auth',
    canActivate: [GuestGuard],
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent }
    ]
  },
  
  // Customer routes
  {
    path: 'cart',
    canActivate: [AuthGuard],
    component: CartPageComponent
  },
  {
    path: 'checkout',
    canActivate: [AuthGuard],
    component: CheckoutPageComponent
  },
  {
    path: 'order-confirmation/:orderId',
    canActivate: [AuthGuard],
    component: OrderConfirmationComponent
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['CUSTOMER'] },
    loadChildren: () => import('./features/customer-dashboard/customer-dashboard.routes')
  },
  
  // Seller routes
  {
    path: 'seller',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['SELLER', 'ADMIN'] },
    loadChildren: () => import('./features/seller-dashboard/seller-dashboard.routes')
  },
  
  // Admin routes
  {
    path: 'admin',
    canActivate: [AuthGuard, RoleGuard],
    data: { roles: ['ADMIN'] },
    loadChildren: () => import('./features/admin-dashboard/admin-dashboard.routes')
  },
  
  // Fallback
  { path: '**', redirectTo: '' }
];
```

### 5.2 Guards Implementation Strategy
- **AuthGuard:** Check if `tokenService.getAccessToken()` exists, else redirect to `/auth/login`
- **RoleGuard:** Check `authService.currentUser$.role` against `route.data.roles`
- **GuestGuard:** If authenticated, redirect to role-specific dashboard

---

## 6. STATE MANAGEMENT STRATEGY

### 6.1 Service-Based Reactive State (No NgRx)
**Pattern:** Each service exposes observables for components to subscribe to.

**Example: AuthService**
```typescript
@Injectable({ providedIn: 'root' })
export class AuthService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();
  
  login(credentials: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>('/api/auth/login', credentials).pipe(
      tap(response => {
        this.tokenService.setTokens(response.access_token, response.refresh_token);
        this.loadUserProfile(); // Decode JWT and set currentUser
      })
    );
  }
  
  private loadUserProfile(): void {
    const token = this.tokenService.getAccessToken();
    if (token) {
      const decoded = this.jwtHelper.decodeToken(token);
      this.currentUserSubject.next({
        id: decoded.userId,
        email: decoded.sub,
        role: decoded.role
      });
    }
  }
}
```

**Example: CartService**
```typescript
@Injectable({ providedIn: 'root' })
export class CartService {
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  public cart$ = this.cartSubject.asObservable();
  
  public cartItemCount$ = this.cart$.pipe(
    map(cart => cart?.items.reduce((sum, item) => sum + item.quantite, 0) || 0)
  );
  
  loadCart(): void {
    this.http.get<Cart>('/api/cart').subscribe(cart => {
      this.cartSubject.next(cart);
    });
  }
  
  addItem(productId: number, variantId?: number, quantity = 1): Observable<Cart> {
    return this.http.post<Cart>('/api/cart/items', { productId, variantId, quantity }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }
}
```

### 6.2 Component Communication
- **Smart Components:** Inject services, handle logic, pass data to dumb components via @Input
- **Dumb Components:** Receive data via @Input, emit events via @Output, no service injection

---

## 7. HTTP INTERCEPTORS

### 7.1 JWT Interceptor
**Purpose:** Inject Bearer token in every request to protected endpoints

```typescript
@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private tokenService: TokenService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.tokenService.getAccessToken();
    if (token && !req.url.includes('/auth/login')) {
      req = req.clone({
        setHeaders: { Authorization: `Bearer ${token}` }
      });
    }
    return next.handle(req);
  }
}
```

### 7.2 Error Interceptor
**Purpose:** Handle 401 errors by refreshing token, then retry original request

```typescript
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(
    private authService: AuthService,
    private tokenService: TokenService,
    private router: Router
  ) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !req.url.includes('/auth/refresh')) {
          // Try to refresh token
          return this.authService.refreshToken().pipe(
            switchMap(() => {
              // Retry original request with new token
              const newToken = this.tokenService.getAccessToken();
              const clonedReq = req.clone({
                setHeaders: { Authorization: `Bearer ${newToken}` }
              });
              return next.handle(clonedReq);
            }),
            catchError(() => {
              // Refresh failed → logout
              this.authService.logout();
              this.router.navigate(['/auth/login']);
              return throwError(() => error);
            })
          );
        }
        return throwError(() => error);
      })
    );
  }
}
```

---

## 8. DESIGN SYSTEM & UI/UX GUIDELINES

### 8.1 Color Palette (Tailwind CSS Variables)
```css
/* src/styles.css */
@tailwind base;
@tailwind components;
@tailwind utilities;

@layer base {
  :root {
    --primary: 37 99 235;      /* Blue-600 */
    --primary-dark: 29 78 216; /* Blue-700 */
    --secondary: 107 114 128;  /* Gray-500 */
    --success: 34 197 94;      /* Green-500 */
    --danger: 239 68 68;       /* Red-500 */
    --warning: 251 146 60;     /* Orange-400 */
    --background: 255 255 255; /* White */
    --text: 17 24 39;          /* Gray-900 */
    --border: 229 231 235;     /* Gray-200 */
  }
}
```

### 8.2 Typography
- **Font:** System font stack (sans-serif)
  ```css
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  ```
- **Headings:** `text-2xl font-bold` (h1), `text-xl font-semibold` (h2)
- **Body:** `text-base text-gray-700`

### 8.3 Component Design Principles
1. **Consistency:** Same spacing (p-4, mb-4), same button styles
2. **Feedback:** Loading spinners, success toasts, error messages
3. **Accessibility:** Semantic HTML, aria labels, keyboard navigation
4. **Mobile-first:** Design for small screens, progressively enhance

### 8.4 Reusable Component Library
- **Button:** Primary, secondary, danger variants
- **Card:** Product card, order card, stat card
- **Badge:** Status badges (PENDING → yellow, SHIPPED → blue, DELIVERED → green)
- **Modal:** Confirm actions (delete product, cancel order)
- **Toast:** Global notification system (top-right corner)

---

## 9. API INTEGRATION CHECKLIST

### 9.1 Environment Configuration
```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  apiPrefix: '/api'
};
```

### 9.2 HTTP Service Base
All feature services extend a base class for consistency:
```typescript
@Injectable()
export abstract class BaseApiService {
  protected apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;
  
  constructor(protected http: HttpClient) {}
  
  protected handleError(error: HttpErrorResponse): Observable<never> {
    console.error('API Error:', error);
    return throwError(() => new Error(error.message));
  }
}
```

### 9.3 API Endpoints Mapping (matches backend)
| Backend Endpoint | Frontend Service Method |
|---|---|
| `POST /api/auth/register` | `authService.register(data)` |
| `POST /api/auth/login` | `authService.login(credentials)` |
| `POST /api/auth/refresh` | `authService.refreshToken()` |
| `GET /api/products?page=0` | `productService.getProducts(filters)` |
| `GET /api/products/:id` | `productService.getProductById(id)` |
| `POST /api/cart/items` | `cartService.addItem(productId, qty)` |
| `POST /api/orders` | `orderService.placeOrder(addressId)` |
| `GET /api/orders/my` | `orderService.getMyOrders()` |
| `PUT /api/orders/:id/cancel` | `orderService.cancelOrder(id)` |

---

## 10. RESPONSIVE DESIGN BREAKPOINTS

### 10.1 Mobile-First Strategy
```css
/* Base styles for mobile (< 640px) */
.product-grid { grid-template-columns: 1fr; }

/* Small devices (640px+) */
@media (min-width: 640px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}

/* Medium devices (768px+) */
@media (min-width: 768px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}

/* Large devices (1024px+) */
@media (min-width: 1024px) {
  .product-grid { grid-template-columns: repeat(4, 1fr); }
}
```

### 10.2 Key Responsive Patterns
- **Header:** Hamburger menu on mobile, full nav on desktop
- **Filters:** Slide-out drawer on mobile, sidebar on desktop
- **Product Grid:** 1 col mobile → 2 cols tablet → 4 cols desktop
- **Forms:** Stack inputs on mobile, side-by-side on desktop

---

## 11. TESTING STRATEGY (Bonus Points)

### 11.1 Unit Tests (Karma + Jasmine)
- **Services:** Mock HttpClient, test API calls
- **Components:** Mock services, test @Input/@Output
- **Guards:** Test route access based on auth state

### 11.2 E2E Tests (Optional — Cypress)
- Full user flow: Register → Browse → Add to Cart → Checkout → Order

---

## 12. DEVELOPMENT WORKFLOW

### 12.1 Phase-by-Phase Implementation (3 weeks)

#### Week 1: Setup + Core Infrastructure
- [x] Day 1-2: `ng new shopflow-frontend` + Tailwind setup + folder structure
- [x] Day 3-4: Core services (auth, token, HTTP interceptors)
- [x] Day 5: Guards (auth, role, guest) + routing skeleton
- [x] Day 6-7: Shared components (header, footer, spinner, rating stars)

#### Week 2: Feature Development
- [ ] Day 8-9: Home page + Product catalog + Product detail
- [ ] Day 10-11: Cart page + Checkout flow + Order confirmation
- [ ] Day 12-13: Customer dashboard (orders + reviews)
- [ ] Day 14: Seller dashboard (products CRUD)

#### Week 3: Polish + Admin + Testing
- [ ] Day 15-16: Admin dashboard (categories + coupons)
- [ ] Day 17: Responsive design fixes + UX polish
- [ ] Day 18: Testing + bug fixes
- [ ] Day 19: Documentation (README.md) + final testing
- [ ] Day 20: Demo preparation + rapport.pdf frontend section

### 12.2 Git Workflow (matches backend)
```bash
git checkout develop
git checkout -b feature/frontend-auth
# code + test
git add .
git commit -m "feat(auth): implement login and register pages"
git checkout develop
git merge feature/frontend-auth
git branch -d feature/frontend-auth
```

### 12.3 Daily Checklist
- [ ] Run `ng serve` → app compiles without errors
- [ ] Test in Chrome DevTools mobile view (iPhone SE, iPad)
- [ ] Check console for errors
- [ ] Verify API calls in Network tab
- [ ] Push to `develop` branch if feature complete

---

## 13. COMMON PITFALLS & SOLUTIONS

| Problem | Solution |
|---|---|
| CORS error in browser | Backend must allow `http://localhost:4200` in CORS config |
| 401 after login | JWT interceptor not registered in `app.config.ts` providers |
| Refresh token loop | Check if `/auth/refresh` is excluded from interceptor |
| Product images not loading | Use placeholder image for missing `imageUrl` |
| Cart count not updating | CartService not emitting new value after add/remove |
| Lazy-loaded module not loading | Check `loadChildren` path in routes |
| Form validation not working | Ensure `ReactiveFormsModule` imported in component |
| Tailwind classes not applying | Run `ng build` to regenerate CSS, check `tailwind.config.js` |

---

## 14. BONUS FEATURES (for extra points)

- [ ] **Dark mode:** Toggle with `localStorage` persistence
- [ ] **PWA:** Add service worker for offline support
- [ ] **Animations:** Page transitions with Angular Animations API
- [ ] **Image zoom:** Product detail page image magnifier
- [ ] **Real-time search:** Autocomplete with debounced API calls
- [ ] **Wishlist:** Save products for later (requires new backend endpoint)
- [ ] **Toasts:** Global notification system with auto-dismiss
- [ ] **Skeleton loaders:** Better loading UX than spinners

---

## 15. DELIVERABLES CHECKLIST

### 15.1 Code
- [ ] Clean `node_modules/` and `.angular/` before zipping
- [ ] All components have `.ts`, `.html`, `.css` files
- [ ] No console.log statements in production code
- [ ] All API URLs use `environment.apiUrl`

### 15.2 Documentation
- [ ] `README.md` with:
  - Prerequisites (Node 18+, Angular CLI 17+)
  - Install: `npm install`
  - Run: `ng serve`
  - Build: `ng build --configuration production`
  - Deployed URL (if hosted)
- [ ] `rapport.pdf` includes:
  - Screenshot of each page
  - Architecture diagram (services + components + guards)
  - Justification for Angular vs Next.js choice
  - Challenges faced (e.g., refresh token flow, lazy loading)

### 15.3 Demo Preparation
- [ ] Test full flow: Register → Login → Browse → Add to Cart → Checkout → View Order
- [ ] Test seller flow: Login → Add Product → View in Catalog
- [ ] Test admin flow: Login → Create Category → View Dashboard
- [ ] Prepare demo account credentials:
  - Customer: `customer@test.com` / `password123`
  - Seller: `seller@test.com` / `password123`
  - Admin: `admin@test.com` / `password123`

---

## 16. FINAL ARCHITECTURE SUMMARY

```
┌─────────────────────────────────────────────────────────────┐
│                     ANGULAR FRONTEND                        │
├─────────────────────────────────────────────────────────────┤
│  Browser (http://localhost:4200)                            │
│    ↓                                                         │
│  App Component (router-outlet)                              │
│    ↓                                                         │
│  Guards (auth, role) check before route activation          │
│    ↓                                                         │
│  Page Components (smart — inject services)                  │
│    ↓                                                         │
│  Feature Services (HTTP calls via HttpClient)               │
│    ↓                                                         │
│  HTTP Interceptors (JWT injection, error handling)          │
│    ↓                                                         │
│  Spring Boot Backend (http://localhost:8080/api)            │
│    ↓                                                         │
│  PostgreSQL Database                                        │
└─────────────────────────────────────────────────────────────┘
```

---

## 17. RESOURCES & REFERENCES

### 17.1 Official Documentation
- Angular 17: https://angular.dev
- Tailwind CSS: https://tailwindcss.com/docs
- RxJS: https://rxjs.dev

### 17.2 Tutorial Videos (if needed)
- Angular 17 Standalone Components: YouTube search "Angular 17 tutorial"
- JWT Auth in Angular: YouTube search "Angular JWT interceptor"

### 17.3 UI Inspiration
- Dribbble: E-commerce dashboard designs
- Behance: E-commerce product pages
- Real-world examples: Jumia, Amazon, Tunisianet (local)

---

## END OF FRONTEND PLAN

**Next Steps:**
1. Review this plan with the team
2. Initialize Angular project: `ng new shopflow-frontend`
3. Install Tailwind CSS: `npm install -D tailwindcss postcss autoprefixer`
4. Create folder structure as defined in Section 3
5. Start with Week 1 tasks (auth + core services)

**Questions to resolve before starting:**
- Do we need Arabic/French/English i18n support?
- Should we use Angular Material or build custom components?
- Payment gateway: mock only or integrate real API (e.g., Flouci)?

---

**Author:** Senior Frontend Architect (based on CLAUDE.md + MiniProjet_ShopFlow.pdf)  
**Date:** April 9, 2026  
**Version:** 1.0
