#!/usr/bin/env python3
"""ServiceHub SaaS: aplicación orientada a servicios sin dependencias externas."""

from http import HTTPStatus
from http.server import SimpleHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import urlparse
import json


ROOT = Path(__file__).resolve().parent
PUBLIC = ROOT / "public"

PLANS = [
    {"id": "starter", "name": "Starter", "price": 299, "users": 5, "status": "Disponible"},
    {"id": "business", "name": "Business", "price": 699, "users": 20, "status": "Más elegido"},
    {"id": "scale", "name": "Scale", "price": 1299, "users": 60, "status": "Disponible"},
]

CUSTOMERS = [
    {"id": 101, "name": "Café Horizonte", "plan": "Business", "status": "Activo"},
    {"id": 102, "name": "Estudio Norte", "plan": "Starter", "status": "Activo"},
    {"id": 103, "name": "Logística Poblana", "plan": "Scale", "status": "Prueba"},
    {"id": 104, "name": "Clínica Arboleda", "plan": "Business", "status": "Activo"},
]

SUBSCRIPTIONS = [
    {"id": "SUB-001", "customer": "Café Horizonte", "plan": "Business", "renewal": "15 oct 2026"},
    {"id": "SUB-002", "customer": "Estudio Norte", "plan": "Starter", "renewal": "22 oct 2026"},
    {"id": "SUB-003", "customer": "Logística Poblana", "plan": "Scale", "renewal": "30 sep 2026"},
]


class ServiceHubHandler(SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=str(PUBLIC), **kwargs)

    def _json(self, payload, status=HTTPStatus.OK):
        content = json.dumps(payload, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(content)))
        self.send_header("Cache-Control", "no-store")
        self.end_headers()
        self.wfile.write(content)

    def do_GET(self):
        route = urlparse(self.path).path
        if route == "/api/summary":
            return self._json({
                "monthlyRevenue": 18450,
                "activeCustomers": sum(c["status"] == "Activo" for c in CUSTOMERS),
                "servicesOnline": 4,
                "availability": 99.9,
            })
        if route == "/api/plans":
            return self._json(PLANS)
        if route == "/api/customers":
            return self._json(CUSTOMERS)
        if route == "/api/subscriptions":
            return self._json(SUBSCRIPTIONS)
        if route == "/api/health":
            return self._json({"service": "ServiceHub API", "status": "ok", "version": "1.0"})
        return super().do_GET()

    def log_message(self, message, *args):
        print(f"[ServiceHub] {self.address_string()} - {message % args}")


if __name__ == "__main__":
    address = ("127.0.0.1", 8060)
    print("ServiceHub disponible en http://127.0.0.1:8060")
    ThreadingHTTPServer(address, ServiceHubHandler).serve_forever()
