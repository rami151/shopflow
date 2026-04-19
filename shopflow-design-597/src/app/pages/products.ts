import { Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { RouterLink } from "@angular/router";

interface Product {
  id: number;
  name: string;
  price: number;
  originalPrice?: number;
  category: string;
  rating: number;
  reviews: number;
  badge?: string;
}

@Component({
  selector: "app-products",
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './products.html',
  styles: [],
})
export class ProductsComponent {
  searchQuery = "";
  sortBy = "newest";
  minPrice = 0;
  maxPrice = 1000;

  categories = ["Electronics", "Fashion", "Home", "Sports", "Beauty", "Books"];
  selectedCategories: { [key: string]: boolean } = {};

  selectedRatings: { [key: number]: boolean } = {};

  products: Product[] = [
    { id: 1, name: "Premium Wireless Headphones", price: 129.99, originalPrice: 199.99, category: "Electronics", rating: 5, reviews: 342, badge: "Best Seller" },
    { id: 2, name: "Ultra Soft Cotton T-Shirt", price: 24.99, category: "Fashion", rating: 4, reviews: 156, badge: "New" },
    { id: 3, name: "Modern LED Desk Lamp", price: 45.50, originalPrice: 69.99, category: "Home", rating: 5, reviews: 289 },
    { id: 4, name: "Professional Yoga Mat", price: 35.99, category: "Sports", rating: 4, reviews: 178, badge: "Top Rated" },
    { id: 5, name: "Organic Face Cream", price: 42.00, originalPrice: 65.00, category: "Beauty", rating: 5, reviews: 421 },
    { id: 6, name: "Latest Tech Novel", price: 18.50, category: "Books", rating: 4, reviews: 89 },
    { id: 7, name: "Stainless Steel Water Bottle", price: 32.00, category: "Sports", rating: 4, reviews: 234 },
    { id: 8, name: "Wireless Charging Pad", price: 29.99, originalPrice: 49.99, category: "Electronics", rating: 4, reviews: 198, badge: "Sale" },
    { id: 9, name: "Comfort Running Shoes", price: 89.99, category: "Fashion", rating: 5, reviews: 567 },
    { id: 10, name: "Smart Coffee Maker", price: 75.00, category: "Home", rating: 4, reviews: 145 },
    { id: 11, name: "Portable Speaker", price: 59.99, originalPrice: 99.99, category: "Electronics", rating: 5, reviews: 312 },
    { id: 12, name: "Premium Skincare Set", price: 89.99, category: "Beauty", rating: 5, reviews: 534 },
  ];

  filteredProducts = signal<Product[]>(this.products);

  constructor() {
    this.updateFilters();
  }

  updateFilters() {
    let filtered = this.products.filter((p) => {
      const matchesSearch = p.name.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchesPrice = p.price >= this.minPrice && p.price <= this.maxPrice;
      const matchesCategory = Object.keys(this.selectedCategories).length === 0 || this.selectedCategories[p.category];
      const matchesRating = Object.keys(this.selectedRatings).length === 0 || this.selectedRatings[p.rating];

      return matchesSearch && matchesPrice && matchesCategory && matchesRating;
    });

    if (this.sortBy === "price-low") {
      filtered.sort((a, b) => a.price - b.price);
    } else if (this.sortBy === "price-high") {
      filtered.sort((a, b) => b.price - a.price);
    } else if (this.sortBy === "rating") {
      filtered.sort((a, b) => b.rating - a.rating || b.reviews - a.reviews);
    } else if (this.sortBy === "popular") {
      filtered.sort((a, b) => b.reviews - a.reviews);
    }

    this.filteredProducts.set(filtered);
  }

  clearFilters() {
    this.searchQuery = "";
    this.minPrice = 0;
    this.maxPrice = 1000;
    this.selectedCategories = {};
    this.selectedRatings = {};
    this.sortBy = "newest";
    this.updateFilters();
  }
}
