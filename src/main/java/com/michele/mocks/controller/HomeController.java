package com.michele.mocks.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> home() {
        String html = """
                <!DOCTYPE html>
                <html lang=\"en\">
                <head>
                    <meta charset=\"UTF-8\" />
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
                    <title>SeedCraft Backend</title>
                    <style>
                        :root {
                            color-scheme: light dark;
                            --bg: #0b1220;
                            --card: #131c2e;
                            --text: #e6edf7;
                            --muted: #a9b7cc;
                            --accent: #5aa9ff;
                            --ok: #66d18f;
                        }
                        body {
                            margin: 0;
                            font-family: Inter, Arial, sans-serif;
                            background: linear-gradient(180deg, #0b1220 0%, #101a2b 100%);
                            color: var(--text);
                        }
                        .container {
                            max-width: 980px;
                            margin: 0 auto;
                            padding: 40px 20px 60px;
                        }
                        .badge {
                            display: inline-block;
                            padding: 6px 10px;
                            border-radius: 999px;
                            background: rgba(102, 209, 143, 0.15);
                            color: var(--ok);
                            font-size: 0.85rem;
                            margin-bottom: 16px;
                        }
                        h1 { font-size: 2.2rem; margin: 0 0 10px; }
                        p { color: var(--muted); line-height: 1.6; }
                        .actions { margin: 22px 0 28px; display: flex; gap: 12px; flex-wrap: wrap; }
                        a.button {
                            text-decoration: none;
                            color: #fff;
                            background: var(--accent);
                            padding: 10px 14px;
                            border-radius: 10px;
                            font-weight: 600;
                        }
                        a.link {
                            text-decoration: none;
                            color: var(--text);
                            border: 1px solid #2b3a54;
                            padding: 10px 14px;
                            border-radius: 10px;
                        }
                        .grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
                            gap: 14px;
                        }
                        .card {
                            background: rgba(19, 28, 46, 0.9);
                            border: 1px solid #24314a;
                            border-radius: 12px;
                            padding: 16px;
                        }
                        code {
                            background: #0f1727;
                            border: 1px solid #27354f;
                            padding: 2px 6px;
                            border-radius: 6px;
                            color: #b7d4ff;
                        }
                        ul { margin: 8px 0 0; padding-left: 18px; color: var(--muted); }
                        li { margin: 6px 0; }
                    </style>
                </head>
                <body>
                    <main class=\"container\">
                        <span class=\"badge\">SeedCraft Backend · Spring Boot API</span>
                        <h1>Inventory, Catalog, and Dashboard APIs</h1>
                        <p>
                            SeedCraft Backend powers product/category management and inventory dashboard data.
                            It exposes REST endpoints under <code>/api/v1</code> and provides Swagger docs for quick exploration.
                        </p>

                        <div class=\"actions\">
                            <a class=\"button\" href=\"/swagger-ui/index.html\">Open Swagger UI</a>
                            <a class=\"link\" href=\"/api/v1/health\">Health Check</a>
                            <a class=\"link\" href=\"/v3/api-docs\">OpenAPI JSON</a>
                        </div>

                        <section class=\"grid\">
                            <article class=\"card\">
                                <h3>What this project provides</h3>
                                <ul>
                                    <li>CRUD and search APIs for products and categories.</li>
                                    <li>Category tree APIs for hierarchical browsing.</li>
                                    <li>Dashboard-ready inventory data sections for frontend charts/tables.</li>
                                </ul>
                            </article>

                            <article class=\"card\">
                                <h3>Core API areas</h3>
                                <ul>
                                    <li><code>/api/v1/products</code></li>
                                    <li><code>/api/v1/categories</code></li>
                                    <li><code>/api/v1/inventory/dashboard</code></li>
                                </ul>
                            </article>

                            <article class=\"card\">
                                <h3>How to use quickly</h3>
                                <ul>
                                    <li>Run the service and open Swagger UI.</li>
                                    <li>Call <code>/api/v1/health</code> to verify uptime.</li>
                                    <li>Create categories/products, then test dashboard endpoints.</li>
                                </ul>
                            </article>
                        </section>
                    </main>
                </body>
                </html>
                """;

        return ResponseEntity.ok(html);
    }
}
