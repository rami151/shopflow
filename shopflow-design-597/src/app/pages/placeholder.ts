import { Component, OnInit } from "@angular/core";
import { RouterLink, ActivatedRoute } from "@angular/router";

@Component({
  selector: "app-placeholder",
  standalone: true,
  imports: [RouterLink],
  template: `
    <section class="min-h-screen bg-gradient-to-br from-dark-50 to-dark-100 flex items-center justify-center px-4">
      <div class="text-center max-w-md">
        <div class="text-8xl mb-6">{{ icon }}</div>
        <h1 class="text-4xl font-bold text-dark-900 mb-4">{{ title }}</h1>
        <p class="text-lg text-dark-600 mb-8">{{ description }}</p>
        <p class="text-dark-500 mb-8">This page is ready to be built out with full functionality. Feel free to prompt me to add features to this page!</p>
        <a routerLink="/" class="inline-block px-8 py-3 bg-primary-600 hover:bg-primary-700 text-white font-semibold rounded-lg transition">
          Back to Home
        </a>
      </div>
    </section>
  `,
})
export class PlaceholderComponent implements OnInit {
  icon = "🚀";
  title = "Coming Soon";
  description = "This page is being built";

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.route.data.subscribe((data) => {
      this.icon = data["icon"] || "🚀";
      this.title = data["title"] || "Coming Soon";
      this.description = data["description"] || "This page is being built";
    });
  }
}
